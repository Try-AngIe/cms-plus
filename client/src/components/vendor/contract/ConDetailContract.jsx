import { useNavigate } from 'react-router-dom';

const ConDetailContract = () => {
  const navigate = useNavigate();

  // id값은 추후 변경
  const handleButtonClick = () => {
    navigate('/vendor/contracts/Product/update/1');
  };
  return (
    <div className='sub-dashboard mb-5 mr-5 h-640 w-full'>
      ConDetailContract
      <button className='rounded-lg bg-mint p-3 font-bold text-white' onClick={handleButtonClick}>
        임시 상품수정
      </button>
    </div>
  );
};

export default ConDetailContract;
