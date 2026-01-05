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

}