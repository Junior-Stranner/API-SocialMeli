package br.com.socialmedia.socialmedia.dto.response;

import br.com.socialmedia.socialmedia.dto.SellerDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PostResponse {

    @JsonProperty("user_id")
    private long userId;

    @JsonProperty("post_id")
    private long postId;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private ProductResponse product;

    private int category;

    private BigDecimal price;

    @JsonProperty("has_promo")
    private Boolean hasPromo;


    private double discount;

    @JsonProperty("is_seller")
    private SellerDto seller;

    public PostResponse() {
    }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public void setPostId(long postId) { this.postId = postId; }

    public SellerDto getSeller() { return seller; }
    public void setSeller(SellerDto seller) { this.seller = seller; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Boolean getHasPromo() { return hasPromo; }
    public void setHasPromo(Boolean hasPromo) { this.hasPromo = hasPromo; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public long getPostId() { return postId; } // opcional, mas recomendado
    public ProductResponse getProduct() { return product; }
    public void setProduct(ProductResponse product) { this.product = product; }

    public int getCategory() { return category; }
    public void setCategory(int category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}