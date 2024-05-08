<?php

namespace App\Controller;

use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;

class MicrosoftController extends AbstractController
{
    #[Route("/connect/microsoft", name: "connect_microsoft_start")]
    public function connectAction(ClientRegistry $clientRegistry)
    {
        // will redirect to Microsoft!
        return $clientRegistry
            ->getClient('microsoft') // key used in config/packages/knpu_oauth2_client.yaml
            ->redirect([
                'wl.basic', 'wl.signin', 'wl.birthday',
                 'wl.emails','wl.phone_numbers', 'wl.postal_addresses'
            ], []);
    }


    #[Route("/connect/microsoft/check", name: "connect_microsoft_check")]
    public function connectCheckAction(Request $request, ClientRegistry $clientRegistry)
    {
        // ** if you want to *authenticate* the user, then
        // leave this method blank and create a Guard authenticator
        // (read below)

        // /** @var \KnpU\OAuth2ClientBundle\Client\Provider\MicrosoftClient $client */
        // $client = $clientRegistry->getClient('microsoft');

        // try {
        //     // the exact class depends on which provider you're using
        //     /** @var \League\OAuth2\Client\Provider\MicrosoftUser $user */
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
