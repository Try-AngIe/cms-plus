import InputWeb from '@/components/common/inputs/InputWeb';
import { formatPhone } from '@/utils/format/formatPhone';
import move from '@/assets/move.svg';
import { useNavigate } from 'react-router-dom';

const BillingDetailMember = ({ memberData }) => {
  const navigate = useNavigate();
  return (
    <>
      <div className='flex items-center'>
        <p className='text-text_black text-xl font-800'>회원정보</p>
        <button>
          <img
            src={move}
            alt='move'
            className='h-[24px] ml-1 cursor-pointer'
            onClick={() => navigate(`/vendor/members/detail/${memberData.id}`)}
          />
        </button>
      </div>
      <div className='grid grid-cols-4 gap-6 my-8'>
        <InputWeb
          id='memberId'
          label='회원번호'
          value={String(memberData.id).padStart(8, '0')}
          type='text'
          disabled={true}
        />
        <InputWeb
          id='memberName'
          label='회원명'
          value={memberData.name}
          type='text'
          disabled={true}
        />
        <InputWeb
          id='memberPhone'
          label='휴대전화'
          value={formatPhone(memberData.phone)}
          type='text'
          disabled={true}
        />
      </div>
    </>
  );
};

export default BillingDetailMember;
