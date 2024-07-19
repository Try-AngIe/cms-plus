import NextButton from '@/components/common/buttons/StatusNextButton';
import PreviousButton from '@/components/common/buttons/StatusPreButton';
import RegisterBasicInfo from '@/components/vendor/member/registers/RegisterBasicInfo';
import RegisterBillingInfo from '@/components/vendor/member/registers/RegisterBillingInfo';
import RegisterContractInfo from '@/components/vendor/member/registers/RegisterContractInfo';
import RegisterPaymentInfo from '@/components/vendor/member/registers/RegisterPaymentInfo';
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
    0: { title: '기본정보', component: RegisterBasicInfo }, // 기본정보
    1: { title: '계약정보', component: RegisterContractInfo }, // 계약정보
    2: { title: '결제정보', component: RegisterPaymentInfo }, // 결제정보
    3: { title: '청구정보', component: RegisterBillingInfo }, // 청구정보
  };

  const { title, component: Content } = componentMap[status] || {
    title: 'error',
    component: () => 'error',
  };

  return (
    <div className='flex h-full w-full flex-col '>
      <div className='up-dashboard relative mb-5 h-1/5 w-full'>
        progressivee
        <img
          src='/src/assets/close.svg'
          alt='back'
          className='absolute right-6 top-6 cursor-pointer'
          onClick={() => navigate(-1)}
        />
      </div>
      <div className='primary-dashboard flex flex-col pb-9 relative h-4/5 '>
        <div className='border-b border-ipt_border px-2 pt-1 pb-3'>
          <p className='text-text_black text-xl font-800'>{title}</p>
        </div>
        <Content />
        <div className='absolute bottom-0 left-0 flex h-24 w-full justify-between px-10 py-7 font-bold '>
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
