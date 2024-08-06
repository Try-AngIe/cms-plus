import SelectField from '@/components/common/selects/SelectField';
import InputWeb from '@/components/common/inputs/InputWeb';
import { useMemberPaymentStore } from '@/stores/useMemberPaymentStore';
import { validateField } from '@/utils/validators';
import { bankOptions } from '@/utils/bank/bank';

const VirtualAccountTypeForm = ({ paymentType, formType }) => {
  const { paymentTypeInfoReq_Virtual, setPaymentTypeInfoReq_Virtual } = useMemberPaymentStore();

  // <------ 섹렉터 은행 선택 ------>
  const handleChangeSelect = e => {
    setPaymentTypeInfoReq_Virtual({ bank: e.target.value });
  };

  // <------ 인풋 필드 입력값 변경 ------>
  const handleChangeInput = e => {
    const { id, value } = e.target;
    setPaymentTypeInfoReq_Virtual({ [id]: value });
  };

  // TODO
  // <------ 정규표현식 예외처리 ------>

  return (
    <div className='flex'>
      <SelectField
        label='은행'
        classContainer='mr-5'
        classLabel='text-15 text-text_black font-700 ml-2'
        classSelect='py-3 pr-20 p-4 rounded-lg'
        required
        options={bankOptions}
        value={paymentTypeInfoReq_Virtual.bank}
        onChange={handleChangeSelect}
      />
      <InputWeb
        id='accountOwner'
        label='예금주'
        placeholder='최대 20자리'
        type='text'
        required
        classInput='py-3 pr-20'
        value={paymentTypeInfoReq_Virtual.accountOwner}
        onChange={handleChangeInput}
        isValid={validateField('name', paymentTypeInfoReq_Virtual.accountOwner)}
        errorMsg='올바른 형식 아닙니다.'
      />
    </div>
  );
};

export default VirtualAccountTypeForm;
