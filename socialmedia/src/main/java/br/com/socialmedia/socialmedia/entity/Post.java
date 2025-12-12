package br.com.socialmedia.socialmedia.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(name = "post_date", nullable = false)
    private LocalDate date;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int category;

    @Column(nullable = false, precision = 12, scale = 2)
    @DecimalMax(value = "10000000.00")
    @DecimalMin("0.01")
    private BigDecimal price;

    @Column(name = "has_promo", nullable = false)
    private boolean hasPromo;

    @Column(name = "discount", precision = 5, scale = 2)
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal discount;

    public Post() {
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
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

    public boolean isHasPromo() {
        return hasPromo;
    }

    public void setHasPromo(boolean hasPromo) {
        this.hasPromo = hasPromo;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}
