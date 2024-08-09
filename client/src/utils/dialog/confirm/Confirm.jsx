import { useEffect } from 'react';

const Confirm = ({ message, onClickOK, onClickCancel }) => {
  useEffect(() => {
    const handleEscape = e => {
      if (e.key === 'Escape') {
        onClickCancel();
      }
    };

    document.addEventListener('keydown', handleEscape);

    return () => document.removeEventListener('keydown', handleEscape);
  }, [onClickCancel]);

  return (
    <div className='fixed inset-0 mb-12 flex items-center justify-center z-50'>
      <div className='fixed inset-0 bg-black bg-opacity-10' onClick={e => e.stopPropagation()} />
      <div className='relative shadow-dialog bg-white rounded-lg p-5 h-80 w-96 flex flex-col justify-between '>
        <h2 className='text-xl text-text_black font-900 mb-4 border-b border-ipt_border pb-3'>
          효성 CMS+
        </h2>
        <div className='text-text_black font-700 mb-3 w-full h-full flex items-center justify-center px-4'>
          <p className='w-full break-words text-center text-lg'>{message}</p>
        </div>
        <div className='flex justify-end space-x-2'>
          <button
            className='bg-white w-1/3 text-mint transition-all duration-200 
                        font-700 px-2 py-2 text-sm rounded-lg border border-mint hover:bg-mint hover:text-white'
            onClick={onClickCancel}>
            취소
          </button>
          <button
            className='bg-mint w-2/3 text-white transition-all duration-200 
                        font-700 px-2 py-2 text-sm rounded-lg hover:bg-mint_hover'
            onClick={onClickOK}
            autoFocus>
            확인
          </button>
        </div>
      </div>
    </div>
  );
};

export default Confirm;
