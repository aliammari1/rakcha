<?php

namespace App\Tests\Controller;

use App\Controller\CommandeController;
use App\Controller\paymentStripeController;
use App\Entity\Commande;
use App\Entity\Commandeitem;
use App\Entity\Produit;
use App\Entity\Salle;
use App\Entity\Seance;
use App\Entity\Seat;
use App\Entity\Users;
use App\Repository\CommandeRepository;
use App\Repository\SeanceRepository;
use App\Repository\SeatRepository;
use Doctrine\DBAL\Connection;
use Doctrine\DBAL\LockMode;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;
use Omnipay\Common\Message\RequestInterface;
use Omnipay\Common\Message\ResponseInterface;
use Omnipay\PayPal\RestGateway;
use PHPUnit\Framework\TestCase;
use Psr\Container\ContainerInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Security\Core\Authentication\Token\Storage\TokenStorageInterface;
use Symfony\Component\Security\Core\Authentication\Token\UsernamePasswordToken;
use Symfony\Component\Security\Core\Authorization\AuthorizationCheckerInterface;

final class PaymentSecurityTest extends TestCase
{
    private function createMockContainer(Users $user): ContainerInterface
    {
        $authChecker = $this->createMock(AuthorizationCheckerInterface::class);
        $authChecker->method('isGranted')->willReturn(true);

        $token = new UsernamePasswordToken($user, 'main', $user->getRoles());
        $tokenStorage = $this->createMock(TokenStorageInterface::class);
        $tokenStorage->method('getToken')->willReturn($token);

        $csrfTokenManager = $this->createMock(\Symfony\Component\Security\Csrf\CsrfTokenManagerInterface::class);
        $csrfTokenManager->method('isTokenValid')->willReturn(true);

        $router = $this->createMock(\Symfony\Component\Routing\RouterInterface::class);
        $router->method('generate')->willReturn('/payment/success');

        $container = $this->createMock(ContainerInterface::class);
        $container->method('has')->willReturnCallback(static function ($id) {
            return in_array($id, [
                'security.authorization_checker',
                'security.token_storage',
                'security.csrf.token_manager',
                'router',
                'parameter_bag',
            ], true);
        });
        $container->method('get')->willReturnCallback(static function ($id) use ($authChecker, $tokenStorage, $csrfTokenManager, $router) {
            if ('security.authorization_checker' === $id) {
                return $authChecker;
            }
            if ('security.token_storage' === $id) {
                return $tokenStorage;
            }
            if ('security.csrf.token_manager' === $id) {
                return $csrfTokenManager;
            }
            if ('router' === $id) {
                return $router;
            }

            return null;
        });

        return $container;
    }

