import { useInvoiceStore } from '@/stores/useInvoiceStore';

const PaymentAuto = () => {
  const invoiceInfo = useInvoiceStore(state => state.invoiceInfo);
  console.log(invoiceInfo);

  if (!invoiceInfo) {
    return <div>로딩 중...</div>; // 또는 다른 적절한 로딩 표시
  }

  //자동결제 청구상태 확인
  const getAutoStatus = status => {
    if (status == '완납') {
      return '된';
    } else {
      return '될';
    }
  };

  return (
    <div className='flex flex-col items-center justify-between h-screen bg-white p-3'>
      <div className='text-left w-full'>
        <h1 className='text-2xl font-bold text-teal-400 mb-4'>Hyosung CMS+</h1>
        <h3 className='font-semibold text-gray-700 text-base mb-8'>
          아쿠르트에서 회원님께
          <br />
          자동결제 {getAutoStatus(invoiceInfo.billingStatus)} 청구입니다.
        </h3>
      </div>

      <div className='flex-grow flex items-center justify-center'>
        <div className='relative w-52 h-48 mb-40'>
          <div className='absolute inset-0 bg-gradient-to-br from-teal-400 to-blue-200 rounded-full opacity-50 blur-2xl' />
        </div>
      </div>
    </div>
  );
};

export default PaymentAuto;