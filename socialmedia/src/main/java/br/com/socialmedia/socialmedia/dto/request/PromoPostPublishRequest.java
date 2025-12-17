package br.com.socialmedia.socialmedia.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PromoPostPublishRequest(
        @Min(1) int user_id,

        @NotNull
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate date,

        @Valid @NotNull
        ProductRequest product,

        @Min(1) int category,

        @NotNull @Max(10_000_000)
        Double price,

        @NotNull
        Boolean has_promo,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false, message = "discount must be > 0")
        @DecimalMax(value = "1.0", inclusive = false, message = "discount must be < 1")
        Double discount
) {}
