import InputWeb from '@/components/common/inputs/InputWeb';
import ConDetailPaymentCard from './ConDetailPaymentCard';
import ConDetailPaymentCMS from './ConDetailPaymentCMS';
import ConDetailPaymentVirtual from './ConDetailPaymentVirtual';
import formatDateTime from '@/utils/format/formatDateTime';

const ConDetailPaymentMethod = ({ contractData }) => {
  const paymentTypeInfo = contractData?.paymentTypeInfo ?? {};
  const paymentType = paymentTypeInfo?.paymentType?.code ?? '';

  const componentMap = {
    AUTO: () => {
      const paymentMethodInfo = contractData?.paymentMethodInfo ?? {};
      const paymentMethodCode = paymentMethodInfo?.paymentMethod?.code ?? '';

      let PaymentComponent = null;
      if (paymentMethodCode === 'CARD') {
        PaymentComponent = ConDetailPaymentCard;
      } else if (paymentMethodCode === 'CMS') {
        PaymentComponent = ConDetailPaymentCMS;
      }

      // 승인, 대기중, 미동의
      const title = paymentTypeInfo.consentStatus.title;
      let simpleConsentContent = null;
      if (title === '승인') {
        const fileType = paymentTypeInfo.consentImgUrl ? '동의서 제출완료' : '서명 이미지 제출완료';
        simpleConsentContent = `${title}(${fileType}) `;
      } else if (title === '대기중') {
        simpleConsentContent = `${title}(${formatDateTime(paymentTypeInfo.simpleConsentReqDateTime)})`;
      } else if (title === '미동의') {
        simpleConsentContent = `${title}(간편 서명 동의 필요)`;
      }

      return (
        <>
          {PaymentComponent && <PaymentComponent contractData={contractData} />}
          <InputWeb
            id='simpConsentInfo'
            label='동의여부'
            value={simpleConsentContent}
            type='text'
            classContainer='w-1/2 pr-3'
            disabled={true}
          />
        </>
      );
    },
    VIRTUAL: () => <ConDetailPaymentVirtual contractData={contractData} />,
  };

  const Content = componentMap[paymentType] ?? (() => null);

  return (
    <div className='flex-col p-4'>
      <div className='flex flex-col justify-between flex-1'>
        <Content />
      </div>
    </div>
  );
};

export default ConDetailPaymentMethod;