    public function testStripePaymentDerivesAuthoritativeAmountAndRejectsTamperedClientPrice(): void
    {
        $user = new Users();
        $user->setEmail('client@example.com');
        $user->setRoles(['ROLE_USER']);

        $salle = new Salle();
        $refSalle = new \ReflectionClass(Salle::class);
        $propSalleId = $refSalle->getProperty('idSalle');
        $propSalleId->setAccessible(true);
        $propSalleId->setValue($salle, 10);

        $seance = new Seance();
        $refSeance = new \ReflectionClass(Seance::class);
        $propSeanceId = $refSeance->getProperty('idSeance');
        $propSeanceId->setAccessible(true);
        $propSeanceId->setValue($seance, 42);
        $seance->setPrix(15.50);
        $seance->setIdSalle($salle);

        $seat1 = new Seat();
        $seat1->setStatut('vide');
        $seat1->setSalle($salle);

        $seat2 = new Seat();
        $seat2->setStatut('vide');
        $seat2->setSalle($salle);

        $seanceRepo = $this->createMock(SeanceRepository::class);
        $seanceRepo->method('find')->with(42)->willReturn($seance);

        $seatRepo = $this->createMock(SeatRepository::class);

        $connection = $this->createMock(Connection::class);
        $connection->method('isTransactionActive')->willReturn(true);

        $em = $this->createMock(EntityManagerInterface::class);
        $em->method('getConnection')->willReturn($connection);
        $em->expects($this->once())->method('beginTransaction');
        $em->method('find')->willReturnCallback(function ($class, $id, $lockMode) use ($seat1, $seat2) {
            $this->assertSame(LockMode::PESSIMISTIC_WRITE, $lockMode);
            if (101 === $id) {
                return $seat1;
            }
            if (102 === $id) {
                return $seat2;
            }

            return null;
        });

        // Test controller overriding executeStripeCharge to intercept charge amount without external HTTP calls
        $controller = new class extends paymentStripeController {
            public ?int $chargedCents = null;
            public ?string $chargedDescription = null;

            protected function executeStripeCharge(int $amountInCents, string $token, string $description): void
            {
                $this->chargedCents = $amountInCents;
                $this->chargedDescription = $description;
            }
        };
        $controller->setContainer($this->createMockContainer($user));

        // Client payload attempts to manipulate price to 0.01 instead of authoritative 31.00 (2 * 15.50)
        $payload = [
            'seanceId' => 42,
            'seatIds' => [101, 102],
            'stripeToken' => 'tok_test',
            'prix' => 0.01,
        ];
        $request = new Request([], [], [], [], [], [], json_encode($payload));

        $response = $controller->createCharge($request, $seatRepo, $seanceRepo, $em);
        $data = json_decode($response->getContent(), true);

        // Verify the authoritative calculation occurred: 2 seats * 15.50 = 31.00 (3100 cents)
        $this->assertSame(Response::HTTP_OK, $response->getStatusCode());
        $this->assertTrue($data['success']);
        $this->assertSame(31.00, (float) $data['amount']);
        $this->assertSame(2, $data['seatsCount']);
        $this->assertSame(3100, $controller->chargedCents);
        $this->assertSame('reserve', $seat1->getStatut());
        $this->assertSame('reserve', $seat2->getStatut());
    }

    public function testSeatConcurrencyPessimisticLockingRejectsAlreadyReservedSeat(): void
    {
        $user = new Users();
        $user->setEmail('client@example.com');
        $user->setRoles(['ROLE_USER']);

        $salle = new Salle();
        $refSalle = new \ReflectionClass(Salle::class);
        $propSalleId = $refSalle->getProperty('idSalle');
        $propSalleId->setAccessible(true);
        $propSalleId->setValue($salle, 10);

        $seance = new Seance();
        $refSeance = new \ReflectionClass(Seance::class);
        $propSeanceId = $refSeance->getProperty('idSeance');
        $propSeanceId->setAccessible(true);
        $propSeanceId->setValue($seance, 42);
        $seance->setPrix(20.0);
        $seance->setIdSalle($salle);

        // Seat is already marked 'reserve' (e.g. by another concurrent transaction)
        $reservedSeat = new Seat();
        $reservedSeat->setStatut('reserve');
        $reservedSeat->setSalle($salle);

        $seanceRepo = $this->createMock(SeanceRepository::class);
        $seanceRepo->method('find')->with(42)->willReturn($seance);

        $seatRepo = $this->createMock(SeatRepository::class);

        $connection = $this->createMock(Connection::class);
        $connection->method('isTransactionActive')->willReturn(true);

        $em = $this->createMock(EntityManagerInterface::class);
        $em->method('getConnection')->willReturn($connection);
        $em->expects($this->once())->method('beginTransaction');
        $em->expects($this->once())->method('rollback');
        $em->method('find')->with(Seat::class, 101, LockMode::PESSIMISTIC_WRITE)->willReturn($reservedSeat);

        $controller = new class extends paymentStripeController {
            protected function executeStripeCharge(int $amountInCents, string $token, string $description): void
            {
                TestCase::fail('Stripe charge must not be executed for already reserved seat');
            }
        };
        $controller->setContainer($this->createMockContainer($user));

        $payload = [
            'seanceId' => 42,
            'seatIds' => [101],
            'stripeToken' => 'tok_test',
        ];
        $request = new Request([], [], [], [], [], [], json_encode($payload));

        $response = $controller->createCharge($request, $seatRepo, $seanceRepo, $em);
        $data = json_decode($response->getContent(), true);

        // Must return HTTP 409 Conflict and reject the booking without charging
        $this->assertSame(Response::HTTP_CONFLICT, $response->getStatusCode());
        $this->assertFalse($data['success']);
        $this->assertStringContainsString('already reserved', $data['message']);
    }

