package kr.or.kosa.cmsplusmain.test.domain.product.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.or.kosa.cmsplusmain.test.domain.product.dto.TProductDto;
import kr.or.kosa.cmsplusmain.test.domain.product.dto.TProductFindAllByConditionRes;
import kr.or.kosa.cmsplusmain.test.domain.product.dto.TProductSaveReq;
import kr.or.kosa.cmsplusmain.test.domain.product.dto.TProductUpdateReq;

@RestController
@RequestMapping("/api/v1/vendor/producttest")
public class TProductController {

	// 초기화
	public TProductController() {
		initData();
	}

	// 상품 리스트
	private final List<TProductDto> productList = new ArrayList<>();

	// 테스트용 더미 데이터 생성 (조회시 등록하지않으나 있다고 가정하고 하나 넣어야함 | 실제 DB 연동으로 대체해야 함)
	public void initData() {

		// 테스트에서 중복 실행 방지 초기화
		productList.clear();
		List<TProductDto> initData = List.of(
			new TProductDto(1L, 1001L, "상품 조회용 데이터1", 10.0, 10, "2024-01-01 11:11:11", "Null", "Null", "비고 1",
				"STATUS1"),
			new TProductDto(2L, 1002L, "상품 조회용 데이터2", 20.0, 22, "2024-01-02 11:11:11", "2024-01-01 11:11:11",
				"2024-01-01", "비고 2", "STATUS2")
		);

		// 더미데이터 세팅
		productList.addAll(initData);

	}

	// 상품 목록 조회
	@GetMapping
	public TProductFindAllByConditionRes findAllProductByCondition(@RequestParam int page, @RequestParam int offset) {

		// /api/v1/vendor/products?page=1&offset=10
		// 페이지네이션 이외에 정렬, 필터, 검색 기능 염두해야 함
		// 페이지네이션과 정렬, 필터링 기능을 구현해야 하지만 여기서는 간단히 더미 데이터를 반환
		List<TProductDto> data = new ArrayList<>(productList);

		// Pagination (실제 로직에 따라 계산 필요)
		int totalPage = 100;
		int totalCount = 1000;

		TProductFindAllByConditionRes tProductFindAllByConditionRes = new TProductFindAllByConditionRes();
		tProductFindAllByConditionRes.setPage(page);
		tProductFindAllByConditionRes.setOffset(offset);
		tProductFindAllByConditionRes.setTotalPage(totalPage);
		tProductFindAllByConditionRes.setTotalCount(totalCount);
		tProductFindAllByConditionRes.setData(data);

		return tProductFindAllByConditionRes;

	}

	// 상품 등록
	@PostMapping
	public TProductDto saveProduct(@RequestBody TProductSaveReq tProductSaveReq) {

		// 새로 등록할 상품
		TProductDto newProduct = new TProductDto(
			tProductSaveReq.getId(),
			tProductSaveReq.getVendorId(),
			tProductSaveReq.getName(),
			tProductSaveReq.getPrice(),
			0, // 이건 DB에서 알아서 할듯
			tProductSaveReq.getCreatedDateTime(),
			null, // 이건 DB에서 알아서 할듯
			null, // 이건 DB에서 알아서 할듯
			tProductSaveReq.getMemo(),
			"STATUS1" // 이건 DB에서 알아서 할듯
		);

		productList.add(newProduct);

		return newProduct;
	}

	// 상품 상세 조회
	@GetMapping("/{PRODUCT_ID}")
	public TProductDto findProductById(@PathVariable("PRODUCT_ID") Long productId) {

		// 람다식으로 해당 상품아이디 매핑
		// 실제 코드에선 DTO를 분리해서 해야하나 현재 기본 TProductDto에 데이터를 강제로 주입시키기 때문에 애로사항이 있음
		TProductDto product = productList.stream()
			.filter(p -> p.getId().equals(productId))
			.findFirst()
			.orElse(null);

		return product;
	}

	// 상품 수정
	@PutMapping("/{PRODUCT_ID}")
	public TProductDto updateProduct(@PathVariable("PRODUCT_ID") Long productId,
		@RequestBody TProductUpdateReq tProductUpdateReq) {

		// 람다식으로 해당 상품아이디 매핑
		TProductDto updatedProduct = productList.stream()
			.filter(p -> p.getId().equals(productId))
			.findFirst()
			.orElse(null);

		// 상품 수정할 정보
		updatedProduct.setName(tProductUpdateReq.getName());
		updatedProduct.setPrice(tProductUpdateReq.getPrice());
		updatedProduct.setMemo(tProductUpdateReq.getMemo());
		updatedProduct.setUpdatedDateTime("2024-01-01 11:11:11"); // 이건 DB에서 알아서 할듯

		return updatedProduct;
	}

	// 상품 삭제
	@DeleteMapping("/{PRODUCT_ID}")
	public TProductDto deleteProduct(@PathVariable("PRODUCT_ID") Long productId) {

		// 람다식으로 해당 상품아이디 매핑
		TProductDto product = productList.stream()
			.filter(p -> p.getId().equals(productId))
			.findFirst()
			.orElse(null);

		// 실제 삭제 대신 상태 업데이트
		product.setStatus("DELETED");
		product.setDeletedDate("2024-01-01"); // 이건 DB에서 알아서 할듯

		return product;
	}

}