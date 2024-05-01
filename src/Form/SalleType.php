<?php


namespace App\Form;

use App\Entity\Salle;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\GreaterThan;
use Symfony\Component\Validator\Constraints\Type;

class SalleType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nomSalle', null, [
                'label' => 'Room Name',
            ])
            ->add('nbPlaces', null, [
                'constraints' => [
                    new Type([
                        'type' => 'numeric',
                        'message' => 'The value must be numeric',
                    ]),
                    new GreaterThan([
                        'value' => 0,
                        'message' => 'The number of seats must be greater than 0',
                    ]),
                ],
                'label' => 'Number of Seats',
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Salle::class,
        ]);
    }
}

