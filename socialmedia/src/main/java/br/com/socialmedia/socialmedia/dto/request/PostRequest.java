package br.com.socialmedia.socialmedia.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PostRequest(int sellerId,
                          LocalDate date,
                          ProductRequest product,
                          int category,
                          BigDecimal price,
                          boolean hasPromo,
                          BigDecimal discount) {
}
