import { useInvoiceStore } from '@/stores/useInvoiceStore';

const ChooseBank = () => {
  const { setSelectedBank, selectedBank, invoiceInfo } = useInvoiceStore();

  const bankOptions = [
    '기업은행',
    '국민은행',
    '우리은행',
    '수협은행',
    'NH농협은행',
    '부산은행',
    '신한은행',
    '하나은행',
    '광주은행',
    '우체국',
    'iM뱅크',
    '경남은행',
  ];

  const handleBankSelect = bank => {
    setSelectedBank(bank);
  };

  if (!invoiceInfo) {
    return;
  }

  return (
    <>
      <h3 className='mb-8 text-base font-semibold text-gray-700'>
        이니시스
        <br />
        결제
      </h3>

      <h4 className='text-sm text-gray-500 mb-2 font-semibold'>결제금액</h4>
      <div className='mb-4 h-24 border border-mint rounded-lg p-4 flex flex-col justify-between'>
        <div>
          <p className='text-base font-semibold'>{invoiceInfo.invoiceName}</p>
          <p className='text-xs text-gray-500'>{invoiceInfo.billingDate}</p>
        </div>
        <div className='self-end'>
          <p className='font-semibold text-lg'>{invoiceInfo.billingPrice.toLocaleString()}원</p>
        </div>
      </div>

      <h4 className='text-sm text-gray-500 mb-2 font-semibold'>결제은행</h4>
      <div className='grid grid-cols-3 gap-2 mb-4'>
        {bankOptions.map((bank, index) => (
          <button
            key={index}
            className={`p-4 border rounded-lg font-semibold text-xs ${selectedBank === bank ? 'bg-mint text-white' : 'bg-white text-text_grey'}`}
            onClick={() => handleBankSelect(bank)}>
            {bank}
          </button>
        ))}
      </div>
    </>
  );
};

export default ChooseBank;
