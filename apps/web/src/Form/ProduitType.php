<?php

namespace App\Form;

use App\Entity\CategorieProduit;
use App\Entity\Produit;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class ProduitType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nom', null, [
                'label' => 'Name',
                'attr' => [
                    'placeholder' => 'Enter your name',
                ],
            ])
            ->add('prix', null, [

                'label' => 'Price',

                'attr' => [
                    'placeholder' => 'Enter your Price she must be a number',
                ],
            ])
            ->add('description', TextareaType::class, [


                'label' => 'Description',
                'attr' => [
                    'placeholder' => 'Enter your description she must be more than 20 characters',
                ],
            ])
            ->add('quantitep', null, [


                'label' => 'Qantity',
                'attr' => [
                    'placeholder' => 'Enter your quantity she must be a number',
                ],
            ])
            ->add('idCategorieproduit', EntityType::class, [
                'class' => CategorieProduit::class,
                'choice_label' => 'nomcategorie',
                'placeholder' => 'Choisir une catégorie',


                'label' => 'Category',
                'attr' => [
                    'placeholder' => 'Shoose your category',
                ],
            ])
            ->add('image', FileType::class, [
                'label' => 'Picture',
                'mapped' => false,
                'required' => false,

                'attr' => [
                    'placeholder' => 'shoose your picture',
                ],


            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Produit::class,
        ]);
    }
}
