package br.com.socialmedia.socialmedia.dto.response;

import br.com.socialmedia.socialmedia.dto.SellerDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.time.LocalDate;

public class PostResponse {

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("post_id")
    private int postId;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private ProductResponse product;

    private int category;

    private Double price;

    @JsonProperty("has_promo")
    private Boolean hasPromo;

    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "1.0", inclusive = false)
    private Double discount;

    @JsonProperty("is_seller")
    private SellerDto seller;

    public PostResponse() {
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public void setPostId(int postId) { this.postId = postId; }

    public SellerDto getSeller() { return seller; }
    public void setSeller(SellerDto seller) { this.seller = seller; }
}