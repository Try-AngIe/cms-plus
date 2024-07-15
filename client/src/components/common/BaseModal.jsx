import closeIcon from '@/assets/close.svg';
import itemIcon from '@/assets/item.svg';

const BaseModal = ({ isShowModal, setIsShowModal, modalTitle, height, width, children }) => {
  return (
    <>
      {isShowModal ? (
        <div className='fixed inset-0 z-50 flex items-center justify-center'>
          <div className='fixed inset-0 bg-black opacity-25' />
          <div
            className={`z-10 flex flex-col rounded-lg bg-white p-6 shadow-lg ${height} ${width}`}>
            <div className='mb-4 flex items-center justify-between border-b border-ipt_border pb-3'>
              <img src={itemIcon} alt='item' className='h-7 w-7 bg-mint' />
              <p className='text-xl font-bold'>{modalTitle}</p>

              <button className='text-black' onClick={() => setIsShowModal(false)}>
                <img src={closeIcon} alt='close' className='h-4 w-4' />
              </button>
            </div>
            <div>{children}</div>
          </div>
        </div>
      ) : null}
    </>
  );
};

export default BaseModal;
