import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import { describe, it, beforeAll, afterAll, beforeEach } from 'vitest';
import {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} from '@firebase/rules-unit-testing';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const PROJECT_ID = 'demo-rakcha';

describe('Firestore Security Rules', () => {
  let testEnv;

  beforeAll(async () => {
    const rules = fs.readFileSync(path.resolve(__dirname, '../firestore.rules'), 'utf8');
    testEnv = await initializeTestEnvironment({
      projectId: PROJECT_ID,
      firestore: { rules, host: '127.0.0.1', port: 8080 },
    });
  });

  afterAll(async () => {
    if (testEnv) await testEnv.cleanup();
  });

  beforeEach(async () => {
    if (testEnv) await testEnv.clearFirestore();
  });

  // Roles live on the user's own Firestore document. Seed them with rules
  // disabled, as a trusted backend would.
  async function seedUser(uid, role) {
    await testEnv.withSecurityRulesDisabled(async (context) => {
      await context.firestore().collection('users').doc(uid).set({
        uid, role, email: `${uid}@example.com`, display_name: uid,
      });
    });
  }

  describe('Catalogue collections', () => {
    it('allows anyone to read the public catalogue', async () => {
      await testEnv.withSecurityRulesDisabled(async (context) => {
        await context.firestore().collection('Film').doc('film-1').set({ name: 'Interstellar' });
      });
      const db = testEnv.unauthenticatedContext().firestore();
      await assertSucceeds(db.collection('Film').doc('film-1').get());
    });

    it('denies unauthenticated writes to the catalogue', async () => {
      const db = testEnv.unauthenticatedContext().firestore();
      await assertFails(db.collection('Film').doc('film-2').set({ name: 'Inception' }));
      await assertFails(db.collection('cinema').doc('c1').set({ nom: 'Palace' }));
    });

    it('allows only managers to write Film and Seance', async () => {
      await seedUser('alice', 'client');
      await seedUser('manager', 'manager');

      await assertFails(testEnv.authenticatedContext('alice').firestore()
        .collection('Film').doc('film-2').set({ name: 'Inception' }));
      await assertSucceeds(testEnv.authenticatedContext('manager').firestore()
        .collection('Film').doc('film-2').set({ name: 'Inception' }));
    });
  });

  describe('User profiles', () => {
    it('denies unauthenticated profile access', async () => {
      const db = testEnv.unauthenticatedContext().firestore();
      await assertFails(db.collection('users').doc('alice').get());
      await assertFails(db.collection('users').doc('alice').set({ display_name: 'Alice' }));
    });

    it('allows a user to create only their own client profile', async () => {
      const alice = testEnv.authenticatedContext('alice').firestore();
      await assertSucceeds(alice.collection('users').doc('alice').set({
        uid: 'alice', email: 'alice@example.com', display_name: 'Alice', role: 'client',
      }));
      await assertFails(alice.collection('users').doc('bob').set({
        uid: 'bob', email: 'bob@example.com', display_name: 'Bob', role: 'client',
      }));
    });

    it('does not let a user assign themselves a staff role', async () => {
      const alice = testEnv.authenticatedContext('alice').firestore();
      await assertFails(alice.collection('users').doc('alice').set({
        uid: 'alice', email: 'alice@example.com', display_name: 'Alice', role: 'admin',
      }));
    });

    it('protects other users profiles from ordinary users', async () => {
      await seedUser('alice', 'client');
      await seedUser('bob', 'client');
      await seedUser('admin', 'admin');

      await assertSucceeds(testEnv.authenticatedContext('alice').firestore()
        .collection('users').doc('alice').get());
      await assertFails(testEnv.authenticatedContext('alice').firestore()
        .collection('users').doc('bob').get());
      await assertSucceeds(testEnv.authenticatedContext('admin').firestore()
        .collection('users').doc('bob').get());
    });

    it('allows users to update their own non-role fields only', async () => {
      await seedUser('alice', 'client');
      await seedUser('bob', 'client');
      const alice = testEnv.authenticatedContext('alice').firestore();
      await assertSucceeds(alice.collection('users').doc('alice').update({ display_name: 'Alice Two' }));
      await assertFails(alice.collection('users').doc('alice').update({ role: 'admin' }));
      await assertFails(alice.collection('users').doc('bob').update({ display_name: 'Hacked' }));
      await assertFails(alice.collection('users').doc('bob').delete());
    });
  });

  describe('Basket (users/{uid}/cart)', () => {
    const item = {
      productId: 'p1', name: 'Sculpture', image: 'assets/images/product-sculpture.jpg',
      price: 85, quantity: 1,
    };

    it('denies unauthenticated basket access', async () => {
      const db = testEnv.unauthenticatedContext().firestore();
      await assertFails(db.doc('users/alice/cart/p1').get());
      await assertFails(db.doc('users/alice/cart/p1').set(item));
    });

    it('allows a user to manage their own basket and denies other users', async () => {
      const alice = testEnv.authenticatedContext('alice').firestore();
      const bob = testEnv.authenticatedContext('bob').firestore();

      await assertSucceeds(alice.doc('users/alice/cart/p1').set(item));
      await assertSucceeds(alice.doc('users/alice/cart/p1').update({ quantity: 3 }));
      await assertFails(alice.doc('users/bob/cart/p1').set(item));
      await assertFails(bob.doc('users/alice/cart/p1').get());
      await assertFails(bob.doc('users/alice/cart/p1').delete());
    });

    it('rejects invalid quantities', async () => {
      const alice = testEnv.authenticatedContext('alice').firestore();
      await assertFails(alice.doc('users/alice/cart/p1').set({ ...item, quantity: 0 }));
      await assertFails(alice.doc('users/alice/cart/p1').set({ ...item, quantity: 100 }));
    });
  });

  describe('Reservation isolation', () => {
    const reservation = {
      user_ref: null, filmId: 'f1', filmName: 'Film', seanceId: 's1',
      seats: ['A1'], total: 16,
    };

    it('denies unauthenticated reservation access', async () => {
      const db = testEnv.unauthenticatedContext().firestore();
      await assertFails(db.collection('Reservation').doc('r1').get());
      await assertFails(db.collection('Reservation').doc('r1').set(reservation));
    });

    it('allows an owner to create and read a valid request, denies others', async () => {
      const alice = testEnv.authenticatedContext('alice').firestore();
      const data = { ...reservation, user_ref: alice.doc('users/alice') };
      await assertSucceeds(alice.collection('Reservation').doc('r-alice').set(data));
      await assertSucceeds(alice.collection('Reservation').doc('r-alice').get());

      const bob = testEnv.authenticatedContext('bob').firestore();
      await assertFails(bob.collection('Reservation').doc('r-alice').get());
      await assertFails(bob.collection('Reservation').doc('r-alice').delete());
      await assertFails(alice.collection('Reservation').doc('r-alice').set({
        ...data, user_ref: alice.doc('users/bob'),
      }));
    });

    it('rejects malformed reservation documents', async () => {
      const alice = testEnv.authenticatedContext('alice').firestore();
      await assertFails(alice.collection('Reservation').doc('r-bad').set({
        user_ref: alice.doc('users/alice'), seats: ['A1'], total: 16,
      }));
    });
  });

  describe('Cinema approvals', () => {
    it('allows a client to propose a pending cinema but not to approve it', async () => {
      await seedUser('alice', 'client');
      const alice = testEnv.authenticatedContext('alice').firestore();
      const venue = {
        nom: 'Palace', address: 'Tunis', responsable: 'alice',
        statut: 'Pending', image_url: 'assets/images/palace.jpg', description: 'New venue',
      };
      await assertSucceeds(alice.collection('cinema').doc('c5').set(venue));
      await assertFails(alice.collection('cinema').doc('c5').update({ statut: 'Accepted' }));
    });

    it('allows an admin to approve a cinema', async () => {
      await seedUser('admin', 'admin');
      await testEnv.withSecurityRulesDisabled(async (context) => {
        await context.firestore().collection('cinema').doc('c5').set({
          nom: 'Palace', address: 'Tunis', responsable: 'alice',
          statut: 'Pending', image_url: 'assets/images/palace.jpg', description: 'New venue',
        });
      });
      const admin = testEnv.authenticatedContext('admin').firestore();
      await assertSucceeds(admin.collection('cinema').doc('c5').update({ statut: 'Accepted' }));
    });
  });
});
