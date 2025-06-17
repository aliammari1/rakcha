<?php

namespace App\Form;

use App\Entity\Cinema;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\DataTransformerInterface;
use Symfony\Component\Form\Exception\TransformationFailedException;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\HttpFoundation\File\File as SymfonyFile;
use Symfony\Component\OptionsResolver\OptionsResolver;

class CinemaType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nom', null, [

                'label' => 'Name',
            ])
            ->add('adresse', null, [
                'label' => 'Address',
            ])
            ->add('logo', FileType::class, [
                'label' => 'Choose an image',
                'required' => true,
            ]);

        // Ajouter un view transformer pour convertir la chaîne de chemin d'accès en objet SymfonyFile
        $builder->get('logo')->addModelTransformer(new class() implements DataTransformerInterface {
            public function transform($value)
            {
                // transforme l'objet SymfonyFile en chaîne de chemin d'accès
                return null;
            }

            public function reverseTransform($value)
            {
                // transforme la chaîne de chemin d'accès en objet SymfonyFile
                if (!$value instanceof SymfonyFile) {
                    if ($value === null) {
                        return null;
                    }
                    try {
                        return new SymfonyFile($value);
                    } catch (\Exception $e) {
                        throw new TransformationFailedException(sprintf('An error occurred: %s', $e->getMessage()));
                    }
                }

                return $value;
            }
        });
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Cinema::class,
        ]);
    }
}
