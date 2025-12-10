package br.com.socialmedia.socialmedia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(@NotBlank @Size(max = 80) String name,
                          boolean seller) {
}
