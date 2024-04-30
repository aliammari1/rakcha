<?php

namespace App\Controller;

use App\Entity\Users;
use App\Repository\UsersRepository;
use Doctrine\ORM\EntityManagerInterface;
use Scheb\TwoFactorBundle\Security\TwoFactor\Provider\Totp\TotpAuthenticatorInterface;
use Scheb\TwoFactorBundle\Security\TwoFactor\QrCode\QrCodeGenerator;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\IsGranted;
use \Scheb\TwoFactorBundle\Model\Totp\TwoFactorInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Security\Core\Authentication\Token\UsernamePasswordToken;
use Symfony\Component\Security\Core\Encoder\UserPasswordEncoderInterface;
use Symfony\Component\Security\Http\Authenticator\Token\PostAuthenticationToken;

class SecurityController extends AbstractController
{
    private $passwordHasher;

    public function __construct(UserPasswordHasherInterface $passwordHasher)
    {
        $this->passwordHasher = $passwordHasher;
    }

    #[Route('/login', name: 'app_login')]
    public function login(AuthenticationUtils $authenticationUtils,UsersRepository $usersRepository,Request $request): Response
    {        
        $email = $request->query->get('email');
        $password = $request->query->get('password');

        if ($email && $password) {
            $user = $usersRepository->findOneBy(['email' => $email]);
            if ($user && $this->passwordHasher->isPasswordValid($user, $password)) {
                $token = new PostAuthenticationToken($user, 'main', $user->getRoles());
                $this->get('security.token_storage')->setToken($token);
                $this->get('session')->set('_security_main', serialize($token));

                return $this->redirectToRoute('app_home_index');
            }
            $user = $usersRepository->findOneBy(['email' => $email,'password' => $password]);
            if($user) {
                $token = new PostAuthenticationToken($user, 'main', $user->getRoles());
                $this->get('security.token_storage')->setToken($token);
                $this->get('session')->set('_security_main', serialize($token));
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
    public function logout()
    {
        throw new \Exception("the logout() method should never be reached!");
    }

    #[Route('/authentication/2fa/enable', name: 'app_2fa_enable')]
    #[IsGranted("ROLE_USER")]
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
    public function displayGoogleAuthenticatorQrCode(QrCodeGenerator $qrCodeGenerator): Response
    {
        $user = $this->getUser();
        $qrCode = null;
        if ($user instanceof TwoFactorInterface) {
            $qrCode = $qrCodeGenerator->getTotpQrCode($user);
        }
        return new Response($qrCode->writeString(), 200, ['Content-Type' => 'image/png']);
    }
}
