package br.com.socialmedia.socialmedia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Product {

    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(name = "product_name", nullable = false, length = 40)
    private String productName;

    @Column(name = "type", nullable = false, length = 15)
    private String type;

    @Column(name = "brand", nullable = false, length = 25)
    private String brand;

    @Column(name = "color", nullable = false, length = 15)
    private String color;

    @Column(name = "notes", length = 80)
    private String notes;


    public Product() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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