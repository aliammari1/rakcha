<?php

namespace App\Form;

use App\Entity\Commande;
use Symfony\Bridge\Twig\AppVariable;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;

class CommandeType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            
        ->add('numTelephone', TextType::class, [
            'label' => 'Phone Number',
            'attr' => ['placeholder' => 'Enter your Phone Number'],
        ])


        ->add('adresse', TextType::class, [
            'label' => 'Address',
            'attr' => [
                'placeholder' => 'Enter your address',
                
            ],
        ]);

        
}

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Commande::class,
            'selectedItemIds' => null
        ]);
    }
}
