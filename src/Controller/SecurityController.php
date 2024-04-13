<?php

namespace App\Controller;

use App\Entity\Users;
use Doctrine\ORM\EntityManagerInterface;
use Scheb\TwoFactorBundle\Security\TwoFactor\Provider\Totp\TotpAuthenticatorInterface;
use Scheb\TwoFactorBundle\Security\TwoFactor\QrCode\QrCodeGenerator;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\IsGranted;
use \Scheb\TwoFactorBundle\Model\Totp\TwoFactorInterface;

class SecurityController extends AbstractController
{
    #[Route('/login', name: 'app_login')]
    public function login(AuthenticationUtils $authenticationUtils): Response
    {
        return $this->render('back/login.html.twig', [
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
