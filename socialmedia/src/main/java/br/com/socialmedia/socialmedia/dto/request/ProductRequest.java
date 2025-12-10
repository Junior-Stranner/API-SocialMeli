package br.com.socialmedia.socialmedia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductRequest(@NotNull int productId,
                             @NotBlank @Size(max = 40) String name,
                             @NotBlank @Size(max = 40) String type,
                             @NotBlank @Size(max = 40) String brand,
                             @NotBlank @Size(max = 15) String color,
                             @Size(max = 80) @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$") String notes,
                             @NotNull int categoryId)
{}
