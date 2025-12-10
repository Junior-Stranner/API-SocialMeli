package br.com.socialmedia.socialmedia.dto.response;

import br.com.socialmedia.socialmedia.dto.SellerDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PostResponse(int id,
                           SellerDto seller,
                           LocalDate date,
                           ProductResponse product,
                           int category,
                           BigDecimal price,
                           boolean hasPromo,
                           BigDecimal discount)
{}
