package com.team01.billage.product.domain;

import com.team01.billage.category.domain.Category;
import com.team01.billage.product.dto.ProductRequestDto;
import com.team01.billage.product.enums.RentalStatus;
import com.team01.billage.user.domain.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Users seller;

    // 판매지역

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "rental_status", nullable = false)
    @Builder.Default
    private RentalStatus rentalStatus = RentalStatus.AVAILABLE;

    @Column(nullable = false)
    private int dayPrice;

    private Integer weekPrice; // 선택값 (null 가능)

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private int viewCount = 0;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> productImages = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void updateProduct(ProductRequestDto dto) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.dayPrice = dto.getDayPrice();
        this.weekPrice = dto.getWeekPrice();
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
    }

    public void updateProductCategory(Category category) {
        this.category = category;
    }

    public void updateRentalStatus(RentalStatus status) {
        this.rentalStatus = status;
    }

    public void deleteProduct() {
        this.deletedAt = LocalDateTime.now();
        this.productImages.clear();
    }

    public void increaseViewCount() {
        this.viewCount += 1;
    }

    public void addProductImage(ProductImage productImage){
        this.productImages.add(productImage);
    }

}
