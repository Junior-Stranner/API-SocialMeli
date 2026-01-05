package br.com.socialmedia.socialmedia.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class PromoPostPublishRequest {

        @Min(value = 1, message = "user_id must be greater than zero")
        private int userId;

        @NotNull(message = "date must not be null")
        @JsonFormat(pattern = "dd-MM-yyyy")
        private LocalDate date;

        @Valid
        @NotNull(message = "product must not be null")
        private ProductRequest product;

        @Min(value = 1, message = "category must be greater than zero")
        private int category;

        @NotNull(message = "price must not be null")
        @Max(value = 10_000_000, message = "price must be <= 10000000")
        private Double price;

        @JsonProperty("has_promo")
        @NotNull(message = "has_promo must not be null")
        private Boolean hasPromo;

        /**
         * Opção 1 (fração 0..1): mantém sua validação atual
         * Ex.: 0.20 = 20% de desconto
         */
        @NotNull(message = "discount must not be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "discount must be > 0")
        @DecimalMax(value = "1.0", inclusive = false, message = "discount must be < 1")
        private Double discount;

        public PromoPostPublishRequest() {
        }

        public int getUserId() {
                return userId;
        }

        public void setUserId(int userId) {
                this.userId = userId;
        }

        public ProductRequest getProduct() {
                return product;
        }

        public Double getDiscount() {
                return discount;
        }
}