import React from 'react';
import Input from '@/components/common/Input';
import AddressInput from '@/components/common/AddressInput';

const BasicInfo = () => {
  return (
    <div className='flex flex-col p-3 bg-white'>
      <div className='text-left w-full'>
        <h3 className='font-semibold text-gray-700 text-base mb-8'>
          회원님의
          <br />
          기본정보를 확인해주세요.
        </h3>
      </div>

      <form className='space-y-4'>
        <Input label='회원명' name='name' type='text' required placeholder='최대 40자' />
        <Input
          label='휴대전화'
          name='mobile'
          type='tel'
          required
          placeholder="'-' 없이, 최대 12자리"
        />
        <Input label='유선전화' name='phone' type='tel' placeholder="'-' 없이, 최대 12자리" />
        <Input label='이메일' name='email' type='email' required placeholder='cms@gmail.' />
        <AddressInput />
      </form>
    </div>
  );
};

export default BasicInfo;
