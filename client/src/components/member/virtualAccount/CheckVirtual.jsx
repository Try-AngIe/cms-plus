import InfoRow from '@/components/common/InfoRow';

const CheckVirtual = () => {
  return (
    <div>
      <h3 className='mb-8 text-base font-semibold text-gray-700'>
        결제 전 가상계좌 정보를
        <br />
        확인해주세요.
      </h3>
      <div className='mb-4 space-y-2 border-b border-t py-3'>
        <InfoRow label='입금은행' value='신한은행' />
        <InfoRow label='입금계좌' value='56293456234298' />
        <InfoRow label='예금주명' value='(주)야쿠르트' />
        <InfoRow label='결제금액' value='11,000원' />
        <InfoRow label='입금상태' value='입금대기' />
      </div>
    </div>
  );
};

export default CheckVirtual;
