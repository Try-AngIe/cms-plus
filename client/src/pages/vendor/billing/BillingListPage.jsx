import BillingRegisterModal from '@/components/vendor/modal/BillingRegisterModal';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const BillingListPage = () => {
  const [isShowModal, setIsShowModal] = useState(false);
  const navigate = useNavigate();

  // id값은 추후 변경
  const handleButtonClick = () => {
    navigate('detail/1');
  };

  return (
    <div className='primary-dashboard h-full w-full'>
      BillingListPage{' '}
      <button
        className='mr-4 rounded-lg bg-mint p-3 font-bold text-white'
        onClick={() => setIsShowModal(true)}>
        임시 청구 생성
      </button>
      <button className='rounded-lg bg-mint p-3 font-bold text-white' onClick={handleButtonClick}>
        임시 청구 상세 이동
      </button>
      <BillingRegisterModal
        isShowModal={isShowModal}
        setIsShowModal={setIsShowModal}
        modalTitle={'청구생성'}
      />
    </div>
  );
};

export default BillingListPage;
