import billImage from '@/assets/billlogo.svg';

const Main = () => {
  return (
    <div className='flex flex-col items-center justify-between h-screen bg-white p-3'>
      <div className='text-left w-full'>
        <h1 className='text-2xl font-bold text-teal-400 mb-4'>Hyosung CMS+</h1>
        <h3 className='font-semibold text-gray-700 text-base mb-8'>
          아쿠르트에서 회원님께
          <br />
          청구서를 보냈습니다.
        </h3>
      </div>

      <div className='flex-grow flex items-center justify-center'>
        <div className='relative w-52 h-48 mb-40'>
          <div className='absolute inset-0 bg-gradient-to-br from-teal-400 to-blue-200 rounded-full opacity-50 blur-2xl'></div>
          <img
            src={billImage}
            alt='Card'
            className='absolute inset-0 w-full h-full object-contain z-10'
          />
        </div>
      </div>
    </div>
  );
};

export default Main;
