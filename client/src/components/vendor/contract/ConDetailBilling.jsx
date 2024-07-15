import { useNavigate } from 'react-router-dom';

const ConDetailBilling = () => {
  const navigate = useNavigate();

  // id값은 추후 변경
  const handleButtonClick = () => {
    navigate('/vendor/contracts/billings/update/1');
  };
  return (
    <div className='sub-dashboard h-640 w-full'>
      ConDetailBilling
      <button className='rounded-lg bg-mint p-3 font-bold text-white' onClick={handleButtonClick}>
        임시 청구수정
      </button>
    </div>
  );
};

export default ConDetailBilling;
