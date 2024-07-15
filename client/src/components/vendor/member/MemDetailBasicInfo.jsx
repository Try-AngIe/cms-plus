import { useNavigate } from 'react-router-dom';

const MemDetailBasicInfo = () => {
  const navigate = useNavigate();

  // id값은 추후 변경
  const handleGoDetail = () => {
    navigate('/vendor/members/update/1');
  };
  return (
    <div className='sub-dashboard mb-5 h-640 w-full'>
      DetailBasicInfo
      <button className='rounded-lg bg-mint p-3 font-bold text-white' onClick={handleGoDetail}>
        임시 회원 상세 이동
      </button>
    </div>
  );
};

export default MemDetailBasicInfo;
