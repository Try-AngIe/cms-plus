import InfoRow from '@/components/common/InfoRow';

const CheckVirtual = ({ invoiceInfo }) => {
  const getDepositStatus = status => {
    switch (status) {
      case '수납대기':
        return '입금대기';
      case '완납':
        return '입금완료';
      default:
        return status;
    }
  };

  return (
    <div>
      <h3 className='mb-8 text-base font-semibold text-gray-700'>
        결제 전 가상계좌 정보를
        <br />
        확인해주세요.
      </h3>
      <div className='mb-4 space-y-2 border-b border-t py-3'>
        <InfoRow label='입금은행' value={invoiceInfo.paymentType.bank.title} />
        <InfoRow label='입금계좌' value={invoiceInfo.paymentType.accountNumber} />
        <InfoRow label='예금주명' value='효성학원' />
        <InfoRow label='결제금액' value={`${invoiceInfo.billingPrice.toLocaleString()}원`} />
        <InfoRow label='입금상태' value={getDepositStatus(invoiceInfo.billingStatus)} />
      </div>
    </div>
  );
};

export default CheckVirtual;
