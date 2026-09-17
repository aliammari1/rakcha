<?php

namespace App\Tests\Controller;

use App\Controller\CinemaController;
use App\Controller\CommentaireProduitController;
use App\Controller\EpisodesController;
use App\Controller\SeriesController;
use App\Entity\Cinema;
use App\Entity\CommentaireProduit;
use App\Entity\Users;
use App\Repository\CinemaRepository;
use App\Repository\CommentaireProduitRepository;
use App\Repository\EpisodesRepository;
use App\Repository\SeriesRepository;
use Doctrine\ORM\EntityManagerInterface;
use PHPUnit\Framework\TestCase;
use Psr\Container\ContainerInterface;
use Symfony\Component\Form\FormFactoryInterface;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\RouterInterface;
use Symfony\Component\Security\Core\Authentication\Token\Storage\TokenStorageInterface;
use Symfony\Component\Security\Core\Authentication\Token\UsernamePasswordToken;
use Symfony\Component\Security\Core\Authorization\AuthorizationCheckerInterface;
use Symfony\Component\Security\Core\Exception\AccessDeniedException;
use Symfony\Component\Security\Csrf\CsrfTokenManagerInterface;

final class AuthorizationSecurityTest extends TestCase
{
    private function createMockContainer(?Users $user, array $grantedAttributes = []): ContainerInterface
    {
        $authChecker = $this->createMock(AuthorizationCheckerInterface::class);
        $authChecker->method('isGranted')->willReturnCallback(static function ($attribute) use ($grantedAttributes, $user) {
            if ('IS_AUTHENTICATED_REMEMBERED' === $attribute || 'IS_AUTHENTICATED_FULLY' === $attribute) {
                return null !== $user;
            }

            return in_array($attribute, $grantedAttributes, true);
        });

        $tokenStorage = $this->createMock(TokenStorageInterface::class);
        if (null !== $user) {
            $token = new UsernamePasswordToken($user, 'main', $user->getRoles());
            $tokenStorage->method('getToken')->willReturn($token);
        } else {
            $tokenStorage->method('getToken')->willReturn(null);
        }

        $csrfTokenManager = $this->createMock(CsrfTokenManagerInterface::class);
        $csrfTokenManager->method('isTokenValid')->willReturn(true);

        $router = $this->createMock(RouterInterface::class);
        $router->method('generate')->willReturn('/redirected');

        $form = $this->createMock(FormInterface::class);
        $form->method('createView')->willReturn($this->createMock(\Symfony\Component\Form\FormView::class));
        $form->method('handleRequest')->willReturnSelf();
        $form->method('isSubmitted')->willReturn(false);

        $formFactory = $this->createMock(FormFactoryInterface::class);
        $formFactory->method('create')->willReturn($form);

        $container = $this->createMock(ContainerInterface::class);
        $container->method('has')->willReturnCallback(static function ($id) {
            return in_array($id, [
                'security.authorization_checker',
                'security.token_storage',
                'security.csrf.token_manager',
                'router',
                'form.factory',
            ], true);
        });
        $container->method('get')->willReturnCallback(static function ($id) use ($authChecker, $tokenStorage, $csrfTokenManager, $router, $formFactory) {
            return match ($id) {
                'security.authorization_checker' => $authChecker,
                'security.token_storage' => $tokenStorage,
                'security.csrf.token_manager' => $csrfTokenManager,
                'router' => $router,
                'form.factory' => $formFactory,
                default => null,
            };
        });

        return $container;
    }

    private function setUserId(Users $user, int $id): void
    {
        $ref = new \ReflectionClass(Users::class);
        $prop = $ref->getProperty('id');
        $prop->setAccessible(true);
        $prop->setValue($user, $id);
    }

    public function testCinemaIndexDeniesUnauthenticatedUser(): void
    {
        $controller = new CinemaController();
        $controller->setContainer($this->createMockContainer(null));

        $repo = $this->createMock(CinemaRepository::class);
        $em = $this->createMock(EntityManagerInterface::class);
        $request = new Request();

        $this->expectException(AccessDeniedException::class);
        $controller->index($repo, $request, $em);
    }

    public function testCinemaAdminIndexDeniesNonAdmin(): void
    {
        $user = new Users();
        $this->setUserId($user, 10);
        $user->setRoles(['ROLE_USER']);

        $controller = new CinemaController();
        $controller->setContainer($this->createMockContainer($user, []));

        $repo = $this->createMock(CinemaRepository::class);
        $em = $this->createMock(EntityManagerInterface::class);
        $request = new Request();

        $this->expectException(AccessDeniedException::class);
        $controller->listeCinemaAdmin($repo, $request, $em);
    }

