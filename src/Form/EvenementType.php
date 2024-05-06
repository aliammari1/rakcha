<?php

namespace App\Form;

use App\Entity\Evenement;
use App\Entity\CategorieEvenement; // Add this line
use Symfony\Bridge\Doctrine\Form\Type\EntityType; // Add this line
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class EvenementType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nom')
            ->add('datedebut')
            ->add('datefin')
            ->add('lieu')
            ->add('etat')
            ->add('description')
            ->add('afficheEvent')
            ->add('idCategorie', EntityType::class, [
                'class' => CategorieEvenement::class,
                'choice_label' => 'nomCategorie',
                'attr' => ['class' => 'form-control'],
            ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Evenement::class,
        ]);
    }
}