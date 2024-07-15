import { Navigate } from 'react-router-dom';
import { lazy } from 'react';
import LazyComponentWrapper from '@/pages/utils/LazyComponentWrapper';

const SimpConsent = lazy(() => import('@/pages/member/SimpConsentPage')); // 간편동의 등록
const Invoice = lazy(() => import('@/pages/member/InvoicePage')); // 청구서 상세조회
const PaymentChoose = lazy(() => import('@/pages/member/PaymentChoosePage')); // 청구서 결제수단 선택
const PaymentCard = lazy(() => import('@/pages/member/PaymentCardPage')); // 납부자결제 (카드)
const PaymentAccount = lazy(() => import('@/pages/member/PaymentAccountPage')); // 납부자결제 (계좌)
const PaymentVirtual = lazy(() => import('@/pages/member/PaymentVirtualPage')); // 가상계좌결제

const memberRoute = () => {
  return [
    {
      path: '',
      element: <Navigate replace to='simpconsent' />,
    },
    {
      path: 'simpconsent/:memberid?',
      element: <LazyComponentWrapper component={SimpConsent} />,
    },
    {
      path: 'invoice/:invoiceId',
      element: <LazyComponentWrapper component={Invoice} />,
    },
    {
      path: 'invoice/payment',
      element: <LazyComponentWrapper component={PaymentChoose} />,
    },
    {
      path: 'invoice/payment/card',
      element: <LazyComponentWrapper component={PaymentCard} />,
    },
    {
      path: 'invoice/payment/account',
      element: <LazyComponentWrapper component={PaymentAccount} />,
    },
    {
      path: 'invoice/payment/virtual',
      element: <LazyComponentWrapper component={PaymentVirtual} />,
    },
  ];
};

export default memberRoute;
