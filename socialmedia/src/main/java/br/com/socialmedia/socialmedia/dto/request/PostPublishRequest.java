package br.com.socialmedia.socialmedia.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PostPublishRequest {

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

        public PostPublishRequest() {
        }

        public int getUserId() {
                return userId;
        }

        public void setUserId(int userId) {
                this.userId = userId;
        }

}