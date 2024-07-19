import InputWeb from '@/components/common/inputs/InputWeb';

const ConDetailPaymentCard = ({ contractData }) => {
  const paymentMethodInfo = contractData.paymentMethodInfo;
  const paymentMethod = (paymentMethodInfo) ? paymentMethodInfo.paymentMethod : '';
  const paymentMethodCode = (paymentMethod) ? paymentMethod.code : '';
  console.log(paymentMethod);

  return (
    <>
      <div className='flex items-end mb-5 mt-3'>
        <InputWeb
          id='paymentType'
          label='결제수단'
          placeholder={paymentMethod.title}
          type='text'
          classContainer='w-full mr-6'
          disabled={true}
        />
        <InputWeb
          id='cardNumber'
          label='카드번호'
          placeholder={paymentMethodInfo.cardNumber}
          type='text'
          classContainer='w-full'
          disabled={true}
        />
      </div>
      <div className='flex items-end mb-5 '>
        <InputWeb
          id='cardOwner'
          label='소유주명'
          placeholder={paymentMethodInfo.cardOwner}
          type='text'
          classContainer='w-full mr-3'
          disabled={true}
        />
        <InputWeb
          id='accountOwnerBirth'
          label='생년월일'
          placeholder={paymentMethodInfo.cardOwnerBirth}
          type='text'
          classContainer='w-full'
          disabled={true}
        />
      </div>
    </>
  );
};

export default ConDetailPaymentCard;
