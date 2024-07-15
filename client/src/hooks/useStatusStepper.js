import { useStatusStore } from '@/stores/useStatusStore';
import { useNavigate } from 'react-router-dom';

const CASES = {
  PAYMENT: 'payment', // 납부자 청구서조회 페이지
  CARD: 'card', // 납부자 카드 결제 페이지
  ACCOUNT: 'account', // 납부자 계좌 결제 페이지
  VIRTUAL: 'virtual', // 납부자 가상계좌 페이지
  MEMBERREGISTER: 'memberRegister', // 회원 등록 페이지
};

const useStatusStepper = (type, start, end) => {
  const status = useStatusStore(state => state.status);
  const increment = useStatusStore(state => state.increment);
  const decrement = useStatusStore(state => state.decrement);
  const reset = useStatusStore(state => state.reset);
  const navigate = useNavigate();

  const handleClickPrevious = () => {
    if (status > start) {
      decrement();
    }

    // 가상계좌 2p -> 1p
    if (type === CASES.VIRTUAL && status === 2) {
      navigate(-1);
      return;
    }
    // 카드, 계좌 3p -> 2p, 2p -> 1p
    if ((type === CASES.CARD || type === CASES.ACCOUNT) && (status === 2 || status === 3)) {
      navigate(-1);
      return;
    }
  };

  const handleClickNext = () => {
    if (status < end) {
      increment();
    }
    if (status === end) {
      reset();
      // 삭제 예정
      if (type !== 'simpconsent') {
        navigate('/member/invoice/3');
      }
    }
    switch (type) {
      case CASES.PAYMENT:
        if (status === 1) navigate('/member/invoice/payment');
        break;
      case CASES.VIRTUAL:
        if (status === 1) navigate('/member/invoice/payment/virtual');
        break;
      case CASES.CARD:
        if (status === 2) navigate('/member/invoice/payment/card');
        break;
      case CASES.ACCOUNT:
        if (status === 2) navigate('/member/invoice/payment/account');
        break;
      case CASES.MEMBERREGISTER:
        if (status === 3) {
          reset();
          navigate('/vendor/members');
        }
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
