package br.com.socialmedia.socialmedia.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProductRequest {

        @Min(value = 1, message = "product_id must be greater than zero")
        private int productId;

        @NotBlank
        @Size(max = 40)
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]+$", message = "product_name must not contain special characters")
        private String productName;

        @NotBlank
        @Size(max = 15)
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]+$", message = "type must not contain special characters")
        private String type;

        @NotBlank
        @Size(max = 25)
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]+$", message = "brand must not contain special characters")
        private String brand;

        @NotBlank
        @Size(max = 15)
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]+$", message = "color must not contain special characters")
        private String color;

        @Size(max = 80)
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]*$", message = "notes must not contain special characters")
        private String notes;

        public ProductRequest() {
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