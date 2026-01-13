package br.com.socialmedia.socialmedia.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PostPublishRequest {

        @NotNull
        @Positive
        private Long userId;

        @NotNull(message = "date must not be null")
        @JsonFormat(pattern = "dd-MM-yyyy")
        @FutureOrPresent(message = "date must be today or in the future")
        private LocalDate date;

        @Valid
        @NotNull(message = "product must not be null")
        private ProductRequest product;

        @Min(value = 1, message = "category must be greater than zero")
        private int category;

        @NotNull(message = "price must not be null")
        @DecimalMax(value = "10000000.00")
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal price;

        public PostPublishRequest() {}

        public Long getUserId() {
                return userId;
        }

        public void setUserId(Long userId) {
                this.userId = userId;
        }

        public LocalDate getDate() {
                return date;
        }

        public void setDate(LocalDate date) {
                this.date = date;
        }

        public ProductRequest getProduct() {
                return product;
        }

        public void setProduct(ProductRequest product) {
                this.product = product;
        }

        public int getCategory() {
                return category;
        }

        public void setCategory(int category) {
                this.category = category;
        }

        public BigDecimal getPrice() {
                return price;
        }

        public void setPrice(BigDecimal price) {
                this.price = price;
        }
}