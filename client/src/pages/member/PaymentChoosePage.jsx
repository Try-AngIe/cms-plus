import NextButton from '@/components/common/StatusNextButton';
import PreviousButton from '@/components/common/StatusPreButton';
import { useStatusStore } from '@/stores/useStatusStore';
import useStatusStepper from '@/hooks/useStatusStepper';
import { useState } from 'react';

const PaymentChoosePage = () => {
  const start = 0;
  const end = 3;
  const status = useStatusStore(state => state.status);
  const [paymentType, setPaymentType] = useState('card');
  const { handleClickPrevious, handleClickNext } = useStatusStepper(paymentType, start, end);

  return (
    <div className='flex flex-col h-full'>
      <div className='flex-grow'>
        <h3 className='mb-8 text-base font-semibold text-gray-700'>
          결제수단을
          <br />
          선택해주세요.
        </h3>
        <div className='flex gap-4 mt-4'>
          <label className='flex-1'>
            <input
              type='radio'
              name='paymentType'
              value='card'
              className='sr-only'
              checked={paymentType === 'card'}
              onChange={() => setPaymentType('card')}
            />
            <div
              className={`border rounded-lg p-4 h-24 flex items-center justify-center cursor-pointer ${paymentType === 'card' ? 'border-cyan-500 bg-cyan-50' : 'hover:border-gray-300'}`}>
              <p className={paymentType === 'card' ? 'text-cyan-500' : 'text-gray-500'}>카드</p>
            </div>
          </label>
          <label className='flex-1'>
            <input
              type='radio'
              name='paymentType'
              value='account'
              className='sr-only'
              checked={paymentType === 'account'}
              onChange={() => setPaymentType('account')}
            />
            <div
              className={`border rounded-lg p-4 h-24 flex items-center justify-center cursor-pointer ${paymentType === 'account' ? 'border-cyan-500 bg-cyan-50' : 'hover:border-gray-300'}`}>
              <p className={paymentType === 'account' ? 'text-cyan-500' : 'text-gray-500'}>계좌</p>
            </div>
          </label>
        </div>
      </div>

      <div className='absolute bottom-0 left-0 flex h-24 w-full justify-between p-6 font-bold'>
        <PreviousButton onClick={handleClickPrevious} status={status} start={start} end={end} />
        <NextButton onClick={handleClickNext} type={'invoice'} status={status} end={end} />
      </div>
    </div>
  );
};

export default PaymentChoosePage;
