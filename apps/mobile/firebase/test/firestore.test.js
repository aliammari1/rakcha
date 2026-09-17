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
      firestore: {
        rules,
        host: '127.0.0.1',
        port: 8080,
      },
    });
  });

  afterAll(async () => {
    if (testEnv) {
      await testEnv.cleanup();
    }
  });

  beforeEach(async () => {
    if (testEnv) {
      await testEnv.clearFirestore();
    }
  });

  describe('Catalog Collections (Film, cinema, produit, etc.)', () => {
    it('allows unauthenticated users to read catalog documents', async () => {
      await testEnv.withSecurityRulesDisabled(async (context) => {
        const adminDb = context.firestore();
        await adminDb.collection('Film').doc('film-1').set({ title: 'Interstellar' });
      });

      const unauthDb = testEnv.unauthenticatedContext().firestore();
      await assertSucceeds(unauthDb.collection('Film').doc('film-1').get());
    });

    it('denies unauthenticated users from creating catalog documents', async () => {
      const unauthDb = testEnv.unauthenticatedContext().firestore();
      await assertFails(unauthDb.collection('Film').doc('film-2').set({ title: 'Inception' }));
      await assertFails(unauthDb.collection('cinema').doc('cinema-1').set({ name: 'Palace' }));
    });

    it('allows authenticated users to create catalog documents', async () => {
      const aliceDb = testEnv.authenticatedContext('alice').firestore();
      await assertSucceeds(aliceDb.collection('Film').doc('film-2').set({ title: 'Inception' }));
    });
  });

  describe('User Profiles (/users/{userId})', () => {
    it('denies unauthenticated users from reading or creating profiles', async () => {
      const unauthDb = testEnv.unauthenticatedContext().firestore();
      await assertFails(unauthDb.collection('users').doc('alice').get());
      await assertFails(unauthDb.collection('users').doc('alice').set({ name: 'Alice' }));
    });

    it('allows authenticated user to create their own profile matching uid', async () => {
      const aliceDb = testEnv.authenticatedContext('alice').firestore();
      await assertSucceeds(aliceDb.collection('users').doc('alice').set({ name: 'Alice', email: 'alice@example.com' }));
    });

    it('denies authenticated user from creating another user profile', async () => {
      const aliceDb = testEnv.authenticatedContext('alice').firestore();
      await assertFails(aliceDb.collection('users').doc('bob').set({ name: 'Bob', email: 'bob@example.com' }));
    });

    it('allows authenticated users to view profiles', async () => {
      await testEnv.withSecurityRulesDisabled(async (context) => {
        const adminDb = context.firestore();
        await adminDb.collection('users').doc('bob').set({ name: 'Bob' });
      });

      const aliceDb = testEnv.authenticatedContext('alice').firestore();
      await assertSucceeds(aliceDb.collection('users').doc('bob').get());
    });

    it('allows user to update and delete only their own profile', async () => {
      await testEnv.withSecurityRulesDisabled(async (context) => {
        const adminDb = context.firestore();
        await adminDb.collection('users').doc('alice').set({ name: 'Alice' });
        await adminDb.collection('users').doc('bob').set({ name: 'Bob' });
      });

      const aliceDb = testEnv.authenticatedContext('alice').firestore();
      await assertSucceeds(aliceDb.collection('users').doc('alice').update({ name: 'Alice Modified' }));
      await assertFails(aliceDb.collection('users').doc('bob').update({ name: 'Bob Hacked' }));
      await assertFails(aliceDb.collection('users').doc('bob').delete());
      await assertSucceeds(aliceDb.collection('users').doc('alice').delete());
    });
  });

  describe('Cart Isolation (/cart/{document})', () => {
    it('denies unauthenticated access completely', async () => {
      const unauthDb = testEnv.unauthenticatedContext().firestore();
      await assertFails(unauthDb.collection('cart').doc('cart-1').get());
      await assertFails(unauthDb.collection('cart').doc('cart-1').set({ quantity: 1 }));
    });

    it('allows user to create cart item owned by themselves', async () => {
      const aliceDb = testEnv.authenticatedContext('alice').firestore();
      const aliceUserRef = aliceDb.doc('users/alice');

      await assertSucceeds(aliceDb.collection('cart').doc('cart-alice-1').set({
        user_ref: aliceUserRef,
        price: 15.0,
        quantity: 1,
      }));
    });

    it('denies user from creating cart item for another user', async () => {
      const aliceDb = testEnv.authenticatedContext('alice').firestore();
      const bobUserRef = aliceDb.doc('users/bob');

      await assertFails(aliceDb.collection('cart').doc('cart-bob-1').set({
        user_ref: bobUserRef,
        price: 15.0,
        quantity: 1,
      }));
    });

    it('enforces owner isolation: user cannot read or delete another user cart item', async () => {
      await testEnv.withSecurityRulesDisabled(async (context) => {
        const db = context.firestore();
        await db.collection('cart').doc('cart-bob-1').set({
          user_ref: db.doc('users/bob'),
          price: 25.0,
          quantity: 2,
        });
      });

      const aliceDb = testEnv.authenticatedContext('alice').firestore();

      await assertFails(aliceDb.collection('cart').doc('cart-bob-1').get());
      await assertFails(aliceDb.collection('cart').doc('cart-bob-1').delete());
      await assertFails(aliceDb.collection('cart').doc('cart-bob-1').update({ quantity: 5 }));
    });

    it('enforces immutability of owner upon update', async () => {
      await testEnv.withSecurityRulesDisabled(async (context) => {
        const db = context.firestore();
        await db.collection('cart').doc('cart-alice-1').set({
          user_ref: db.doc('users/alice'),
          price: 10.0,
          quantity: 1,
        });
      });

      const aliceDb = testEnv.authenticatedContext('alice').firestore();

      // Alice can update her own cart item quantity
      await assertSucceeds(aliceDb.collection('cart').doc('cart-alice-1').update({ quantity: 3 }));

      // Alice cannot reassign her cart item to bob
      await assertFails(aliceDb.collection('cart').doc('cart-alice-1').update({
        user_ref: aliceDb.doc('users/bob'),
      }));
    });
  });

  describe('Reservation Isolation (/Reservation/{document})', () => {
    it('denies unauthenticated access to reservations', async () => {
      const unauthDb = testEnv.unauthenticatedContext().firestore();
      await assertFails(unauthDb.collection('Reservation').doc('res-1').get());
      await assertFails(unauthDb.collection('Reservation').doc('res-1').set({ nbplaces: 2 }));
    });

    it('allows owner to create and read their reservation, denies other users', async () => {
      const aliceDb = testEnv.authenticatedContext('alice').firestore();
      const aliceUserRef = aliceDb.doc('users/alice');

      await assertSucceeds(aliceDb.collection('Reservation').doc('res-alice-1').set({
        user_ref: aliceUserRef,
        nbplaces: 2,
        note: 'Front row',
      }));

      const bobDb = testEnv.authenticatedContext('bob').firestore();

      await assertFails(bobDb.collection('Reservation').doc('res-alice-1').get());
      await assertFails(bobDb.collection('Reservation').doc('res-alice-1').update({ nbplaces: 5 }));
      await assertFails(bobDb.collection('Reservation').doc('res-alice-1').delete());
    });
  });

  describe('Favoris Isolation (/Favoris/{document})', () => {
    it('denies unauthenticated access to favoris', async () => {
      const unauthDb = testEnv.unauthenticatedContext().firestore();
      await assertFails(unauthDb.collection('Favoris').doc('fav-1').get());
      await assertFails(unauthDb.collection('Favoris').doc('fav-1').set({ event: 'event1' }));
    });

    it('allows owner to manage favoris and denies other users', async () => {
      const aliceDb = testEnv.authenticatedContext('alice').firestore();
      const aliceUserRef = aliceDb.doc('users/alice');

      await assertSucceeds(aliceDb.collection('Favoris').doc('fav-alice-1').set({
        user_ref: aliceUserRef,
        event: aliceDb.doc('Event/event-1'),
      }));

      const bobDb = testEnv.authenticatedContext('bob').firestore();

      await assertFails(bobDb.collection('Favoris').doc('fav-alice-1').get());
      await assertFails(bobDb.collection('Favoris').doc('fav-alice-1').delete());
    });
  });

  describe('Reviews (/review/{document})', () => {
    it('allows public read for reviews', async () => {
      await testEnv.withSecurityRulesDisabled(async (context) => {
        const adminDb = context.firestore();
        await adminDb.collection('review').doc('rev-1').set({
          description: 'Great series',
          rating: 5,
        });
      });

      const unauthDb = testEnv.unauthenticatedContext().firestore();
      await assertSucceeds(unauthDb.collection('review').doc('rev-1').get());
    });

    it('denies unauthenticated creation and modification of reviews', async () => {
      const unauthDb = testEnv.unauthenticatedContext().firestore();
      await assertFails(unauthDb.collection('review').doc('rev-2').set({
        description: 'Anonymous review',
      }));
    });

    it('allows authenticated user to create a review and restricts edit/delete to author', async () => {
      const aliceDb = testEnv.authenticatedContext('alice').firestore();

      await assertSucceeds(aliceDb.collection('review').doc('rev-alice').set({
        description: 'Alice review',
        rating: 4,
        user_ref: aliceDb.doc('users/alice'),
      }));

      const bobDb = testEnv.authenticatedContext('bob').firestore();

      await assertFails(bobDb.collection('review').doc('rev-alice').update({ description: 'Bob edited this' }));
      await assertFails(bobDb.collection('review').doc('rev-alice').delete());

      await assertSucceeds(aliceDb.collection('review').doc('rev-alice').update({ description: 'Alice edited this' }));
      await assertSucceeds(aliceDb.collection('review').doc('rev-alice').delete());
    });
  });
});
