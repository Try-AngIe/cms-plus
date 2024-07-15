import CardInfo from '@/components/member/card/CardInfo';
import ChooseCard from '@/components/member/card/ChooseCard';
import Loading from '@/components/common/Loading';
import Success from '@/components/common/Success';

import NextButton from '@/components/common/StatusNextButton';
import PreviousButton from '@/components/common/StatusPreButton';

import useStatusStepper from '@/hooks/useStatusStepper';
import { useStatusStore } from '@/stores/useStatusStore';

const PaymentCardPage = () => {
  const start = 0;
  const end = 6;
  const status = useStatusStore(state => state.status);
  const { handleClickPrevious, handleClickNext } = useStatusStepper('card', start, end);

  const componentMap = {
    3: ChooseCard, //카드사선택
    4: CardInfo, // 카드정보 입력
    5: () => <Loading content={'결제중...'} />, // 결제로딩
    6: Success, // 입금완료
  };

  const Content = componentMap[status] || (() => 'error');
  return (
    <>
      <Content />
      <div className='absolute bottom-0 left-0 flex h-24 w-full justify-between p-6 font-bold'>
        <PreviousButton onClick={handleClickPrevious} status={status} start={start} end={end} />
        <NextButton onClick={handleClickNext} type={'card'} status={status} end={end} />
      </div>
    </>
  );
};

export default PaymentCardPage;
