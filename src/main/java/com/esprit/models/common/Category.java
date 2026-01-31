package com.esprit.models.common;

import com.esprit.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor

public class Category {

    private  String name;
    private  String description;
    private  CategoryType type;
    private Long id;

    /**
     * Creates a Category with the given name and description and initializes film and actor lists as empty.
     *
     * @param name        the category name
     * @param description the category description
     * @param type        the category type
     */
    public Category(final String name, final String description, final CategoryType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }
}


