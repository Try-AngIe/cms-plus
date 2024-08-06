import { useNavigate } from 'react-router-dom';
import edit from '@/assets/edit.svg';
import ConDetailPaymentType from './ConDetailPaymentType';
import ConDetailPaymentMethod from './ConDetailPaymentMethod';
import { useStatusStore } from '@/stores/useStatusStore';

const ConDetailPayment = ({ contractData, updateAllInfo }) => {
  const { setStatus } = useStatusStore();
  const navigate = useNavigate();

  const handleButtonClick = () => {
    navigate(`/vendor/contracts/update/${contractData.contractId}/${contractData.memberId}`);
  };

  return (
    <div className='sub-dashboard w-1/2'>
      <div className='flex justify-between items-center border-b border-ipt_border px-2 pt-1 pb-3'>
        <p className='text-text_black text-xl font-800'>결제정보</p>
        <button
          className='flex justify-between items-center px-4 py-2 ml-4 text-mint
            font-700 rounded-md border border-mint cursor-pointer hover:bg-mint_hover_light'
          onClick={() => {
            handleButtonClick();
            updateAllInfo();
            setStatus(1);
          }}>
          <img src={edit} alt='edit' className='mr-2 ' />
          <p>결제수정</p>
        </button>
      </div>
      <ConDetailPaymentType contractData={contractData} />
      {contractData?.paymentTypeInfo?.paymentType?.code !== 'BUYER' && (
        <>
          <div className='flex justify-between items-center border-b border-ipt_border px-2 pt-1 pb-3'>
            <p className='text-text_black text-xl font-800'>현재 결제수단</p>
          </div>
          <ConDetailPaymentMethod contractData={contractData} />
        </>
      )}
    </div>
  );
};

export default ConDetailPayment;