    public function testSeatBookingRejectsSeatFromDifferentHall(): void
    {
        $user = new Users();
        $user->setEmail('client@example.com');
        $user->setRoles(['ROLE_USER']);

        $salleA = new Salle();
        $refSalle = new \ReflectionClass(Salle::class);
        $propSalleId = $refSalle->getProperty('idSalle');
        $propSalleId->setAccessible(true);
        $propSalleId->setValue($salleA, 1);

        $salleB = new Salle();
        $propSalleId->setValue($salleB, 2);

        $seance = new Seance();
        $refSeance = new \ReflectionClass(Seance::class);
        $propSeanceId = $refSeance->getProperty('idSeance');
        $propSeanceId->setAccessible(true);
        $propSeanceId->setValue($seance, 42);
        $seance->setPrix(10.0);
        $seance->setIdSalle($salleA);

        $seatDifferentHall = new Seat();
        $seatDifferentHall->setStatut('vide');
        $seatDifferentHall->setSalle($salleB); // Belongs to Salle B, not Salle A

        $seanceRepo = $this->createMock(SeanceRepository::class);
        $seanceRepo->method('find')->with(42)->willReturn($seance);

        $seatRepo = $this->createMock(SeatRepository::class);

        $connection = $this->createMock(Connection::class);
        $connection->method('isTransactionActive')->willReturn(true);

        $em = $this->createMock(EntityManagerInterface::class);
        $em->method('getConnection')->willReturn($connection);
        $em->expects($this->once())->method('beginTransaction');
        $em->expects($this->once())->method('rollback');
        $em->method('find')->with(Seat::class, 999, LockMode::PESSIMISTIC_WRITE)->willReturn($seatDifferentHall);

        $controller = new paymentStripeController();
        $controller->setContainer($this->createMockContainer($user));

        $payload = [
            'seanceId' => 42,
            'seatIds' => [999],
            'stripeToken' => 'tok_test',
        ];
        $request = new Request([], [], [], [], [], [], json_encode($payload));

        $response = $controller->createCharge($request, $seatRepo, $seanceRepo, $em);
        $this->assertSame(Response::HTTP_BAD_REQUEST, $response->getStatusCode());
    }

