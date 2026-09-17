<?php

namespace App\Controller;

use App\Entity\Users;
use App\Repository\UsersRepository;
use BaconQrCode\Renderer\Image\SvgImageBackEnd;
use BaconQrCode\Renderer\ImageRenderer;
use BaconQrCode\Renderer\RendererStyle\RendererStyle;
use BaconQrCode\Writer;
use Doctrine\ORM\EntityManagerInterface;
use Scheb\TwoFactorBundle\Security\TwoFactor\Provider\Totp\TotpAuthenticatorInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Core\Authentication\Token\Storage\TokenStorageInterface;
use Symfony\Component\Security\Http\Attribute\IsGranted;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;
use Symfony\Component\Security\Http\Authenticator\Token\PostAuthenticationToken;

class SecurityController extends AbstractController
{
    private UserPasswordHasherInterface $passwordHasher;

    public function __construct(UserPasswordHasherInterface $passwordHasher)
    {
        $this->passwordHasher = $passwordHasher;
    }

    #[Route('/login', name: 'app_login')]
    public function login(AuthenticationUtils $authenticationUtils, UsersRepository $usersRepository, Request $request, TokenStorageInterface $tokenStorage, SessionInterface $session): Response
    {
        $email = $request->query->get('email');
        $password = $request->query->get('password');

        if ($email && $password) {
            $user = $usersRepository->findOneBy(['email' => $email]);
            if ($user && $this->passwordHasher->isPasswordValid($user, $password)) {
                $token = new PostAuthenticationToken($user, 'main', $user->getRoles());
                $tokenStorage->setToken($token);
                $session->set('_security_main', serialize($token));

                return $this->redirectToRoute('app_home_index');
            }
        }

        return $this->render('back/login.html.twig', [
            'users' => $usersRepository->findAll(),
            'error_message' => $authenticationUtils->getLastAuthenticationError(),
            'last_username' => $authenticationUtils->getLastUsername(),
        ]);
    }

    #[Route('/logout', name: 'app_logout_index')]
    public function logout(): void
    {
        throw new \Exception('the logout() method should never be reached!');
    }

    #[Route('/authentication/2fa/enable', name: 'app_2fa_enable')]
    #[IsGranted('ROLE_USER')]
    public function enable2fa(TotpAuthenticatorInterface $totpAuthenticator, EntityManagerInterface $entityManager)
    {
        $user = $this->getUser();
        if ($user instanceof Users) {
            if (!$user->isTotpAuthenticationEnabled()) {
                $user->setTotpSecret($totpAuthenticator->generateSecret());
                $entityManager->flush();
            }
        }

        return $this->render('back/twofa.html.twig');
    }

    #[Route('/authentication/2fa/qr-code', name: 'app_qr_code')]
    public function displayGoogleAuthenticatorQrCode(): Response
    {
        $user = $this->getUser();
        if (!$user instanceof Users || !$user->isTotpAuthenticationEnabled() || null === $user->getTotpSecret()) {
            throw $this->createNotFoundException('Two-factor authentication is not enabled.');
        }

        $label = rawurlencode('Rakcha:'.$user->getUserIdentifier());
        $uri = sprintf('otpauth://totp/%s?secret=%s&issuer=Rakcha', $label, rawurlencode($user->getTotpSecret()));
        $writer = new Writer(new ImageRenderer(new RendererStyle(300), new SvgImageBackEnd()));

        return new Response($writer->writeString($uri), Response::HTTP_OK, ['Content-Type' => 'image/svg+xml']);
    }
}
