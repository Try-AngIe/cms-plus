import MemberChooseModal from '@/components/vendor/modal/MemberChooseModal';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const ContractListPage = () => {
  const [isShowModal, setIsShowModal] = useState(false);

  const navigate = useNavigate();

  // id값은 추후 변경
  const handleButtonClick = () => {
    navigate('detail/1');
  };
  return (
    <div className='primary-dashboard h-full w-full'>
      <button
        className='rounded-lg bg-mint p-3 font-bold text-white'
        onClick={() => setIsShowModal(true)}>
        임시 기존 회원 계약 등록
      </button>

      <button className='rounded-lg bg-mint p-3 font-bold text-white' onClick={handleButtonClick}>
        임시 계약 상세 정보
      </button>
      <MemberChooseModal
        isShowModal={isShowModal}
        setIsShowModal={setIsShowModal}
        modalTitle={'회원선택'}
      />
    </div>
  );
};

export default ContractListPage;
