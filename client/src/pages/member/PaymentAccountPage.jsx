import ChooseBank from '@/components/member/account/ChooseBank';
import AccountInfo from '@/components/member/account/AccountInfo';
import Loading from '@/components/common/member/Loading';
import Success from '@/components/common/member/Success';

import NextButton from '@/components/common/buttons/StatusNextButton';
import PreviousButton from '@/components/common/buttons/StatusPreButton';

import { useStatusStore } from '@/stores/useStatusStore';
import useStatusStepper from '@/hooks/useStatusStepper';
import { useInvoiceStore } from '@/stores/useInvoiceStore';

const PaymentAccountPage = () => {
  const start = 0;
  const end = 6;
  const status = useStatusStore(state => state.status);
  const { handleClickPrevious, handleClickNext } = useStatusStepper('account', start, end);
  const invoiceInfo = useInvoiceStore((state) => state.invoiceInfo);


  const componentMap = {
    3: () => <ChooseBank billingInfo={invoiceInfo}/>, //은행선택
    4: AccountInfo, // 계좌정보 입력
    5: () => <Loading content={'결제중...'} />, // 결제로딩
    6: Success, // 입금완료
  };

  const Content = componentMap[status] || (() => 'error');

  return (
    <>
      <Content />
      <div className='absolute bottom-0 left-0 flex h-24 w-full justify-between p-6 font-bold'>
        <PreviousButton onClick={handleClickPrevious} status={status} start={start} end={end} />
        <NextButton onClick={handleClickNext} type={'account'} status={status} end={end} />
      </div>
    </>
  );
};

export default PaymentAccountPage;
