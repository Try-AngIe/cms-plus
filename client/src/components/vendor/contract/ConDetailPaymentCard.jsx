import InputWeb from '@/components/common/inputs/InputWeb';

const ConDetailPaymentCard = ({ contractData }) => {
  const paymentMethodInfo = contractData.paymentMethodInfo;
  const paymentMethod = paymentMethodInfo ? paymentMethodInfo.paymentMethod : '';
  const paymentMethodCode = paymentMethod ? paymentMethod.code : '';

  return (
    <>
      <div className='flex items-end mb-5 mt-3'>
        <InputWeb
          id='paymentType'
          label='결제수단'
          value={paymentMethod.title}
          type='text'
          classContainer='w-full'
          disabled={true}
        />
        <div className='flex items-start justify-center text-lg w-14 py-3' />
        <InputWeb
          id='cardNumber'
          label='카드번호'
          value={paymentMethodInfo.cardNumber}
          type='text'
          classContainer='w-full'
          disabled={true}
        />
      </div>
      <div className='flex items-end mb-5 '>
        <InputWeb
          id='cardOwner'
          label='소유주명'
          value={paymentMethodInfo.cardOwner}
          type='text'
          classContainer='w-full'
          disabled={true}
        />
        <div className='flex items-start justify-center text-lg w-14 py-3' />
        <InputWeb
          id='accountOwnerBirth'
          label='생년월일'
          value={paymentMethodInfo.cardOwnerBirth}
          type='text'
          classContainer='w-full'
          disabled={true}
        />
      </div>
    </>
  );
};

export default ConDetailPaymentCard;
