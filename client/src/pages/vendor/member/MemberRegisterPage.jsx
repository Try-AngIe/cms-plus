import NextButton from '@/components/common/StatusNextButton';
import PreviousButton from '@/components/common/StatusPreButton';
import RegisterBasicInfo from '@/components/vendor/member/RegisterBasicInfo';
import RegisterBillingInfo from '@/components/vendor/member/RegisterBillingInfo';
import RegisterContractInfo from '@/components/vendor/member/RegisterContractInfo';
import RegisterPaymentInfo from '@/components/vendor/member/RegisterPaymentInfo';
import useStatusStepper from '@/hooks/useStatusStepper';
import { useStatusStore } from '@/stores/useStatusStore';
import { useNavigate } from 'react-router-dom';

const MemberRegisterPage = () => {
  const start = 0;
  const end = 4;
  const status = useStatusStore(state => state.status);
  const { handleClickPrevious, handleClickNext } = useStatusStepper('memberRegister', start, end);
  const navigate = useNavigate();

  const componentMap = {
    0: RegisterBasicInfo, // 기본정보
    1: RegisterContractInfo, // 계약정보
    2: RegisterPaymentInfo, // 결제정보
    3: RegisterBillingInfo, // 청구정보
  };

  const Content = componentMap[status] || (() => 'error');

  return (
    <div className='w- flex h-full w-full flex-col'>
      <div className='sub-dashboard relative mb-5 h-48 w-full'>
        progressivee
        <img
          src='/src/assets/close.svg'
          alt='back'
          className='absolute right-6 top-6 cursor-pointer'
          onClick={() => navigate(-1)}
        />
      </div>
      <div className='primary-dashboard relative h-full w-full'>
        information
        <Content />
        <div className='absolute bottom-0 left-0 flex h-24 w-full justify-between p-6 font-bold'>
          <PreviousButton
            onClick={handleClickPrevious}
            status={status}
            type={'memberRegister'}
            start={start}
            end={end}
          />
          <NextButton onClick={handleClickNext} status={status} type={'memberRegister'} end={end} />
        </div>
      </div>
    </div>
  );
};

export default MemberRegisterPage;
