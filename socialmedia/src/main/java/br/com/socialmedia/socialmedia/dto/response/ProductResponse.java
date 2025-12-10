package br.com.socialmedia.socialmedia.dto.response;

public record ProductResponse(  int productId,
                                String name,
                                String type,
                                String brand,
                                String color,
                                String notes,
                                int categoryId) {
}
