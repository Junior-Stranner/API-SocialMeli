package br.com.socialmedia.socialmedia.dto.response;


public record PromoCountResponse(
        int user_id,
        String userName,
        int count
) {}
