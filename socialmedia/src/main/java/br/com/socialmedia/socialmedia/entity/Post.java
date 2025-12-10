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
}