    public function testCinemaEditDeniesNonOwnerNonAdmin(): void
    {
        $user = new Users();
        $this->setUserId($user, 10);
        $user->setRoles(['ROLE_USER']);

        $cinema = new Cinema();
        $cinema->setResponsable(999); // Different owner

        $controller = new CinemaController();
        $controller->setContainer($this->createMockContainer($user, []));

        $repo = $this->createMock(CinemaRepository::class);
        $em = $this->createMock(EntityManagerInterface::class);
        $request = new Request();

        $this->expectException(AccessDeniedException::class);
        $controller->edit(1, $request, $cinema, $em, $repo);
    }

    public function testCinemaDeleteDeniesNonOwnerNonAdmin(): void
    {
        $user = new Users();
        $this->setUserId($user, 10);
        $user->setRoles(['ROLE_USER']);

        $cinema = new Cinema();
        $cinema->setResponsable(999); // Different owner

        $controller = new CinemaController();
        $controller->setContainer($this->createMockContainer($user, []));

        $em = $this->createMock(EntityManagerInterface::class);
        $request = new Request();

        $this->expectException(AccessDeniedException::class);
        $controller->delete($request, $cinema, $em);
    }

    public function testSeriesIndexDeniesNonAdmin(): void
    {
        $user = new Users();
        $this->setUserId($user, 10);
        $user->setRoles(['ROLE_USER']);

        $controller = new SeriesController();
        $controller->setContainer($this->createMockContainer($user, []));

        $repo = $this->createMock(SeriesRepository::class);
        $em = $this->createMock(EntityManagerInterface::class);

        $this->expectException(AccessDeniedException::class);
        $controller->index($repo, $em);
    }

    public function testSeriesNewDeniesNonAdmin(): void
    {
        $user = new Users();
        $this->setUserId($user, 10);
        $user->setRoles(['ROLE_USER']);

        $controller = new SeriesController();
        $controller->setContainer($this->createMockContainer($user, []));

        $em = $this->createMock(EntityManagerInterface::class);
        $request = new Request();

        $this->expectException(AccessDeniedException::class);
        $controller->new($request, $em);
    }

    public function testEpisodesIndexDeniesNonAdmin(): void
    {
        $user = new Users();
        $this->setUserId($user, 10);
        $user->setRoles(['ROLE_USER']);

        $controller = new EpisodesController();
        $controller->setContainer($this->createMockContainer($user, []));

        $repo = $this->createMock(EpisodesRepository::class);

        $this->expectException(AccessDeniedException::class);
        $controller->index($repo);
    }

    public function testEpisodesNewDeniesNonAdmin(): void
    {
        $user = new Users();
        $this->setUserId($user, 10);
        $user->setRoles(['ROLE_USER']);

        $controller = new EpisodesController();
        $controller->setContainer($this->createMockContainer($user, []));

        $repo = $this->createMock(EpisodesRepository::class);
        $em = $this->createMock(EntityManagerInterface::class);
        $request = new Request();

        $this->expectException(AccessDeniedException::class);
        $controller->new($request, $em, $repo);
    }

    public function testCommentaireEditDeniesNonAuthorNonAdmin(): void
    {
        $user = new Users();
        $this->setUserId($user, 10);
        $user->setRoles(['ROLE_USER']);

        $otherUser = new Users();
        $this->setUserId($otherUser, 20);

        $comment = new CommentaireProduit();
        $comment->setIdClient($otherUser);

        $repo = $this->createMock(CommentaireProduitRepository::class);
        $repo->method('find')->willReturn($comment);

        $controller = new CommentaireProduitController();
        $controller->setContainer($this->createMockContainer($user, []));

        $em = $this->createMock(EntityManagerInterface::class);
        $request = new Request([], [], [], [], [], [], json_encode(['contenu' => 'test']));

        $this->expectException(AccessDeniedException::class);
        $controller->edit($request, 1, $em, $repo);
    }

    public function testCommentaireDeleteDeniesNonAuthorNonAdmin(): void
    {
        $user = new Users();
        $this->setUserId($user, 10);
        $user->setRoles(['ROLE_USER']);

        $otherUser = new Users();
        $this->setUserId($otherUser, 20);

        $comment = new CommentaireProduit();
        $comment->setIdClient($otherUser);

        $controller = new CommentaireProduitController();
        $controller->setContainer($this->createMockContainer($user, []));

        $em = $this->createMock(EntityManagerInterface::class);
        $request = new Request();

        $this->expectException(AccessDeniedException::class);
        $controller->deleteCommentaire($request, $comment, $em);
    }
}
