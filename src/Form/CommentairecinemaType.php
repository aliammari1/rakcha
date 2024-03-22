<?php

namespace App\Form;

use App\Entity\Commentairecinema;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class CommentairecinemaType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('idclient')
            ->add('idcinema')
            ->add('commentaire')
            ->add('sentiment')
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Commentairecinema::class,
        ]);
    }
}
