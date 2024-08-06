import InputWeb from '@/components/common/inputs/InputWeb';

const ConDetailPaymentVirtual = ({ contractData }) => {
  const paymentTypeInfo = contractData.paymentTypeInfo;

  return (
    <>
      <div className='flex items-end mb-5 mt-3'>
        <InputWeb
          id='bank'
          label='은행'
          value={paymentTypeInfo.bank.title}
          type='text'
          classContainer='w-1/2 pr-3'
          disabled={true}
        />
      </div>
      <div className='flex items-end mb-5'>
        <InputWeb
          id='accountOwner'
          label='예금주'
          value={paymentTypeInfo.accountOwner}
          type='text'
          classContainer='w-full'
          disabled={true}
        />
        <div className='flex items-start justify-center text-lg w-14 py-3' />
        <InputWeb
          id='accountNumber'
          label='계좌번호'
          value={paymentTypeInfo.accountNumber}
          type='text'
          classContainer='w-full'
          disabled={true}
        />
      </div>
    </>
  );
};

export default ConDetailPaymentVirtual;
