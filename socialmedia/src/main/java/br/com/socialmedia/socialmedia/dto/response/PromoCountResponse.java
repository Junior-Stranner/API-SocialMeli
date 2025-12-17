package br.com.socialmedia.socialmedia.dto.response;


public record PromoCountResponse(
        int user_id,
        String user_name,
        int promo_products_count
) {}
