import { useNavigate } from 'react-router-dom';

const MemberInfoUpdatePage = () => {
  const navigate = useNavigate();
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
        MemberInfoUpdatePage
        <div className='absolute bottom-0 left-0 flex h-24 w-full justify-between p-6 font-bold'>
          Button
        </div>
      </div>
    </div>
  );
};

export default MemberInfoUpdatePage;