    public function testConcurrentDoubleBookingScenarioResultsInExactlyOneSuccessAndOneConflict(): void
    {
        $user1 = new Users();
        $user1->setEmail('user1@example.com');
        $user1->setRoles(['ROLE_USER']);

        $user2 = new Users();
        $user2->setEmail('user2@example.com');
        $user2->setRoles(['ROLE_USER']);

        $salle = new Salle();
        $refSalle = new \ReflectionClass(Salle::class);
        $propSalleId = $refSalle->getProperty('idSalle');
        $propSalleId->setAccessible(true);
        $propSalleId->setValue($salle, 10);

        $seance = new Seance();
        $refSeance = new \ReflectionClass(Seance::class);
        $propSeanceId = $refSeance->getProperty('idSeance');
        $propSeanceId->setAccessible(true);
        $propSeanceId->setValue($seance, 42);
        $seance->setPrix(20.0);
        $seance->setIdSalle($salle);

        $seat = new Seat();
        $seat->setStatut('vide');
        $seat->setSalle($salle);

        $seanceRepo = $this->createMock(SeanceRepository::class);
        $seanceRepo->method('find')->with(42)->willReturn($seance);
        $seatRepo = $this->createMock(SeatRepository::class);

        $connection = $this->createMock(Connection::class);
        $connection->method('isTransactionActive')->willReturn(true);

        $em = $this->createMock(EntityManagerInterface::class);
        $em->method('getConnection')->willReturn($connection);
        $em->method('find')->with(Seat::class, 101, LockMode::PESSIMISTIC_WRITE)->willReturn($seat);

        $controller = new class extends paymentStripeController {
            public int $chargeCount = 0;

            protected function executeStripeCharge(int $amountInCents, string $token, string $description): void
            {
                ++$this->chargeCount;
            }
        };

        // First request from User 1 attempts reservation
        $controller->setContainer($this->createMockContainer($user1));
        $req1 = new Request([], [], [], [], [], [], json_encode([
            'seanceId' => 42,
            'seatIds' => [101],
            'stripeToken' => 'tok_user1',
        ]));
        $resp1 = $controller->createCharge($req1, $seatRepo, $seanceRepo, $em);
        $data1 = json_decode($resp1->getContent(), true);

        $this->assertSame(Response::HTTP_OK, $resp1->getStatusCode());
        $this->assertTrue($data1['success']);
        $this->assertSame('reserve', $seat->getStatut());
        $this->assertSame(1, $controller->chargeCount);

        // Second competing request from User 2 arrives for the exact same seat
        $controller->setContainer($this->createMockContainer($user2));
        $req2 = new Request([], [], [], [], [], [], json_encode([
            'seanceId' => 42,
            'seatIds' => [101],
            'stripeToken' => 'tok_user2',
        ]));
        $resp2 = $controller->createCharge($req2, $seatRepo, $seanceRepo, $em);
        $data2 = json_decode($resp2->getContent(), true);

        $this->assertSame(Response::HTTP_CONFLICT, $resp2->getStatusCode());
        $this->assertFalse($data2['success']);
        $this->assertStringContainsString('already reserved', $data2['message']);
        // Crucial invariant: Stripe was only charged once despite 2 requests
        $this->assertSame(1, $controller->chargeCount);
    }

    public function testSeatConcurrencyEnforcesDeterministicLockOrdering(): void
    {
        $user = new Users();
        $user->setEmail('user@example.com');
        $user->setRoles(['ROLE_USER']);

        $salle = new Salle();
        $refSalle = new \ReflectionClass(Salle::class);
        $propSalleId = $refSalle->getProperty('idSalle');
        $propSalleId->setAccessible(true);
        $propSalleId->setValue($salle, 1);

        $seance = new Seance();
        $refSeance = new \ReflectionClass(Seance::class);
        $propSeanceId = $refSeance->getProperty('idSeance');
        $propSeanceId->setAccessible(true);
        $propSeanceId->setValue($seance, 10);
        $seance->setPrix(12.0);
        $seance->setIdSalle($salle);

        $createSeat = static function (int $id) use ($salle) {
            $s = new Seat();
            $s->setStatut('vide');
            $s->setSalle($salle);

            return $s;
        };

        $seats = [
            101 => $createSeat(101),
            105 => $createSeat(105),
            109 => $createSeat(109),
        ];

        $lockedOrder = [];
        $em = $this->createMock(EntityManagerInterface::class);
        $em->method('getConnection')->willReturn($this->createMock(Connection::class));
        $em->method('find')->willReturnCallback(static function ($class, $id, $lockMode) use (&$lockedOrder, $seats) {
            $lockedOrder[] = $id;

            return $seats[$id] ?? null;
        });

        $seanceRepo = $this->createMock(SeanceRepository::class);
        $seanceRepo->method('find')->with(10)->willReturn($seance);
        $seatRepo = $this->createMock(SeatRepository::class);

        $controller = new class extends paymentStripeController {
            protected function executeStripeCharge(int $amountInCents, string $token, string $description): void
            {
            }
        };
        $controller->setContainer($this->createMockContainer($user));

        // Client passes unordered and duplicated seat IDs [109, 101, 105, 101]
        $payload = [
            'seanceId' => 10,
            'seatIds' => [109, 101, 105, 101],
            'stripeToken' => 'tok_order_test',
        ];
        $request = new Request([], [], [], [], [], [], json_encode($payload));

        $response = $controller->createCharge($request, $seatRepo, $seanceRepo, $em);
        $this->assertSame(Response::HTTP_OK, $response->getStatusCode());

        // Locks must be acquired strictly in ascending sorted order: 101, 105, 109
        $this->assertSame([101, 105, 109], $lockedOrder);
    }

