<?php

namespace App\Form;

use App\Entity\Actor;
use App\Entity\Category;
use App\Entity\Film;
use App\Form\Type\CheckboxDropdownType;
use Doctrine\ORM\Mapping\Entity;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\TimeType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class FilmType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nom')
            ->add('image', FileType::class, [
                'label' => 'Image',
                'mapped' => false,
                
            ])
            ->add('duree', TimeType::class, [
                'widget' => 'single_text',
                "with_seconds" => true,
            ])
            ->add('description', TextareaType::class)
            ->add('annederalisation', ChoiceType::class, [
                'choices' => array_combine(range(date('Y'), date('Y') - 74), range(date('Y'), date('Y') - 74)),
                'label' => 'Year',
            ])
            ->add('actors', EntityType::class, [
                'class' => Actor::class,
                'choice_label' => 'nom',
                'multiple' => true,
                'expanded' => true,
            ])
            ->add('categorys', EntityType::class, [
                'class' => Category::class,
                'choice_label' => 'nom',
                'multiple' => true,
                'expanded' => true,
            ])
        ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Film::class,
        ]);
    }
}
