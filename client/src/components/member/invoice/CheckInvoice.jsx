import InfoRow from '@/components/common/InfoRow';

const CheckInvoice = () => {
  return (
    <div>
      <div className='w-full text-left'>
        <h3 className='mb-8 text-base font-semibold text-gray-700'>
          회원님의
          <br />
          청구서를 확인해주세요.
        </h3>
      </div>
      <h4 className='text-sm text-gray-500 mb-2'>이번달 청구요금</h4>
      <div className='mb-4 h-40 border border-mint rounded-lg p-4 flex flex-col justify-between'>
        <div>
          <p className='text-base font-semibold'>상품명1 외 1</p>
          <p className='text-xs text-gray-500'>2024년 06월</p>
        </div>
        <div className='self-end'>
          <p className='font-semibold text-lg'>11,000원</p>
        </div>
      </div>
      <div className='mb-4 space-y-2 border-b border-t py-3'>
        <InfoRow label='업체명' value='(주)야쿠르트' />
        <InfoRow label='대표자' value='박민석' />
        <InfoRow label='대표번호' value='1509-1524' />
      </div>
    </div>
  );
};

export default CheckInvoice;