    public function testPaypalPaymentDerivesAuthoritativeAmountAndIgnoresClientSuppliedAmount(): void
    {
        $user = new Users();
        $user->setEmail('shopper@example.com');
        $user->setRoles(['ROLE_USER']);

        $commande = new Commande();
        $commande->setIdclient($user);

        $product1 = new Produit();
        $product1->setNom('Popcorn');
        $product1->setPrix(10);

        $product2 = new Produit();
        $product2->setNom('Soda');
        $product2->setPrix(5);

        $item1 = new Commandeitem();
        $item1->setIdProduit($product1);
        $item1->setQuantity(2); // 2 * 10 = 20

        $item2 = new Commandeitem();
        $item2->setIdProduit($product2);
        $item2->setQuantity(3); // 3 * 5 = 15. Total = 35.00

        $commandeRepo = $this->createMock(CommandeRepository::class);
        $commandeRepo->method('find')->with('77')->willReturn($commande);

        $itemRepo = $this->createMock(EntityRepository::class);
        $itemRepo->method('findBy')->with(['idcommande' => $commande])->willReturn([$item1, $item2]);

        $em = $this->createMock(EntityManagerInterface::class);
        $em->method('getRepository')->with(Commandeitem::class)->willReturn($itemRepo);

        // Mock Omnipay purchase request and response
        $purchaseResponse = $this->createMock(ResponseInterface::class);
        $purchaseResponse->method('isRedirect')->willReturn(true);
        $purchaseResponse->method('getData')->willReturn([
            'links' => [
                ['rel' => 'approval_url', 'href' => 'https://sandbox.paypal.com/checkout?token=mock_123'],
            ],
        ]);

        $purchaseRequest = $this->createMock(RequestInterface::class);
        $purchaseRequest->method('send')->willReturn($purchaseResponse);

        $gateway = $this->createMock(RestGateway::class);
        // Assert that purchase amount is exactly '35.00' (authoritative server calculation)
        $gateway->expects($this->once())
            ->method('purchase')
            ->with($this->callback(static function (array $params) {
                return isset($params['amount']) && '35.00' === $params['amount'];
            }))
            ->willReturn($purchaseRequest);

        $controller = new CommandeController($em);
        $controller->setContainer($this->createMockContainer($user));

        // Inject mock gateway via reflection
        $refController = new \ReflectionClass(CommandeController::class);
        $propGateway = $refController->getProperty('passerelle');
        $propGateway->setAccessible(true);
        $propGateway->setValue($controller, $gateway);

        // Client passes manipulated amount: 1.00 instead of 35.00
        $request = new Request(
            ['commandeId' => '77'],
            ['token' => 'mock_valid_token', 'amount' => '1.00']
        );

        $response = $controller->payment($request, $commandeRepo, $em);

        $this->assertSame(Response::HTTP_FOUND, $response->getStatusCode());
        $this->assertSame('https://sandbox.paypal.com/checkout?token=mock_123', $response->headers->get('Location'));
    }

