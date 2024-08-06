package kr.or.kosa.cmsplusmain.util;

import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBilling.*;
import static kr.or.kosa.cmsplusmain.domain.billing.entity.QBillingProduct.*;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContract.*;
import static kr.or.kosa.cmsplusmain.domain.contract.entity.QContractProduct.contractProduct;
import static kr.or.kosa.cmsplusmain.domain.member.entity.QMember.*;
import static kr.or.kosa.cmsplusmain.domain.payment.entity.QPayment.*;
import static org.springframework.util.StringUtils.*;

import java.time.LocalDate;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;

import kr.or.kosa.cmsplusmain.domain.base.dto.PageReq;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStatus;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;

public class QueryExpressions {

    /*********** 계약 조건 ************/
    public static BooleanExpression contractVendorIdEq(Long vendorId) {
        return (vendorId != null) ? contract.vendor.id.eq(vendorId) : null;
    }
    public static BooleanExpression contractIdEq(Long contractId) {
        return contractId != null ? contract.id.eq(contractId) : null;
    }
    public static BooleanExpression contractStatusEq(ContractStatus contractStatus) {
        return (contractStatus != null) ? contract.contractStatus.eq(contractStatus) : null;
    }
    public static BooleanExpression contractDayEq(Integer contractDay) {
        return (contractDay != null) ? contract.contractDay.eq(contractDay) : null;
    }
    public static BooleanExpression contractCountLoe(Integer contractCount) {
        return contractCount != null ? contract.countDistinct().intValue().loe(contractCount) : null;
    }
    public static BooleanExpression contractPriceLoe(Long contractPrice) {
        return contractPrice != null ? contractProduct.price.multiply(contractProduct.quantity).sum().loe(contractPrice) : null;
    }

    /*********** 결제 조건 ************/
    public static BooleanExpression paymentTypeEq(PaymentType paymentType) {
        return (paymentType != null) ? payment.paymentType.eq(paymentType) : null;
    }
    public static BooleanExpression paymentMethodEq(PaymentMethod paymentMethod) {
        return (paymentMethod != null) ? payment.paymentMethod.eq(paymentMethod) : null;
    }

    /*********** 청구 조건 ************/
    public static BooleanExpression billingStatusEq(BillingStatus billingStatus) {
        return (billingStatus != null) ? billing.billingStatus.eq(billingStatus) : null;
    }
    public static BooleanExpression billingDateEq(LocalDate billingDate) {
        return (billingDate != null) ? billing.billingDate.eq(billingDate) : null;
    }
    public static BooleanExpression billingPriceLoe(Long billingPrice) {
        return billingPrice != null ? billingProduct.price.multiply(billingProduct.quantity).sum().loe(billingPrice) : null;
    }

    /*********** 회원 조건 ************/
    public static BooleanExpression memberIdEq(Long memberId) {
        return (memberId != null) ? member.id.eq(memberId) : null;
    }
    public static BooleanExpression memberNameContains(String memberName) {
        return hasText(memberName) ? member.name.containsIgnoreCase(memberName) : null;
    }
    public static BooleanExpression memberPhoneContains(String memberPhone) {
        return hasText(memberPhone) ? member.phone.containsIgnoreCase(memberPhone) : null;
    }
    public static BooleanExpression memberEmailContains(String memberEmail) {
        return hasText(memberEmail) ? member.phone.containsIgnoreCase(memberEmail) : null;
    }
    public static BooleanExpression memberEnrollDateEq(LocalDate memberEnrollDate) {
        return (memberEnrollDate != null) ? member.enrollDate.eq(memberEnrollDate) : null;
    }
    public static BooleanExpression memberContractPriceLoe(Long contractPrice) {
        return (contractPrice != null) ? member.contractPrice.loe(contractPrice) : null;
    }
    public static BooleanExpression memberContractCountLoe(Integer contractCount) {
        return (contractCount != null) ? member.contractCount.loe(contractCount) : null;
    }
}