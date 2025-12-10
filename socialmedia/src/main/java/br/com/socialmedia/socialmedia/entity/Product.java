package br.com.socialmedia.socialmedia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(name = "product_name", nullable = false, length = 40)
    @NotBlank
    @Size(max = 40)
    private String name;

    @Column(name = "product_type", nullable = false, length = 40)
    @NotBlank
    @Size(max = 40)
    private String type;

    @Column(name = "product_brand", nullable = false, length = 40)
    @NotBlank
    @Size(max = 40)
    private String brand;

    @Column(name = "product_color", nullable = false, length = 15)
    @NotBlank
    @Size(max = 15)
    private String color;

    @Column(name = "product_notes", length = 80)
    @Size(max = 80)
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", message = "Notas não podem conter caracteres especiais")
    private String notes;

    @Column(name = "category", nullable = false)
    @NotNull
    private int categoryId;

    public Product(String name, String type, String brand, String color, String notes) {
        this.name = name;
        this.type = type;
        this.brand = brand;
        this.color = color;
        this.notes = notes;
    }

    public Product() {

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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
