<?php

namespace App\Form;

use App\Entity\Seance;
use App\Entity\Cinema;
use App\Entity\Salle;
use App\Repository\CinemaRepository;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\FormEvent;
use Symfony\Component\Form\FormEvents;
use Symfony\Component\Form\FormInterface;

class SeanceType extends AbstractType
{
    private $cinemaRepository;

    public function __construct(CinemaRepository $cinemaRepository)
    {
        $this->cinemaRepository = $cinemaRepository;
    }

    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('idCinema', EntityType::class, [
                'class' => Cinema::class,
                'choice_label' => 'nom',
                'placeholder' => 'Choose a cinema',
                'constraints' => [
                    new NotBlank(),
                ],
                'label' => 'Cinema',
                'query_builder' => function (CinemaRepository $er) {
                    return $er->createQueryBuilder('c')
                        ->where('c.statut = :statut')
                        ->setParameter('statut', 'Accepted');
                },
            ])
            ->add('idSalle', EntityType::class, [
                'class' => Salle::class,
                'placeholder' => 'Choose a room',
                'choice_label' => 'nomSalle',
                'label' => 'Room',
            ])
            ->add('idFilm', EntityType::class, [
                'class' => \App\Entity\Film::class,
                'choice_label' => 'nom',
                'placeholder' => 'Choose a film',
                'label' => 'Film',
            ])
            ->add('date')
            ->add('hd')
            ->add('hf')
            ->add('prix');

        $formModifier = function (FormInterface $form, Cinema $cinema = null) {
            $salles = $cinema ? $cinema->getSalles() : [];
            $films = $cinema ? $cinema->getFilmCinemas()->map(function($filmCinema) {
                return $filmCinema->getFilm();
            })->toArray() : [];

            $form->add('idSalle', EntityType::class, [
                'class' => Salle::class,
                'choices' => $salles,
                'required' => false,
                'choice_label' => 'nomSalle',
                'placeholder' => 'Choose a room',
                'attr' => ['class' => 'custom-select'],
                'label' => 'Room'
            ]);

            $form->add('idFilm', EntityType::class, [
                'class' => \App\Entity\Film::class,
                'choices' => $films,
                'required' => false,
                'choice_label' => 'nom',
                'placeholder' => 'Choose a film',
                'attr' => ['class' => 'custom-select'],
                'label' => 'Film'
            ]);
        };

        
        $builder->get('idCinema')->addEventListener(
            FormEvents::POST_SUBMIT,
            function (FormEvent $event) use ($formModifier) {
                $cinema = $event->getForm()->getData();
                $formModifier($event->getForm()->getParent(), $cinema);
            }
        );

        
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => Seance::class,
        ]);
    }
}