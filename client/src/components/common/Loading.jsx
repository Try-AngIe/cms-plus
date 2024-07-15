const Loading = ({ content }) => {
  return (
    <div className='flex h-screen flex-col items-center justify-between bg-white p-3'>
      <div className='w-full text-left'>
        <h1 className='mb-4 text-2xl font-bold text-teal-400'>Hyosung CMS+</h1>
        <h3 className='mb-8 text-base font-semibold text-gray-700'>결제 로딩중</h3>
      </div>

      <div className='flex flex-grow items-center justify-center'>
        <div className='relative mb-40 h-48 w-52'>
          <div className='absolute inset-0 rounded-full bg-gradient-to-br from-teal-400 to-blue-200 opacity-50 blur-2xl'></div>
        </div>
      </div>
    </div>
  );
};

export default Loading;
