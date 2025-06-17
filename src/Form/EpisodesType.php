<?php

namespace App\Form;

use App\Entity\Episodes;
use App\Entity\Series;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\File;
use Symfony\Component\Validator\Constraints\NotBlank;


class EpisodesType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('titre')
            ->add('numeroepisode')
            ->add('saison')
            ->add('image', FileType::class, [
                'label' => ' Image',
                'mapped' => false,
                'required' => false,
            ])
            ->add('video', FileType::class, [
                'label' => 'Video',
                'mapped' => false,
                'required' => true,
                'constraints' => [
                    new File([
                        'mimeTypes' => [
                            'video/mp4',
                            'video/mpeg',
                            // Ajoutez ici d'autres types de fichiers vidéo acceptés si nécessaire
                        ],
                        'mimeTypesMessage' => 'Veuillez télécharger un fichier vidéo valide (MP4, MPEG, etc.)',
                    ]),
                ],
            ])
            ->add('idserie', EntityType::class, [
                'class' => Series::class,
                'choice_label' => 'nom',
                'placeholder' => 'Sélectionnez une série',
                'constraints' => [
                    new NotBlank(),
                ],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Episodes::class,
        ]);
    }
}
