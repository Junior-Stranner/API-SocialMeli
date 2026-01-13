package br.com.socialmedia.socialmedia.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PromoCountResponse(
        @JsonProperty("user_id") long userId,
        @JsonProperty("user_name") String userName,
        @JsonProperty("promo_products_count") long promoProductsCount
) {}