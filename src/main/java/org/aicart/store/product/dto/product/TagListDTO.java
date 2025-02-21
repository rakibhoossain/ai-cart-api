package org.aicart.store.product.dto.product;

public class TagListDTO {
    private long id;

    public TagListDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }

    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
