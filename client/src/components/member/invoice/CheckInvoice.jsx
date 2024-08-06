import InfoRow from '@/components/common/InfoRow';
import { useInvoiceStore } from '@/stores/useInvoiceStore';

const CheckInvoice = () => {
  const invoiceInfo = useInvoiceStore(state => state.invoiceInfo);

  return (
    <div>
      <div className='w-full text-left'>
        <h3 className='mb-8 text-base font-semibold text-gray-700'>
          {invoiceInfo.member.name}님의
          <br />
          청구서를 확인해주세요.
        </h3>
      </div>
      <h4 className='text-sm text-gray-500 mb-2'>이번달 청구요금</h4>
      <div className='mb-4 h-40 border border-mint rounded-lg p-4 flex flex-col justify-between'>
        <div>
          <p className='text-base font-semibold'>{invoiceInfo.invoiceName}</p>
        </div>
        <div className='self-end'>
          <p className='font-semibold text-lg'>{invoiceInfo.billingPrice.toLocaleString()}원</p>
        </div>
      </div>
      <div className='mb-4 space-y-2 border-b border-t py-3'>
        <InfoRow label='업체명' value='메가스터디교육(주)' />
        <InfoRow label='대표자' value='손성은' />
        <InfoRow label='대표번호' value='1599-1010' />
      </div>
    </div>
  );
};

export default CheckInvoice;
