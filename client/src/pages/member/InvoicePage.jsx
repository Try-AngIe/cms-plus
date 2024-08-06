import Main from '@/components/member/invoice/Main';
import CheckInvoice from '@/components/member/invoice/CheckInvoice';

import NextButton from '@/components/common/buttons/StatusNextButton';
import PreviousButton from '@/components/common/buttons/StatusPreButton';

import useStatusStepper from '@/hooks/useStatusStepper';
import { useStatusStore } from '@/stores/useStatusStore';
import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { useInvoiceStore } from '@/stores/useInvoiceStore';
import { getBillingInfo } from '@/apis/billing';

const InvoicePage = () => {
  const start = 0;
  const end = 2;
  const { invoiceId } = useParams();
  const { setInvoiceInfo, invoiceInfo } = useInvoiceStore();
  const { status, reset } = useStatusStore();
  const [paymentType, setPaymentType] = useState('BUYER');
  const { handleClickPrevious, handleClickNext } = useStatusStepper(paymentType, start, end);

  // <----- incvoiceInfo 정보 가져오기 ----->
  const axiosInvoiceInfo = async billingId => {
    try {
      const res = await getBillingInfo(billingId);
      setInvoiceInfo(res.data);
      setPaymentType(res.data.paymentType.paymentType.code);
    } catch (err) {
      console.error('청구서 정보 조회 실패:', err);
      alert(err.response.data.message);
    }
  };

  const componentMap = {
    0: Main, // 시작화면
    1: CheckInvoice, // 청구서 확인
  };

  const Content = componentMap[status] || (() => '');

  // <----- 페이지 렌더링 시 초기화 ----->
  useEffect(() => {
    axiosInvoiceInfo(invoiceId);
    if (status >= 2) {
      reset();
    }
  }, []);

  return (
    <>
      {/* 청구서 결제방식 판별해서 paymenttype useState에 할당*/}
      {invoiceInfo ? <Content /> : <></>}
      <div className='fixed bottom-0 left-0 w-full'>
        <div className='absolute inset-0 bg-white opacity-100 blur' />
        <div className='relative flex h-24 w-full justify-between p-6 font-bold z-50'>
          <PreviousButton onClick={handleClickPrevious} status={status} start={start} end={end} />
          <NextButton onClick={handleClickNext} type={'invoice'} status={status} end={end} />
        </div>
      </div>
    </>
  );
};

export default InvoicePage;
