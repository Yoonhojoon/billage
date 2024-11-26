package com.team01.billage.product.service;

import com.team01.billage.category.domain.Category;
import com.team01.billage.category.repository.CategoryRepository;
import com.team01.billage.exception.CustomException;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.dto.ProductDeleteCheckDto;
import com.team01.billage.product.dto.ProductDetailResponseDto;
import com.team01.billage.product.dto.ProductRequestDto;
import com.team01.billage.product.dto.ProductResponseDto;
import com.team01.billage.product.enums.RentalStatus;
import com.team01.billage.product.repository.ProductRepository;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.product_review.repository.ProductReviewRepository;
import com.team01.billage.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.team01.billage.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductReviewRepository productReviewRepository;

    @Transactional
    public ProductDetailResponseDto findProduct(Long productId) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        product.increaseViewCount(); // 조회수 단순 증가

        return toDetailDto(product);
    }

    public List<ProductResponseDto> findAllProducts() {
        List<Product> products = productRepository.findAllByDeletedAtIsNull();
        return products.stream()
                .map(product -> ProductResponseDto.builder()
                        .productId(product.getId())
                        .title(product.getTitle())
                        .updatedAt(product.getUpdatedAt())
                        .dayPrice(product.getDayPrice())
                        .weekPrice(product.getWeekPrice())
                        .viewCount(product.getViewCount())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDetailResponseDto createProduct(ProductRequestDto productRequestDto) {

        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .category(category)
                .title(productRequestDto.getTitle())
                .description(productRequestDto.getDescription())
                .dayPrice(productRequestDto.getDayPrice())
                .weekPrice(productRequestDto.getWeekPrice())
                .latitude(productRequestDto.getLatitude())
                .longitude(productRequestDto.getLongitude())
                .build();

        Product createProduct = productRepository.save(product);

        return toDetailDto(createProduct);

    }

    @Transactional
    public ProductDetailResponseDto updateProduct(Long productId, ProductRequestDto productRequestDto) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (product.getRentalStatus() != RentalStatus.AVAILABLE) {
            throw new CustomException(PRODUCT_MODIFICATION_NOT_ALLOWED);
        }

        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        product.updateProductCategory(category);
        product.updateProduct(productRequestDto);

        return toDetailDto(product);

    }

    @Transactional
    public ProductDeleteCheckDto deleteProduct(Long productId) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (product.getRentalStatus() != RentalStatus.AVAILABLE) {
            throw new CustomException(PRODUCT_MODIFICATION_NOT_ALLOWED);
        }

        product.deleteProduct();

        return ProductDeleteCheckDto.builder()
                .productId(productId)
                .deletedAt(product.getDeletedAt())
                .build();

    }

    private ProductDetailResponseDto toDetailDto(Product product) {

        List<ShowReviewResponseDto> reviews = productReviewRepository.findByProduct_id(
                product.getId());
        Users seller = product.getSeller();

        return ProductDetailResponseDto.builder()
                .categoryName(product.getCategory().getName())
                .title(product.getTitle())
                .description(product.getDescription())
                .rentalStatus(product.getRentalStatus().getDisplayName())
                .dayPrice(product.getDayPrice())
                .weekPrice(product.getWeekPrice())
                .latitude(product.getLatitude())
                .longitude(product.getLongitude())
                .viewCount(product.getViewCount())
                .updatedAt(product.getUpdatedAt())
                .sellerId(seller.getId())
                .sellerNickname(seller.getNickname())
                .sellerImageUrl(seller.getImageUrl())
                .reviews(reviews)
                .build();

    }

}
