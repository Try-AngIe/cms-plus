// 대시보드 로딩 스피너 컴포넌트
const LoadingSpinner = ({ size = 'md', text = '로딩 중...', shape = 'box' }) => {
  const sizeClasses = {
    sm: 'w-8 h-8',
    md: 'w-12 h-12',
    lg: 'w-16 h-16',
    xl: 'w-20 h-20',
    xxl: 'w-28 h-28',
    xxxl: 'w-32 h-32',
  };

  return (
    <div
      className={`${shape === 'box' && 'fixed inset-3 mb-12 items-center justify-center z-50'} 
                  flex flex-col items-center py-6`}>
      {shape === 'box' && (
        <div className='fixed inset-0 bg-black bg-opacity-10' onClick={e => e.stopPropagation()} />
      )}
      <div
        className={`${shape === 'box' && 'px-24 py-10 relative shadow-dialog rounded-xl bg-white'} `}>
        <div className='flex justify-center items-center'>
          <div className={`${sizeClasses[size]} relative mb-2`}>
            <div className='absolute top-0 left-0 w-full h-full border-4 border-mint opacity-30 rounded-full animate-ping' />
            <div className='absolute top-0 left-0 w-full h-full border-4 border-mint border-t-transparent rounded-full animate-spin' />
          </div>
        </div>
        <p className='mt-4 text-text_grey font-700'>{text}</p>
      </div>
    </div>
  );
};

export default LoadingSpinner;
