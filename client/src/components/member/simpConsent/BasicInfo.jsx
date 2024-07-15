import React, { useState, useEffect } from 'react';
import Input from '@/components/common/Input';
import AddressInput from '@/components/common/AddressInput';

const BasicInfo = ({ userData, setUserData }) => {
  const [localData, setLocalData] = useState({
    name: userData.name,
    mobile: userData.mobile,
    phone: userData.phone,
    email: userData.email,
    zipcode: userData.zipcode,
    address: userData.address,
    addressDetail: userData.addressDetail,
  });

  useEffect(() => {
    setLocalData({
      name: userData.name,
      mobile: userData.mobile,
      phone: userData.phone,
      email: userData.email,
      zipcode: userData.zipcode,
      address: userData.address,
      addressDetail: userData.addressDetail,
    });
  }, [userData]);

  const handleChange = e => {
    const { name, value } = e.target;
    setLocalData(prev => ({ ...prev, [name]: value }));
  };

  const handleBlur = e => {
    const { name, value } = e.target;
    setUserData({ [name]: value });
  };

  const handleAddressChange = (field, value) => {
    setLocalData(prev => ({ ...prev, [field]: value }));
    setUserData({ [field]: value });
  };

  return (
    <div className='flex flex-col bg-white p-1'>
      <div className='w-full text-left'>
        <h3 className='mb-8 text-base font-semibold text-gray-700'>
          회원님의
          <br />
          기본정보를 확인해주세요.
        </h3>
      </div>

      <form className='space-y-4'>
        <Input
          label='회원명'
          name='name'
          type='text'
          required
          placeholder='최대 40자'
          value={localData.name}
          onChange={handleChange}
          onBlur={handleBlur}
        />
        <Input
          label='휴대전화'
          name='mobile'
          type='tel'
          required
          placeholder="'-' 없이, 최대 12자리"
          value={localData.mobile}
          onChange={handleChange}
          onBlur={handleBlur}
        />
        <Input
          label='유선전화'
          name='phone'
          type='tel'
          placeholder="'-' 없이, 최대 12자리"
          value={localData.phone}
          onChange={handleChange}
          onBlur={handleBlur}
        />
        <Input
          label='이메일'
          name='email'
          type='email'
          required
          placeholder='cms@gmail.'
          value={localData.email}
          onChange={handleChange}
          onBlur={handleBlur}
        />
        <AddressInput
          zipcode={localData.zipcode}
          address={localData.address}
          addressDetail={localData.addressDetail}
          onAddressChange={handleAddressChange}
        />
      </form>
    </div>
  );
};

export default BasicInfo;
