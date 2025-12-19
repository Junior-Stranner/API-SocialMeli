package br.com.socialmedia.socialmedia.dto.response;


public record PromoCountResponse(
        int userId,
        String userName,
        int count
) {}
