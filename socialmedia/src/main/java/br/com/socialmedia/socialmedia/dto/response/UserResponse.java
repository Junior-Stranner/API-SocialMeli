package br.com.socialmedia.socialmedia.dto.response;

public record UserResponse(
        int id,
        String name,
        boolean seller
){}
