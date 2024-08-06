import React, { useCallback, useEffect } from 'react';
import Input from '@/components/common/inputs/Input';
import SelectField from '@/components/common/selects/SelectField';
import { verifyCMS } from '@/apis/validation';
import { validateField } from '@/utils/validators';
import { formatBirthDate } from '@/utils/format/formatBirth';
import { bankOptions } from '@/utils/bank/bank';

const PaymentCMS = ({
  paymentData,
  onInputChange,
  onVerificationComplete,
  isVerified,
  contractId,
}) => {
  const cmsData = {
    paymentMethod: 'CMS',
    accountNumber: paymentData.accountNumber,
    accountOwner: paymentData.accountHolder,
    accountOwnerBirth: paymentData.accountOwnerBirth,
  };
  const handleCMSVerification = useCallback(async () => {
    try {
      const result = await verifyCMS(cmsData);
      if (result === true) {
        onVerificationComplete(true);
        onInputChange('isVerified', true);
        alert('계좌 인증이 성공적으로 완료되었습니다.');
      } else {
        onVerificationComplete(false);
        onInputChange('isVerified', false);
        alert('계좌 인증에 실패했습니다. 다시 시도해주세요.');
      }
    } catch (error) {
      console.error('CMS verification error:', error);
      onVerificationComplete(false);
      alert('계좌 인증에 실패했습니다. 다시 시도해주세요.');
    }
  }, [paymentData, onVerificationComplete]);

  const handleInputChange = useCallback(
    e => {
      const { name, value } = e.target;
      let formattedValue = value;

      if (name === 'accountOwnerBirth') {
        formattedValue = formatBirthDate(value);
      }
      onInputChange(name, formattedValue);
    },
    [onInputChange]
  );

  return (
    <div className='flex flex-col bg-white p-1'>
      <form className='space-y-4'>
        <SelectField
          label='은행'
          name='bank'
          required
          options={bankOptions}
          value={paymentData.bank}
          onChange={handleInputChange}
          disabled={!!contractId}
        />
        <Input
          label='예금주명'
          name='accountHolder'
          type='text'
          required
          placeholder='최대 20자리'
          value={paymentData.accountHolder}
          onChange={handleInputChange}
          maxLength={20}
          isValid={
            paymentData.accountHolder === '' || validateField('name', paymentData.accountHolder)
          }
          errorMsg='올바른 예금주명을 입력해주세요.'
          disabled={!!contractId}
        />
        <Input
          label='생년월일'
          name='accountOwnerBirth'
          type='text'
          required
          placeholder='YYYY-MM-DD (예: 1990-01-01)'
          value={paymentData.accountOwnerBirth}
          onChange={handleInputChange}
          maxLength={10}
          isValid={
            paymentData.accountOwnerBirth === '' ||
            validateField('birth', paymentData.accountOwnerBirth)
          }
          errorMsg='올바른 생년월일을 입력해주세요.'
          disabled={!!contractId}
        />
        <Input
          label='계좌번호'
          name='accountNumber'
          type='text'
          required
          placeholder='최대 14자리'
          value={paymentData.accountNumber}
          onChange={handleInputChange}
          maxLength={14}
          isValid={
            paymentData.accountNumber === '' ||
            !!contractId ||
            validateField('accountNumber', paymentData.accountNumber)
          }
          errorMsg='올바른 계좌번호를 입력해주세요.'
          disabled={!!contractId}
        />
      </form>
      <button
        className={`mt-4 w-full rounded-lg border py-2 text-sm font-normal transition-colors ${
          isVerified
            ? 'border-green-400 bg-green-50 text-green-400'
            : 'border-teal-400 bg-white text-teal-400 hover:bg-teal-50'
        }`}
        onClick={handleCMSVerification}
        disabled={isVerified || !paymentData.accountNumber}>
        {isVerified ? '인증 완료' : '계좌 인증하기'}
      </button>
    </div>
  );
};

export default React.memo(PaymentCMS);
