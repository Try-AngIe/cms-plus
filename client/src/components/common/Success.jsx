import successImage from '@/assets/success.svg';

const Success = () => {
  return (
    <div className='flex h-screen flex-col items-center justify-between bg-white p-3'>
      <div className='w-full text-left'>
        <h1 className='mb-4 text-2xl font-bold text-teal-400'>Hyosung CMS+</h1>
        <h3 className='mb-8 text-base font-semibold text-gray-700'>
          회원님의
          <br />
          자동결제 등록이 완료되었습니다!
        </h3>
      </div>

      <div className='flex flex-grow items-center justify-center'>
        <div className='relative mb-40 h-48 w-52'>
          <div className='absolute inset-0 rounded-full bg-gradient-to-br from-teal-400 to-blue-200 opacity-50 blur-2xl'></div>
          <img
            src={successImage}
            alt='Card'
            className='absolute inset-0 z-10 h-full w-full object-contain'
          />
        </div>
      </div>
    </div>
  );
};

export default Success;
