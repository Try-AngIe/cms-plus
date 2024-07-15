package kr.or.kosa.cmsplusmain.domain.product.controller;

import kr.or.kosa.cmsplusmain.domain.base.dto.SortPageDto;
import kr.or.kosa.cmsplusmain.domain.product.dto.ProductRes;
import kr.or.kosa.cmsplusmain.domain.product.dto.ProductSearch;
import kr.or.kosa.cmsplusmain.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vendor/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ProductRes getProduct(@PathVariable("productId") Long productId) {
        String vendorUserName = "vendor1"; // 고객1 더미데이터
        return productService.findById(productId, vendorUserName);
    }

    @GetMapping
    public SortPageDto.Res<ProductRes> getProductListItem(ProductSearch search, SortPageDto.Req pageable) { // 검색 및 필터링 조건DTO, 페이지네이션DTO
        String vendorUserName = "vendor1";
        return productService.findProductsByUser(vendorUserName, search, pageable);
    }

}
