import { useStatusStore } from '@/stores/useStatusStore';
import { useNavigate } from 'react-router-dom';
import { useInvoiceStore } from '@/stores/useInvoiceStore';

const CASES = {
  BUYER: 'BUYER', // 모바일 청구서조회
  CARD: 'CARD', // 모바일 카드 결제
  ACCOUNT: 'ACCOUNT', // 모바일 계좌 결제
  VIRTUAL: 'VIRTUAL', // 모바일 가상계좌
  AUTO: 'AUTO', //모바일 자동결제
  MEMBERREGISTER: 'MEMBERREGISTER', // 회원 등록
};

const useStatusStepper = (type, start, end) => {
  const invoiceInfo = useInvoiceStore(state => state.invoiceInfo);
  const { status, increment, decrement, reset } = useStatusStore();

  const navigate = useNavigate();
  type = type.toUpperCase();

  const handleClickPrevious = () => {
    if (status > start) {
      decrement();
    }

    // 가상계좌 2p -> 1p
    if (type === CASES.VIRTUAL && status === 2) {
      navigate(-1);
      return;
    }
    // 카드, 계좌 2p -> 1p
    if ((type === CASES.CARD || type === CASES.ACCOUNT) && status === 2) {
      navigate(`/member/invoice/${invoiceInfo.billingId}`);
      return;
    }

    // 카드, 계좌 3p -> 2p
    if ((type === CASES.CARD || type === CASES.ACCOUNT) && status === 3) {
      navigate(`/member/invoice/${invoiceInfo.billingId}/payment`);
      return;
    }
  };

  const handleClickNext = () => {
    if (status === 1 && invoiceInfo) {
      if (invoiceInfo.billingStatus === '완납') {
        //billingStatus가 '완납'이고 자동결제일 경우
        if (type === CASES.AUTO) {
          navigate(`/member/invoice/${invoiceInfo.billingId}/payment/auto`);
        } else {
          //billingStatus가 '완납'이고 자동결제가 아니면 complete페이지로 이동
          navigate(`/member/invoice/${invoiceInfo.billingId}/payment/complete`);
        }
        reset();
        return;
      }
      // '완납'이 아닐 경우, status 1에 대한 기존 로직 계속 진행
      if (type === CASES.BUYER) {
        navigate(`/member/invoice/${invoiceInfo.billingId}/payment`);
      }
      //'완납'이 아니면서, 자동결제일 경우
      if (type === CASES.AUTO) {
        navigate(`/member/invoice/${invoiceInfo.billingId}/payment/auto`);
        reset();
        return;
      }
    }
    if (status < end) {
      increment();
    }
    if (status === end && !type === CASES.MEMBERREGISTER) {
      reset();
    }

    switch (type) {
      case CASES.BUYER:
        if (status === 1) navigate(`/member/invoice/${invoiceInfo.billingId}/payment`);
        break;
      case CASES.VIRTUAL:
        if (status === 1) navigate(`/member/invoice/${invoiceInfo.billingId}/payment/virtual`);
        break;
      case CASES.CARD:
        if (status === 2) navigate(`/member/invoice/${invoiceInfo.billingId}/payment/card`);
        break;
      case CASES.ACCOUNT:
        if (status === 2) navigate(`/member/invoice/${invoiceInfo.billingId}/payment/account`);
        break;
      default:
        break;
    }
  };

  return {
    handleClickPrevious,
    handleClickNext,
  };
};

export default useStatusStepper;
