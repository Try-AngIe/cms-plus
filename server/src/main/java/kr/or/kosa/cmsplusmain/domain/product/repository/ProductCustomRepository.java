package kr.or.kosa.cmsplusmain.domain.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.or.kosa.cmsplusmain.domain.base.repository.BaseCustomRepository;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import org.springframework.stereotype.Repository;

import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.contract;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContractProduct.contractProduct;

@Repository
public class ProductCustomRepository extends BaseCustomRepository<Product> {

    public ProductCustomRepository(EntityManager em, JPAQueryFactory jpaQueryFactory) {
        super(em, jpaQueryFactory);
    }

    public int getContractNumber(Long productId) {

//        상품이 포함된 계약의 개수를 구하는 sql
//        select count(distinct pc.contract_id)
//        from product_contract pc
//        join contract c
//        where pc.product_id = #{productId} and c.deleted = 0
        return jpaQueryFactory
                .select(contract.id.countDistinct())
                .from(contractProduct)
                .join(contractProduct.contract, contract) // 계약-상품 테이블의 외래키인 계약ID를 타고 들어감
                .where(contractProduct.product.id.eq(productId), contract.deleted.eq(false))
                .fetchOne().intValue();

    }

}
