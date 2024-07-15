import { useNavigate } from 'react-router-dom';

const ConDetailPayment = () => {
  const navigate = useNavigate();

  // id값은 추후 변경
  const handleButtonClick = () => {
    navigate('/vendor/contracts/payment/update/1');
  };
  return (
    <div className='sub-dashboard mb-5 h-640 w-full'>
      ConDetailPayment
      <button className='rounded-lg bg-mint p-3 font-bold text-white' onClick={handleButtonClick}>
        임시 결제수정
      </button>
    </div>
  );
};

export default ConDetailPayment;
