<?php

namespace App\Controller;
use App\Entity\Evenement;
use App\Form\EvenementType;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Dompdf\Dompdf;
use Dompdf\Options;

use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
use Twilio\Rest\Client;

#[Route('/evenement')]
class EvenementController extends AbstractController
{
    #[Route('/', name: 'app_evenement_index', methods: ['GET'])]
public function index(Request $request, EntityManagerInterface $entityManager): Response
{   
    
    $searchTerm = $request->query->get('search');
    $sortBy = $request->query->get('sort_by', 'nom'); // Default sort by 'nom'
    $sortOrder = $request->query->get('sort_order', 'asc'); // Default sort order 'asc'

    $queryBuilder = $entityManager->getRepository(Evenement::class)->createQueryBuilder('e');

    // Search query
    if ($searchTerm) {
        $queryBuilder->where('e.nom LIKE :searchTerm')
                     ->orWhere('e.lieu LIKE :searchTerm')
                     ->orWhere('e.description LIKE :searchTerm')
                     ->setParameter('searchTerm', '%' . $searchTerm . '%');
    }

    // Sorting query
    $queryBuilder->orderBy('e.' . $sortBy, $sortOrder);

    $evenements = $queryBuilder->getQuery()->getResult();

    return $this->render('evenement/index.html.twig', [
        'evenements' => $evenements,
    ]);
}
  #[Route('/affichback', name: 'app_evenement_affichback', methods: ['GET'])]
    public function affichback(Request $request, EntityManagerInterface $entityManager): Response
{
    $searchTerm = $request->query->get('search');
    $sortBy = $request->query->get('sort_by', 'nom'); // Default sort by 'nom'
    $sortOrder = $request->query->get('sort_order', 'asc'); // Default sort order 'asc'

    $queryBuilder = $entityManager->getRepository(Evenement::class)->createQueryBuilder('e');

    // Search query
    if ($searchTerm) {
        $queryBuilder->where('e.nom LIKE :searchTerm')
                     ->orWhere('e.lieu LIKE :searchTerm')
                     ->orWhere('e.description LIKE :searchTerm')
                     ->setParameter('searchTerm', '%' . $searchTerm . '%');
    }

    // Sorting query
    $queryBuilder->orderBy('e.' . $sortBy, $sortOrder);

    $evenements = $queryBuilder->getQuery()->getResult();

    return $this->render('evenement/affichback.html.twig', [
        'evenements' => $evenements,
    ]);
}



    #[Route('/new', name: 'app_evenement_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
{
    $evenement = new Evenement();
    $form = $this->createForm(EvenementType::class, $evenement);

    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        $entityManager->persist($evenement);
        $entityManager->flush();

        return $this->redirectToRoute('app_evenement_affichback');
    }

    return $this->render('evenement/new.html.twig', [
        'evenement' => $evenement,
        'form' => $form->createView(),
    ]);

    $sid    = "ACbdf5d6feda24bb559b232d801c631a65";
    $token  = "42c6d9e04357c619befa9276eb2c3eb7";
    $twilio = new Client($sid, $token);

    $message = $twilio->messages
      ->create("+21622757828", // to
        array(
          "from" => "+14422540252",
          "body" => "New Event has been added !"
        )
      );

print($message->sid);
    
}


    #[Route('/{id}', name: 'app_evenement_show', methods: ['GET'])]
    public function show(Evenement $evenement): Response
    {
        return $this->render('evenement/show.html.twig', [
            'evenement' => $evenement,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_evenement_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Evenement $evenement, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(EvenementType::class, $evenement);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_evenement_affichback', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('evenement/edit.html.twig', [
            'evenement' => $evenement,
            'form' => $form,
        ]);
    }
#[Route('/backshow/{id}', name: 'app_evenement_showback', methods: ['GET'])]
    public function showback(Evenement $evenement): Response
    {
        return $this->render('evenement/showback.html.twig', [
            'evenement' => $evenement,
        ]);
    }
    #[Route('/{id}', name: 'app_evenement_delete', methods: ['POST'])]
    public function delete(Request $request, Evenement $evenement, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$evenement->getId(), $request->request->get('_token'))) {
            $entityManager->remove($evenement);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_evenement_affichback', [], Response::HTTP_SEE_OTHER);
    }

 #[Route('/{id}/pdf', name: 'app_evenement_pdf', methods: ['GET'])]
public function generatePdf(Evenement $evenement): Response
{
    // Create a new instance of Dompdf
    $pdfoptions = new Options();
    $pdfoptions->set('defaultFont', 'Arial');
    $pdfoptions->setIsRemoteEnabled(true); // Enable remote file access if needed

    $dompdf = new Dompdf($pdfoptions);

    // Render HTML for the PDF
    $html = $this->renderView('evenement/pdf.html.twig', [
        'evenement' => $evenement,
    ]);

    $dompdf->loadHtml($html);

    // Set paper size and orientation
    $dompdf->setPaper('A4', 'portrait');

    // Render the PDF
    $dompdf->render();

    // Output the generated PDF to the browser
    return new Response(
        $dompdf->output(),
        200,
        [
            'Content-Type' => 'application/pdf',
            'Content-Disposition' => 'inline; filename="evenement.pdf"',
        ]
    );
}

   #[Route('/trimultifunction', name: 'app_evenement_trimultifunction', methods: ['POST'])]
public function trimultifunction(Request $request): Response
{
    $input = $request->request->get('input');

    if (!$input) {
        return new Response('Input cannot be empty.', Response::HTTP_BAD_REQUEST);
    }

    $trimmedInput = trim($input);

    return new Response($trimmedInput);
}

  
}
