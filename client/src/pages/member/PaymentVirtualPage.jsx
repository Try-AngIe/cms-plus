import CheckVirtual from '@/components/member/virtualAccount/CheckVirtual';
import Loading from '@/components/common/Loading';
import Success from '@/components/common/Success';

import NextButton from '@/components/common/StatusNextButton';
import PreviousButton from '@/components/common/StatusPreButton';

import { useStatusStore } from '@/stores/useStatusStore';
import useStatusStepper from '@/hooks/useStatusStepper';

const PaymentVirtualPage = () => {
  const start = 0;
  const end = 4;
  const status = useStatusStore(state => state.status);
  const { handleClickPrevious, handleClickNext } = useStatusStepper('virtual', start, end);

  const componentMap = {
    2: CheckVirtual, // 가상계좌 정보
    3: () => <Loading content={'결제중...'} />, // 결제로딩
    4: Success, // 입금완료
  };

  const Content = componentMap[status] || (() => 'error');

  return (
    <>
      <Content />
      <div className='absolute bottom-0 left-0 flex h-24 w-full justify-between p-6 font-bold'>
        <PreviousButton onClick={handleClickPrevious} status={status} start={start} end={end} />
        <NextButton onClick={handleClickNext} type={'invoice'} status={status} end={end} />
      </div>
    </>
  );
};

export default PaymentVirtualPage;
