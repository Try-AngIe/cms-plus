package kr.or.kosa.cmsplusmain.domain.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import lombok.Getter;

@Getter
public class ProductQRes {

    private final Product product;
    private final int contractNumber;

    // 쿼리프로젝션을 이용해서 repository->service용 DTO
    @QueryProjection
    public ProductQRes(Product product, int contractNumber) {
        this.product = product;
        this.contractNumber = contractNumber;
    }

    public ProductRes toProductRes() {
        return ProductRes.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .productMemo(product.getMemo())
                .contractNumber(contractNumber)
                .productCreatedDate(product.getCreatedDateTime().toLocalDate()) // 엔티티에서 가져옴
                .build();
    }

}