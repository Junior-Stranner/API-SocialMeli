package br.com.socialmedia.socialmedia.dto.response;

import br.com.socialmedia.socialmedia.dto.SellerDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PostResponse {

    private int id;
    private SellerDto seller;
    private LocalDate date;
    private ProductResponse product;
    private int category;
    private BigDecimal price;
    private boolean hasPromo;
    private BigDecimal discount;

    // Construtor vazio
    public PostResponse() {}

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public SellerDto getSeller() { return seller; }
    public void setSeller(SellerDto seller) { this.seller = seller; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public ProductResponse getProduct() { return product; }
    public void setProduct(ProductResponse product) { this.product = product; }

    public int getCategory() { return category; }
    public void setCategory(int category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isHasPromo() { return hasPromo; }
    public void setHasPromo(boolean hasPromo) { this.hasPromo = hasPromo; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
}