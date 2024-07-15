package kr.or.kosa.cmsplusmain.domain.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter // 세터 필요
public class ProductSearch { // final 금지(@RequestParam으로 받는게 아님)

    /****** 검색 가능 항목 *******/
    private String productName;
    private String productMemo;

    /****** 날짜 선택 항목 *******/
    private LocalDate productCreatedDate;

    /****** 숫자 이하 항목 *******/
    private Integer productPrice;
    private Integer contractNumber;

}
