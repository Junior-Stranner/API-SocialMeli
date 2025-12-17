package br.com.socialmedia.socialmedia.dto.response;

public record ProductResponse(
        int product_id,
        String product_name,
        String type,
        String brand,
        String color,
        String notes
) {}