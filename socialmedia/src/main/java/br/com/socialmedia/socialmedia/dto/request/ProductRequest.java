package br.com.socialmedia.socialmedia.dto.request;

import jakarta.validation.constraints.*;

public record ProductRequest(
        @Min(value = 1, message = "product_id must be greater than zero")
        int productId,

        @NotBlank @Size(max = 40)
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]+$", message = "product_name must not contain special characters")
        String product_name,

        @NotBlank @Size(max = 15)
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]+$", message = "type must not contain special characters")
        String type,

        @NotBlank @Size(max = 25)
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]+$", message = "brand must not contain special characters")
        String brand,

        @NotBlank @Size(max = 15)
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]+$", message = "color must not contain special characters")
        String color,

        @Size(max = 80)
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 ]*$", message = "notes must not contain special characters")
        String notes
) {}
