package kr.or.kosa.cmsplusmain.domain.product.dto;

import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProductRes {

    private final Long productId;
    private final String productName;
    private final LocalDate productCreatedDate;
    private final int productPrice;
    private final String productMemo;
    private final int contractNumber; // 계약 건수

    public static ProductRes fromEntity(Product product, int contractNumber) {
        return ProductRes.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productCreatedDate(product.getCreatedDateTime().toLocalDate())
                .productPrice(product.getPrice())
                .productMemo(product.getMemo())
                .contractNumber(contractNumber)
                .build();
    }

}