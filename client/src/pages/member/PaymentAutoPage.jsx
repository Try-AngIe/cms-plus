import { useInvoiceStore } from '@/stores/useInvoiceStore';
import successImage from '@/assets/success.svg';

const PaymentAuto = () => {
  const invoiceInfo = useInvoiceStore(state => state.invoiceInfo);

  if (!invoiceInfo) {
    return <div>로딩 중...</div>;
  }

  // 자동결제 청구상태 확인
  const getAutoStatus = status =>
    status === '완납' ? '자동결제 된 청구입니다.' : '자동결제 될 청구입니다.';

  return (
    <div className='flex flex-col items-center justify-between h-screen bg-white p-3'>
      <div className='text-left w-full'>
        <h1 className='text-2xl font-800 text-teal-400 mb-4'>Hyosung CMS+</h1>
        <h3 className='font-semibold text-gray-700 text-base mb-8'>
          효성 아카데미에서 회원님께
          <br />
          {getAutoStatus(invoiceInfo.billingStatus)}
        </h3>
      </div>

      <div className='flex flex-grow items-center justify-center'>
        <div className='relative mb-40 h-48 w-52'>
          <div className='absolute inset-0 rounded-full bg-gradient-to-br from-teal-400 to-blue-200 opacity-50 blur-2xl' />
          <img
            src={successImage}
            alt='Card'
            className='absolute inset-0 z-10 h-full w-full object-contain'
          />
        </div>
      </div>
    </div>
  );
};

export default PaymentAuto;
