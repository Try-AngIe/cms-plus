import InputWeb from '@/components/common/inputs/InputWeb';

const ConDetailPaymentType = ({ contractData }) => {
  // Safely access nested properties with default values
  const paymentTypeTitle = contractData?.paymentTypeInfo?.paymentType?.title ?? '';
  const contractDay = contractData?.contractDay ?? '';
  const contractStartDate = contractData?.contractStartDate ?? '';
  const contractEndDate = contractData?.contractEndDate ?? '';

  return (
    <div className='flex-col p-4'>
      <div className='flex flex-col justify-between flex-1'>
        <div className='flex items-end mb-5 mt-3'>
          <InputWeb
            id='paymentType'
            label='결제방식'
            value={paymentTypeTitle}
            type='text'
            classContainer='w-full'
            disabled={true}
          />
          <div className='flex items-start justify-center text-lg w-14 py-3' />
          <InputWeb
            id='contractDay'
            label='약정일'
            value={contractDay}
            type='text'
            classContainer='w-full'
            disabled={true}
          />
        </div>
        <div className='flex items-end mb-5 '>
          <InputWeb
            id='contractStartDate'
            label='계약기간'
            value={contractStartDate}
            type='text'
            classContainer='w-full'
            disabled={true}
          />
          <div className='flex items-start justify-center text-lg w-14 py-3'>~</div>
          <InputWeb
            id='contractEndDate'
            label=''
            value={contractEndDate}
            type='text'
            classContainer='w-full '
            disabled={true}
          />
        </div>
      </div>
    </div>
  );
};

export default ConDetailPaymentType;
