<?php

namespace App\Controller;

use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;


class GoogleController extends AbstractController
{
    #[Route("/connect/google", name: "connect_google_start")]
    public function connectAction(ClientRegistry $clientRegistry)
    {
        // will redirect to Google!
        return $clientRegistry
            ->getClient('google') // key used in config/packages/knpu_oauth2_client.yaml
            ->redirect([
                'https://www.googleapis.com/auth/userinfo.profile', 'https://www.googleapis.com/auth/userinfo.email' // the scopes you want to access
            ], []);
    }


    #[Route("/connect/google/check", name: "connect_google_check")]
    public function connectCheckAction(Request $request, ClientRegistry $clientRegistry)
    {
        // ** if you want to *authenticate* the user, then
        // leave this method blank and create a Guard authenticator
        // (read below)

        // /** @var \KnpU\OAuth2ClientBundle\Client\Provider\GoogleClient $client */
        // $client = $clientRegistry->getClient('google');

        // try {
        //     // the exact class depends on which provider you're using
        //     /** @var \League\OAuth2\Client\Provider\GoogleUser $user */
        //     $user = $client->fetchUser();

        //     // do something with all this new power!
        //     // e.g. $name = $user->getFirstName();

        //     dd($user);
        //     // ...
        // } catch (IdentityProviderException $e) {
        //     // something went wrong!
        //     // probably you should return the reason to the user
        //     dd($e->getMessage());
        // }
    }
}
