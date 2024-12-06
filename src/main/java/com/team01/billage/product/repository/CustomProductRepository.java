package com.team01.billage.product.repository;

import com.team01.billage.product.dto.OnSaleResponseDto;
import com.team01.billage.product.dto.ProductResponseDto;
import java.util.List;

public interface CustomProductRepository {

    List<OnSaleResponseDto> findAllOnSale(String email);

    List<ProductResponseDto> findAllProductsByCategoryId(Long categoryId, Long userId);
}
