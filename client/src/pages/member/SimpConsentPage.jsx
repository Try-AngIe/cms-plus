import React from 'react';
import Main from '@/components/member/simpConsent/Main';
import BasicInfo from '@/components/member/simpConsent/BasicInfo';
import ContractInfo from '@/components/member/simpConsent/ContractInfo';
import PaymentInfo from '@/components/member/simpConsent/PaymentInfo';
import Signature from '@/components/member/simpConsent/Signature';
import Loading from '@/components/common/Loading';
import Success from '@/components/common/Success';

import NextButton from '@/components/common/StatusNextButton';
import PreviousButton from '@/components/common/StatusPreButton';

import { useStatusStore } from '@/stores/useStatusStore';
import { useUserDataStore } from '@/stores/useUserDataStore';
import useStatusStepper from '@/hooks/useStatusStepper';

const SimpConsentPage = () => {
  const start = 0;
  const end = 6;
  const status = useStatusStore(state => state.status);
  const { handleClickPrevious, handleClickNext } = useStatusStepper('simpconsent', start, end);

  const userData = useUserDataStore(state => state.userData);
  const setUserData = useUserDataStore(state => state.setUserData);

  const componentMap = {
    0: Main,
    1: () => <BasicInfo userData={userData} setUserData={setUserData} />,
    2: () => <ContractInfo userData={userData} setUserData={setUserData} />,
    3: () => <PaymentInfo userData={userData} setUserData={setUserData} />,
    4: () => <Signature userData={userData} setUserData={setUserData} />,
    5: () => <Loading content={'등록중...'} />,
    6: () => <Success userData={userData} />,
  };

  const Content = componentMap[status] || (() => 'error');

  return (
    <>
      <Content />
      <div className='absolute bottom-0 left-0 flex h-24 w-full justify-between p-6 font-bold'>
        <PreviousButton onClick={handleClickPrevious} status={status} start={start} end={end} />
        <NextButton onClick={handleClickNext} status={status} type={'simpconsent'} end={end} />
      </div>
    </>
  );
};

export default SimpConsentPage;
