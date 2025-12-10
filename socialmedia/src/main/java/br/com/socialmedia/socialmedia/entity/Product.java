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
    private Long productId;

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
    private Integer categoryId;

    public Product(String name, String type, String brand, String color, String notes) {
        this.name = name;
        this.type = type;
        this.brand = brand;
        this.color = color;
        this.notes = notes;
    }
}
