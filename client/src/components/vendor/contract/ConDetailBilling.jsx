import { useNavigate } from 'react-router-dom';
import edit from '@/assets/edit.svg';
import InputWeb from '@/components/common/inputs/InputWeb';
import { formatPhone } from '@/utils/format/formatPhone';
import { useStatusStore } from '@/stores/useStatusStore';

const ConDetailBilling = ({ contractData, updateAllInfo, children }) => {
  const { setStatus } = useStatusStore();
  const navigate = useNavigate();

  const handleButtonClick = () => {
    navigate(`/vendor/contracts/update/${contractData.contractId}/${contractData.memberId}`);
  };

  return (
    <div className='flex flex-col sub-dashboard pb-2 mb-5 w-full '>
      <div className='flex justify-between items-center border-b border-ipt_border px-2 pt-1 pb-3'>
        <p className='text-text_black text-xl font-800'>청구정보</p>
        <button
          onClick={() => {
            handleButtonClick();
            updateAllInfo();
            setStatus(2);
          }}
          className='flex justify-between items-center px-4 py-2 ml-4 text-mint
            font-700 rounded-md border border-mint cursor-pointer hover:bg-mint_hover_light'>
          <img src={edit} alt='edit' className='mr-2 ' />
          <p>청구수정</p>
        </button>
      </div>
      <div className='grid grid-cols-5 gap-4 my-8 mx-4'>
        <InputWeb
          id='autobilling'
          label='청구 자동 생성'
          value={`${contractData.autoBilling ? '자동' : '수동'}`}
          type='text'
          disabled={true}
        />
        <InputWeb
          id='autosend'
          label='청구서 자동 발송'
          value={`${contractData.autoInvoiceSend ? '자동' : '수동'}`}
          type='text'
          disabled={true}
        />
        <InputWeb
          id='memberPhone'
          label='납부자 휴대번호'
          value={formatPhone(contractData.payerPhone || '')}
          type='text'
          disabled={true}
        />
        <InputWeb
          id='memberEmail'
          label='납부자 이메일'
          value={contractData.payerEmail || ''}
          type='text'
          disabled={true}
        />
        <InputWeb
          id='sendMethod'
          label='청구서 발송 수단'
          value={contractData.invoiceSendMethod?.title}
          type='text'
          disabled={true}
        />
      </div>
      <div className='flex-row justify-between items-center px-2 pt-1 pb-3 mx-5'>
        <p className='text-text_black text-lg font-400 mb-2'>전체청구 / 합계금액</p>
        <p className='text-text_black text-xl font-800'>
          총 {contractData.totalBillingCount}건 / {contractData.totalBillingPrice?.toLocaleString()}
          원
        </p>
      </div>
      {children}
    </div>
  );
};

export default ConDetailBilling;
