package com.danielj.springadsandroid.dto;

/**
 * DTO class for Category
 *
 * @author Daniel Johansson
 */
public class CategoryDto {
    /**
     * Name of category
     */
    private String name;

    /* Constructors */

    public CategoryDto() {
    }

    public CategoryDto(String name) {
        this.name = name;
    }

    /* Getters and setters */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