    public function testPaypalSuccessIsIdempotentWhenAlreadyPaid(): void
    {
        $user = new Users();
        $user->setEmail('shopper@example.com');
        $user->setRoles(['ROLE_USER']);

        $commande = new Commande();
        $commande->setIdclient($user);
        $commande->setStatu('payé'); // Already paid

        $commandeRepo = $this->createMock(CommandeRepository::class);
        $commandeRepo->method('find')->with('77')->willReturn($commande);

        $em = $this->createMock(EntityManagerInterface::class);

        $gateway = $this->createMock(RestGateway::class);
        // Gateway completePurchase should NEVER be called if already marked paid
        $gateway->expects($this->never())->method('completePurchase');

        $controller = new CommandeController($em);
        $controller->setContainer($this->createMockContainer($user));

        $refController = new \ReflectionClass(CommandeController::class);
        $propGateway = $refController->getProperty('passerelle');
        $propGateway->setAccessible(true);
        $propGateway->setValue($controller, $gateway);

        $request = new Request([
            'commandeId' => '77',
            'paymentId' => 'PAY-123',
            'PayerID' => 'PAYER-456',
        ]);

        $response = $controller->success($request, $commandeRepo, $em);

        $this->assertSame(Response::HTTP_FOUND, $response->getStatusCode());
    }

    public function testPaypalSuccessRejectsMismatchedCapturedAmount(): void
    {
        $user = new Users();
        $user->setEmail('shopper@example.com');
        $user->setRoles(['ROLE_USER']);

        $commande = new Commande();
        $commande->setIdclient($user);
        $commande->setStatu('En cours');

        $product = new Produit();
        $product->setNom('Ticket');
        $product->setPrix(50);

        $item = new Commandeitem();
        $item->setIdProduit($product);
        $item->setQuantity(1); // Authoritative amount = 50.00

        $commandeRepo = $this->createMock(CommandeRepository::class);
        $commandeRepo->method('find')->with('77')->willReturn($commande);

        $itemRepo = $this->createMock(EntityRepository::class);
        $itemRepo->method('findBy')->with(['idcommande' => $commande])->willReturn([$item]);

        $em = $this->createMock(EntityManagerInterface::class);
        $em->method('getRepository')->with(Commandeitem::class)->willReturn($itemRepo);
        // Order should NEVER be persisted or flushed with 'payé' if amount mismatch occurs
        $em->expects($this->never())->method('flush');

        // Gateway returns successful capture, but only for $10.00 instead of $50.00
        $captureResponse = $this->createMock(ResponseInterface::class);
        $captureResponse->method('isSuccessful')->willReturn(true);
        $captureResponse->method('getData')->willReturn([
            'id' => 'PAY-123',
            'state' => 'approved',
            'transactions' => [
                ['amount' => ['total' => '10.00']], // Mismatch!
            ],
        ]);

        $captureRequest = $this->createMock(RequestInterface::class);
        $captureRequest->method('send')->willReturn($captureResponse);

        $gateway = $this->createMock(RestGateway::class);
        $gateway->method('completePurchase')->willReturn($captureRequest);

        $controller = new CommandeController($em);

        // Container with twig support for error render
        $twig = $this->createMock(\Twig\Environment::class);
        $twig->method('render')->willReturn('error page content');

        $authChecker = $this->createMock(AuthorizationCheckerInterface::class);
        $authChecker->method('isGranted')->willReturn(true);
        $token = new UsernamePasswordToken($user, 'main', $user->getRoles());
        $tokenStorage = $this->createMock(TokenStorageInterface::class);
        $tokenStorage->method('getToken')->willReturn($token);

        $container = $this->createMock(ContainerInterface::class);
        $container->method('has')->willReturnCallback(static function ($id) {
            return in_array($id, [
                'security.authorization_checker',
                'security.token_storage',
                'twig',
                'parameter_bag',
            ], true);
        });
        $container->method('get')->willReturnCallback(static function ($id) use ($authChecker, $tokenStorage, $twig) {
            if ('security.authorization_checker' === $id) {
                return $authChecker;
            }
            if ('security.token_storage' === $id) {
                return $tokenStorage;
            }
            if ('twig' === $id) {
                return $twig;
            }

            return null;
        });

        $controller->setContainer($container);

        $refController = new \ReflectionClass(CommandeController::class);
        $propGateway = $refController->getProperty('passerelle');
        $propGateway->setAccessible(true);
        $propGateway->setValue($controller, $gateway);

        $request = new Request([
            'commandeId' => '77',
            'paymentId' => 'PAY-123',
            'PayerID' => 'PAYER-456',
        ]);

        $response = $controller->success($request, $commandeRepo, $em);

        // Order status must NOT be changed to payé
        $this->assertNotSame('payé', $commande->getStatu());
        $this->assertStringContainsString('error page content', $response->getContent());
    }

