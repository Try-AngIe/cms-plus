import Checkbox from '@/components/common/inputs/CheckBox';
import { useMemberPaymentStore } from '@/stores/useMemberPaymentStore';

const SimpConsentForm = ({ paymentMethod, formType }) => {
  const { isSimpConsentCheck, setIsSimpConsentCheck, setPaymentTypeInfoReq_Auto } =
    useMemberPaymentStore();

  // <----- 체크박스 선택값 변경 ----->
  const handleChangeCheckbox = value => {
    setIsSimpConsentCheck(!value);

    if (value) {
      const currentTime = new Date().toISOString();
      setPaymentTypeInfoReq_Auto({ simpleConsentReqDateTime: currentTime });
    } else {
      setPaymentTypeInfoReq_Auto({ simpleConsentReqDateTime: '' });
    }
  };

  return (
    <div className='absolute -top-[40px] left-72'>
      <Checkbox
        label='간편서명동의 설정'
        classBox='h-4 w-4 rounded-sm '
        checked={isSimpConsentCheck}
        onChange={handleChangeCheckbox}
      />
    </div>
  );
};

export default SimpConsentForm;
