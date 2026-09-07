package com.enviro.assessment.junior.adrianmajavu.dto;

/**
 * What a Product looks like over the API. A simple immutable data holder -
 * no JPA annotations, no relationship to Portfolio - so the frontend never
 * sees database internals or risks triggering lazy-loading errors.
 */
public class ProductDTO {

    private Long id;
    private String name;
    private String type;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
