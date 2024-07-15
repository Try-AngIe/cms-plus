package kr.or.kosa.cmsplusmain.domain.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.or.kosa.cmsplusmain.domain.base.dto.SortPageDto;
import kr.or.kosa.cmsplusmain.domain.base.repository.BaseCustomRepository;
import kr.or.kosa.cmsplusmain.domain.product.dto.ProductQRes;
import kr.or.kosa.cmsplusmain.domain.product.dto.ProductSearch;
import kr.or.kosa.cmsplusmain.domain.product.dto.QProductQRes;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.product.entity.ProductStatus;
import org.springframework.stereotype.Repository;
import java.util.List;

import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.contract;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContractProduct.contractProduct;
import static kr.or.kosa.cmsplusmain.domain.product.entity.QProduct.product;
import static kr.or.kosa.cmsplusmain.domain.vendor.entity.QVendor.vendor;


@Repository
public class ProductCustomRepository extends BaseCustomRepository<Product> {

    public ProductCustomRepository(EntityManager em, JPAQueryFactory jpaQueryFactory) {
        super(em, jpaQueryFactory);
    }

    // 상품이 매핑되어있는 계약수
    public int getContractNumber(Long productId) {
        /*
         * 상품이 포함된 계약의 개수를 구함
         * select count(distinct cp.contract_id)
         * from contract_product cp
         * join contract c
         * where cp.product_id = #{productId} and c.deleted = 0
         * */
        Long res = jpaQueryFactory
                .select(contract.id.countDistinct())
                .from(contractProduct)
                .join(contractProduct.contract, contract) // 계약-상품 테이블의 외래키인 계약ID를 타고 들어감
                .where(contractProduct.product.id.eq(productId),
                        contractNotDel())
                .fetchOne();

        return (res == null) ? 0 : res.intValue();
    }

    // 고객의 상품 목록
    // 프로젝션 대상이 2개이상이기 때문에 QueryProjection을 이용
    public List<ProductQRes> findProductListWithCondition(String vendorUserName, ProductSearch search, SortPageDto.Req pageable) {
        /*
         * 해당 고객의 모든 상품(삭제되지않은)을 가져오는데, 상품의 정보와 상품이 포함된 계약 갯수를 가져와야함
         *
         * 해당 고객의 모든 상품(삭제되지않은)
         * select *
         * from product p
         * join vendor v
         * where v.vendor_userName = #{userName} and p.deleted = 0
         *
         * 상품의 정보와 상품이 포함된 계약 갯수
         * select count(distinct pc.contract_id)
         * from product_contract pc
         * join contract c
         * where pc.product_id = #{productId} and c.deleted = 0
         *
         * 1. 해당 고객의 모든 상품(삭제되지않은)을 가져오는데, 상품의 정보와 상품이 포함된 계약 갯수를 가져와야함
         * select vendor_product.* count(distinct cp.contract_id)
         * from (select *
         *         from product p
         *         join vendor v
         *         where v.vendor_userName = #{userName} and p.deleted = 0) vp , contract_product cp
         * join contract c
         * where cp.product_id = vp.product_id and c.deleted = 0
         *
         * 2. (최종)
         * select p.*, count(distinct cp.contract_id) as contract_count
         * from product p
         * join vendor v on v.vendor_id = p.vendor_id
         * left join contract_product cp on cp.product_id = p.product_id and cp.deleted = 0
         * left join contract c on c.contract_id = cp.contract_id and c.deleted = 0
         * where v.vendor_username = 'vendor1' and p.deleted = 0
         * group by p.product_id;
         * */
        List<ProductQRes> res = jpaQueryFactory
                .select(new QProductQRes(product, contractProduct.contract.countDistinct().intValue())) //ProductQRes의 QueryDSL DTO 생성
                .from(product)
                .join(product.vendor, vendor)
                .leftJoin(contractProduct)
                        .on(contractProduct.product.eq(product), (contractProductNotDel()))
                .leftJoin(contract)
                        .on(contractProduct.contract.eq(contract), (contractNotDel()))
                .where(
                        productNotDel(),                                        // 상품 소프트 삭제
                        vendorUsernameEq(vendorUserName),                       // 고객 아이디 일치
                        productNameContains(search.getProductName()),           // 상품 이름 포함
                        productPriceLoe(search.getProductPrice()),              // 상품 가격 이하
                        productMemoContains(search.getProductMemo()),           // 상품 비고 포함
                        productCreatedDateEq(search.getProductCreatedDate())    // 상품 생성시기 일치
                )
                .groupBy(product.id)
                .having( // 집계함수(ex. count)에 대한 조건문은 where절이 아닌 having절에서 사용함
                        contractNumberLoe(search.getContractNumber())           // 상품들의 각 해당하는 계약수 이하
                )
                .offset(pageable.getPage())
                .limit(pageable.getSize())
                .fetch();

        return res;
    }

    // 고객의 상품 총 갯수
    public int countAllProducts(String vendorUsername) {
        Long res = jpaQueryFactory
                .select(product.id.count())
                .from(product)
                .join(product.vendor, vendor)
                .where(
                        vendorUsernameEq(vendorUsername),
                        productNotDel()
                )
                .fetchOne();

        return (res == null) ? 0 : res.intValue();
    }

    // 특정 고객이 본인의 상품이 아닌거 조회하는거 처리
    public boolean isExistProductByUsername(Long productId, String vendorUsername) {
        /*
         * 고객테이블에 있는 고객이름이 해당 상품의 고객ID의 이름인지 확인
         * select 1
         * from product p
         * join vendor v on p.vendor_id = v.vendor_id
         * where p.product_id = #{productId} and v.vendor_username = #{vendorUsername}
         * */
        Integer res = jpaQueryFactory
                .selectOne()
                .from(product)
                .join(product.vendor, vendor)
                .where(product.id.eq(productId),
                        vendor.username.eq(vendorUsername))
                .fetchFirst();

        return res != null;
    }


    /* 고객의 상품 목록 조회 */
    public List<Product> findAvailableProductsByVendorUsername(String username) {
        return jpaQueryFactory
                .selectFrom(product)
                .join(product.vendor, vendor)
                .where(
                        vendorUsernameEq(username),
                        productNotDel()
                )
                .fetch();
    }

    /* 전체 상품 목록 조회 */
    public List<Product> findProducts() {
        return jpaQueryFactory
                .selectFrom(product)
                .where(productNotDel())
                .fetch();
    }


}

