package br.com.socialmedia.socialmedia.dto.request;

import br.com.socialmedia.socialmedia.dto.request.ProductRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PostPublishRequest {

        @Min(value = 1, message = "user_id must be greater than zero")
        private int userId;

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
        @Max(value = 10_000_000, message = "price must be <= 10000000")
        private Double price;

        public PostPublishRequest() {}

        public int getUserId() {
                return userId;
        }

        public void setUserId(int userId) {
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

        public Double getPrice() {
                return price;
        }

        public void setPrice(Double price) {
                this.price = price;
        }
}