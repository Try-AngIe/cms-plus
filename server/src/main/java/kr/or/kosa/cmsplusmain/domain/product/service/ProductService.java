package kr.or.kosa.cmsplusmain.domain.product.service;

import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.base.dto.PageRes;
import kr.or.kosa.cmsplusmain.domain.product.dto.*;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.product.exception.DeleteProductException;
import kr.or.kosa.cmsplusmain.domain.product.repository.ProductCustomRepository;
import kr.or.kosa.cmsplusmain.domain.product.repository.ProductRepository;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;

    /**
    * 상품 생성
    * */
    @Transactional
    public void createProduct(Long vendorId, ProductCreateReq productCreateReq) {

        productRepository.save(productCreateReq.toEntity(Vendor.builder().id(vendorId).build()));
    }

    /**
     * 상품 목록 조회
     * */
    public PageRes<ProductListItemRes> searchProducts(Long vendorId, ProductSearchReq search, PageReq pageable) {
        List<ProductListItemRes> content = productCustomRepository.findProductListWithCondition(vendorId, search, pageable)
                .stream()
                .map(ProductQueryDto::toProductRes)
                .toList();

        int totalContentCount = productCustomRepository.countAllProductsWithCondition(vendorId, search); // search 추가

        return new PageRes<>(totalContentCount, pageable.getSize(), content);
    }

    /**
     * 상품 상세 조회
     * */
    public ProductDetailRes getProductDetail(Long productId, Long vendorId) {
        // 해당 상품들 주인이 맞는지 검증
        validateProductUser(productId, vendorId);

        // 계약 건수
        int contractNum = productCustomRepository.getContractNumber(productId);

        // 상품 정보(계약건수를 제외한)
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // 계약 건수 + 상품 정보
        return ProductDetailRes.fromEntity(product, contractNum);
    }

    public List<ProductDto> getAllProducts(Long vendorId) {
        return productCustomRepository.findAvailableProductsByVendorUsername(vendorId).stream()
            .map(ProductDto::fromEntity)
            .toList();
    }

    @Deprecated
    public List<ProductListItemRes> findAvailableProductsByVendorUsername(Long vendorId) {
        return productCustomRepository.findAvailableProductsByVendorUsername(vendorId)
            .stream()
            .map(product -> ProductListItemRes.fromEntity(product, productCustomRepository.getContractNumber(product.getId())))
            .collect(Collectors.toList());
    }

    /**
     * 상품 수정
     * */
    @Transactional
    public void updateProduct(Long vendorId, Long productId, ProductUpdateReq productUpdateReq) {
        validateProductUser(productId, vendorId);
        Product product = productRepository.findById(productId).orElseThrow(IllegalStateException::new);
        product.setMemo(productUpdateReq.getProductMemo());
        product.setPrice(productUpdateReq.getProductPrice());
    }

    /**
     * 상품 삭제
     * 삭제 조건은 현재 진행중인 계약이 없는 상품만 삭제가 가능하다.
     * */
    @Transactional
    public void deleteProduct(Long vendorId,Long productId) {
        validateProductUser(productId, vendorId);
        int inProgressContractCount = productCustomRepository.getContractNumber(productId);
        if (inProgressContractCount > 0) {
            throw new DeleteProductException("진행중인 계약이 존재하는 상품은 삭제가 불가능합니다");
        }
        Product product = productRepository.findById(productId).orElseThrow(IllegalStateException::new);
        product.delete();
    }

    /**
     * 유효성 검증
     * */
    private void validateProductUser(Long productId, Long vendorId) {
        if (!productCustomRepository.isExistProductByUsername(productId, vendorId)) {
            throw new IllegalArgumentException("Not Owner");
        }
    }
}