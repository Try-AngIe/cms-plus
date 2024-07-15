import React from 'react';
import RadioGroup from '@/components/common/RadioGroup';
import PaymentCard from '@/components/member/simpConsent/PaymentCard';
import PaymentCMS from '@/components/member/simpConsent/PaymentCMS';
import { useUserDataStore } from '@/stores/useUserDataStore';

const PaymentInfo = () => {
  const { userData, setUserData, clearPaymentInfo } = useUserDataStore();

  const paymentOptions = [
    { label: '카드', value: 'card' },
    { label: '실시간 CMS', value: 'cms' },
  ];

  const handlePaymentMethodChange = value => {
    clearPaymentInfo(); // 결제 방법이 변경될 때 기존 결제 정보 초기화
    setUserData({ paymentMethod: value });
  };

  return (
    <>
      <div className='w-full text-left'>
        <h3 className='mb-8 text-base font-semibold text-gray-700'>
          회원님의
          <br />
          결제정보를 확인해주세요.
        </h3>
      </div>
      <RadioGroup
        label='결제수단'
        name='paymentMethod'
        options={paymentOptions}
        selectedOption={userData.paymentMethod}
        onChange={handlePaymentMethodChange}
        required={true}
      />
      {userData.paymentMethod === 'card' ? <PaymentCard /> : <PaymentCMS />}
    </>
  );
};

export default PaymentInfo;
