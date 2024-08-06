import { useNavigate } from 'react-router-dom';
import edit from '@/assets/edit.svg';
import { useStatusStore } from '@/stores/useStatusStore';

const ConDetailContract = ({ contractData, updateAllInfo }) => {
  const { setStatus } = useStatusStore();
  const navigate = useNavigate();

  const handleButtonClick = () => {
    if (contractData?.contractId && contractData?.memberId) {
      navigate(`/vendor/contracts/update/${contractData.contractId}/${contractData.memberId}`);
    } else {
      console.error('Contract ID or Member ID is missing');
    }
  };

  const contractProducts = contractData?.contractProducts ?? [];
  const contractPrice = contractData?.contractPrice ?? 0;

  return (
    <div className='sub-dashboard w-1/2 h-full'>
      <div className='flex justify-between items-center border-b border-ipt_border px-2 pb-3 pt-1 h-1/12'>
        <p className='text-text_black text-xl font-800'>계약정보</p>
        <button
          className='flex justify-between items-center px-4 py-2 ml-4 text-mint
            font-700 rounded-md border border-mint cursor-pointer hover:bg-mint_hover_light'
          onClick={() => {
            handleButtonClick();
            updateAllInfo();
            setStatus(0);
          }}>
          <img src={edit} alt='edit' className='mr-2 ' />
          <p>계약수정</p>
        </button>
      </div>
      <div className='my-8 flex flex-col h-5/6'>
        <div className='flex justify-between items-center border-b border-ipt_border px-2 pt-1 pb-3 mx-5'>
          <p className='text-text_black text-lg font-400'>상품수 : {contractProducts.length}개</p>
          <p className='text-text_black text-lg font-400'>
            총금액 : {contractPrice.toLocaleString()}원
          </p>
        </div>
        <div className='mt-5 overflow-y-auto scrollbar-custom'>
          {contractProducts.map((product, idx) => (
            <div
              key={idx}
              className='flex justify-between border border-ipt_border px-5 py-5 m-3 rounded-lg'>
              <p>{product?.name ?? '상품명 없음'}</p>
              <p>{(product?.price ?? 0).toLocaleString()}원</p>
              <p>{product?.quantity ?? 0}개</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default ConDetailContract;
