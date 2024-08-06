import InputWeb from '@/components/common/inputs/InputWeb';
import TextArea from '@/components/common/inputs/TextArea';
import dayjs from 'dayjs';
import { formatId } from '@/utils/format/formatId';
import InputCalendar from '@/components/common/inputs/InputCalendar';
import { disabledStartDate } from '@/utils/format/formatCalender';

const BillingDetailBilling = ({
  billingData,
  billingReq,
  editable,
  onInvoiceMessageChange,
  onBillingDateChange,
}) => {
  const formatDateTime = dateTime => {
    if (!dayjs(dateTime).isValid()) {
      return null;
    }
    return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss');
  };

  const makeStatusText = billingData => {
    if (!billingData) return;
    const { billingStatus, createdDateTime, invoiceSendDateTime, paidDateTime } = billingData;
    switch (billingStatus?.code) {
      case 'CREATED':
        return `${billingStatus?.title} (${formatDateTime(createdDateTime)})`;
      case 'WAITING_PAYMENT':
      case 'NON_PAID':
        return `${billingStatus?.title} (청구서 발송: ${formatDateTime(invoiceSendDateTime) || '미발송'})`;
      case 'PAID':
        return `${billingStatus?.title} (결제: ${formatDateTime(paidDateTime)})`;
      default:
        return '-';
    }
  };

  return (
    <>
      <p className='text-text_black text-xl font-800'>청구정보</p>
      <div className='grid grid-cols-4 gap-6 my-8'>
        <InputWeb
          id='billingId'
          label='청구번호'
          value={formatId(billingData.billingId)}
          type='text'
          disabled={true}
        />
        <InputWeb
          id='invoiceName'
          label='청구서명'
          value={billingData.invoiceName}
          type='text'
          disabled={true}
        />
        <InputWeb
          id='billingStatus'
          label='청구 상태'
          value={makeStatusText(billingData)}
          readOnly
          disabled={true}
        />
      </div>

      <div className='grid grid-cols-4 gap-6 my-5'>
        <InputWeb
          id='billingType'
          label='청구타입'
          value={billingData.billingType.title || '-'}
          type='text'
          disabled={true}
        />
        <InputWeb
          id='billingCreatedDateTime'
          label='청구 생성일'
          value={formatDateTime(billingData.createdDateTime)}
          type='text'
          disabled={true}
        />
        <InputCalendar
          id='billingDate'
          label='결제일'
          value={billingReq.billingDate}
          disabledDate={disabledStartDate}
          handleChangeValue={e => onBillingDateChange(e.target.value)}
          placeholder='결제일'
          width='100%'
          height='53px'
          disabled={!editable}
        />
      </div>

      <TextArea
        id='billingMemo'
        label='청구서 메시지'
        value={billingReq.invoiceMessage || ''}
        onChange={e => onInvoiceMessageChange(e.target.value)}
        disabled={!editable}
        classContainer='my-5 w-1/2'
        classTextarea='w-1/2 h-48'
      />
    </>
  );
};

export default BillingDetailBilling;
