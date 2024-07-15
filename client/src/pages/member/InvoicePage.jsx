import Main from '@/components/member/invoice/Main';
import CheckInvoice from '@/components/member/invoice/CheckInvoice';

import NextButton from '@/components/common/StatusNextButton';
import PreviousButton from '@/components/common/StatusPreButton';

import useStatusStepper from '@/hooks/useStatusStepper';
import { useStatusStore } from '@/stores/useStatusStore';
import { useParams } from 'react-router-dom';
import { useState } from 'react';

const InvoicePage = () => {
  const start = 0;
  const end = 2;
  const { invoiceId } = useParams();
  const status = useStatusStore(state => state.status);
  const [paymentType, setPaymentType] = useState('payment');
  console.log('paymentType', paymentType);
  const { handleClickPrevious, handleClickNext } = useStatusStepper(paymentType, start, end);

  const componentMap = {
    0: Main, // 시작화면
    1: CheckInvoice, // 청구서 확인
  };

  const Content = componentMap[status] || (() => 'error');

  return (
    <>
      {/* 실제 로직에서는 버튼이 아닌 실제로 받아온 데이터를 통해서 결제방식을 확인 
          청구서 결제방식이 판별해서 paymenttype useState에 할당하자*/}
      {status === 1 ? (
        <div className='flex justify-around'>
          <div
            className='border border-black hover:bg-mint'
            onClick={() => setPaymentType('payment')}>
            임의 납부자결제
          </div>

          <div
            className='border border-black hover:bg-mint'
            onClick={() => setPaymentType('virtual')}>
            임의 가상계좌
          </div>
        </div>
      ) : (
        ''
      )}
      <Content />
      <div className='absolute bottom-0 left-0 flex h-24 w-full justify-between p-6 font-bold'>
        <PreviousButton onClick={handleClickPrevious} status={status} start={start} end={end} />
        <NextButton onClick={handleClickNext} type={'invoice'} status={status} end={end} />
      </div>
    </>
  );
};

export default InvoicePage;
