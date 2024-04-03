<?php

namespace App\Form;

use App\Entity\Users;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\EmailType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\TelType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\IsTrue;

class UsersType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nom', TextType::class, [
                'required' => true,
                'label' => 'Last Name',
                'attr' => [
                    'placeholder' => 'Enter your last name',
                ],
            ])
            ->add('prenom', TextType::class, [
                'required' => true,
                'label' => 'First Name',
                'attr' => [
                    'placeholder' => 'Enter your first name',
                ],
            ])
            ->add('numTelephone', TelType::class, [
                'required' => true,
                'label' => 'Phone Number',
                'attr' => [
                    'placeholder' => 'Enter your phone number',
                ],
            ])
            ->add('password', PasswordType::class, [
                'required' => true,
                'trim' => true,
                'label' => 'Password',
                'attr' => [
                    'placeholder' => 'Enter your password',
                ],
            ])
            ->add('role', ChoiceType::class, [
                'required' => true,
                'label' => 'Role',
                'choices' => [
                    'Client' => 'client',
                    'Administrateur' => 'admin',
                    'Responsable de cinema' => 'responsableDeCinema',
                ],
                'attr' => [
                    'placeholder' => 'Select your role',
                ],
            ])
            ->add('adresse', TextType::class, [
                'required' => true,
                'label' => 'Address',
                'attr' => [
                    'placeholder' => 'Enter your address',
                ],
            ])
            ->add('dateDeNaissance', DateType::class, [
                'required' => true,
                'label' => 'Date of Birth',
                'widget' => 'single_text',
                'format' => 'yyyy-MM-dd',
                'attr' => [
                    'placeholder' => 'Select your date of birth',
                ],
            ])
            ->add('email', EmailType::class, [
                'required' => true,
                'label' => 'Email Address',
                'attr' => [
                    'placeholder' => 'Enter your email address',
                ],
            ])
            ->add('photoDeProfil', FileType::class, [
                'mapped' => false,
                'required' => false,
                'label' => 'Profile Photo',
                'attr' => [
                    'placeholder' => 'Choose your profile photo',
                ],
            ])
            ->add('agreeTerms', CheckboxType::class, [
                'mapped' => false,
                'label' => 'Agree to Terms',
                'constraints' => [
                    new IsTrue([
                        'message' => 'You should agree to our terms.',
                    ]),
                ],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Users::class,
        ]);
    }
}
