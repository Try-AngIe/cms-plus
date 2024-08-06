package kr.or.kosa.cmsplusmain.domain.product.controller;

import java.util.List;

import kr.or.kosa.cmsplusmain.LogExecutionTime;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageRes;
import kr.or.kosa.cmsplusmain.domain.base.security.VendorId;
import kr.or.kosa.cmsplusmain.domain.product.dto.*;
import kr.or.kosa.cmsplusmain.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendor/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록
     * */
    @PostMapping
    public void createProduct(@VendorId Long vendorId, @RequestBody ProductCreateReq product) {
        productService.createProduct(vendorId, product);
    }

    /**
     * 상품 목록 전체 조회 (검색 조건, 정렬 조건)
     * */

    @GetMapping
    public PageRes<ProductListItemRes> getProductListItem(@VendorId Long vendorId, ProductSearchReq search, PageReq pageable) { // 검색 및 필터링 조건DTO, 페이지네이션DTO
        return productService.searchProducts(vendorId, search, pageable);
    }

    /**
     * 상품 상세 조회
     * */
    @GetMapping("/{productId}")
    public ProductDetailRes getProduct(@VendorId Long vendorId, @PathVariable("productId") Long productId) {
        return productService.getProductDetail(productId, vendorId);
    }

    /**
     * 상품 수정
     * */
    @PutMapping("/{productId}")
    public void updateProduct(@VendorId Long vendorId, @PathVariable("productId") Long productId, @RequestBody ProductUpdateReq productUpdateReq) {
        productService.updateProduct(vendorId, productId, productUpdateReq);
    }

    /**
     * 상품 삭제
     * */
    @DeleteMapping("/{productId}")
    public void deleteProduct(@VendorId Long vendorId, @PathVariable("productId") Long productId) {
        productService.deleteProduct(vendorId, productId);
    }

    /**
    * 전체 상품 목록 (조건 없이, 이름과 수량 그리고 ID 만)
    * */
    @GetMapping("/all")
    public List<ProductDto> getAllProducts(@VendorId Long vendorId) {
        return productService.getAllProducts(vendorId);
    }
}