    public function testPaypalSuccessRejectsMismatchedCurrency(): void
    {
        $user = new Users();
        $user->setEmail('shopper@example.com');
        $user->setRoles(['ROLE_USER']);

        $commande = new Commande();
        $commande->setIdclient($user);
        $commande->setStatu('En cours');

        $product = new Produit();
        $product->setNom('Ticket');
        $product->setPrix(50);

        $item = new Commandeitem();
        $item->setIdProduit($product);
        $item->setQuantity(1);

        $commandeRepo = $this->createMock(CommandeRepository::class);
        $commandeRepo->method('find')->with('77')->willReturn($commande);

        $itemRepo = $this->createMock(EntityRepository::class);
        $itemRepo->method('findBy')->with(['idcommande' => $commande])->willReturn([$item]);

        $em = $this->createMock(EntityManagerInterface::class);
        $em->method('getRepository')->with(Commandeitem::class)->willReturn($itemRepo);
        $em->expects($this->never())->method('flush');

        // Gateway returns currency 'EUR' while configured is 'USD'
        $captureResponse = $this->createMock(ResponseInterface::class);
        $captureResponse->method('isSuccessful')->willReturn(true);
        $captureResponse->method('getData')->willReturn([
            'id' => 'PAY-123',
            'state' => 'approved',
            'transactions' => [
                [
                    'amount' => [
                        'total' => '50.00',
                        'currency' => 'EUR', // Mismatched currency!
                    ],
                ],
            ],
        ]);

        $captureRequest = $this->createMock(RequestInterface::class);
        $captureRequest->method('send')->willReturn($captureResponse);

        $gateway = $this->createMock(RestGateway::class);
        $gateway->method('completePurchase')->willReturn($captureRequest);

        $controller = new CommandeController($em);

        $twig = $this->createMock(\Twig\Environment::class);
        $twig->method('render')->willReturn('error page content: currency mismatch');

        $authChecker = $this->createMock(AuthorizationCheckerInterface::class);
        $authChecker->method('isGranted')->willReturn(true);
        $token = new UsernamePasswordToken($user, 'main', $user->getRoles());
        $tokenStorage = $this->createMock(TokenStorageInterface::class);
        $tokenStorage->method('getToken')->willReturn($token);

        $container = $this->createMock(ContainerInterface::class);
        $container->method('has')->willReturnCallback(static function ($id) {
            return in_array($id, [
                'security.authorization_checker',
                'security.token_storage',
                'twig',
                'parameter_bag',
            ], true);
        });
        $container->method('get')->willReturnCallback(static function ($id) use ($authChecker, $tokenStorage, $twig) {
            if ('security.authorization_checker' === $id) {
                return $authChecker;
            }
            if ('security.token_storage' === $id) {
                return $tokenStorage;
            }
            if ('twig' === $id) {
                return $twig;
            }

            return null;
        });

        $controller->setContainer($container);

        $refController = new \ReflectionClass(CommandeController::class);
        $propGateway = $refController->getProperty('passerelle');
        $propGateway->setAccessible(true);
        $propGateway->setValue($controller, $gateway);

        $request = new Request([
            'commandeId' => '77',
            'paymentId' => 'PAY-123',
            'PayerID' => 'PAYER-456',
        ]);

        $response = $controller->success($request, $commandeRepo, $em);

        $this->assertNotSame('payé', $commande->getStatu());
        $this->assertStringContainsString('currency mismatch', $response->getContent());
    }
}
