import React, { useEffect, useState } from 'react';

const AddressInput = ({ zipcode, address, addressDetail, onAddressChange }) => {
  const [localAddressDetail, setLocalAddressDetail] = useState(addressDetail);

  useEffect(() => {
    const script = document.createElement('script');
    script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
    script.async = true;
    document.head.appendChild(script);

    return () => {
      document.head.removeChild(script);
    };
  }, []);

  useEffect(() => {
    setLocalAddressDetail(addressDetail);
  }, [addressDetail]);

  const handleAddressSearch = () => {
    new window.daum.Postcode({
      oncomplete: function (data) {
        console.log('Selected address:', data);
        onAddressChange('zipcode', data.zonecode);
        onAddressChange('address', data.address);
        document.querySelector('input[name=address_detail]').focus();
      },
      width: '100%',
      height: '100%',
    }).open();
  };

  const handleAddressDetailChange = e => {
    setLocalAddressDetail(e.target.value);
  };

  const handleAddressDetailBlur = () => {
    onAddressChange('addressDetail', localAddressDetail);
  };

  return (
    <div className='block'>
      <span className='mb-1 block text-sm font-medium text-slate-700'>주소</span>
      <div className='mb-2 flex space-x-2'>
        <input
          type='text'
          name='zipcode'
          value={zipcode}
          className='flex-grow rounded-md border border-slate-300 bg-white px-3 py-2 text-sm placeholder-slate-400 shadow-sm placeholder:text-sm focus:border-mint focus:outline-none focus:ring-1 focus:ring-mint sm:text-sm'
          placeholder='우편번호'
          readOnly
          autoComplete='postal-code'
        />
        <button
          onClick={handleAddressSearch}
          className='rounded-md bg-gray-200 px-4 py-2'
          type='button'>
          🔍
        </button>
      </div>
      <input
        type='text'
        name='address'
        value={address}
        className='mb-2 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm placeholder-slate-400 shadow-sm placeholder:text-sm focus:border-mint focus:outline-none focus:ring-1 focus:ring-mint sm:text-sm'
        placeholder='주소'
        readOnly
        autoComplete='street-address'
      />
      <input
        type='text'
        name='address_detail'
        value={localAddressDetail}
        onChange={handleAddressDetailChange}
        onBlur={handleAddressDetailBlur}
        className='w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm placeholder-slate-400 shadow-sm placeholder:text-sm focus:border-mint focus:outline-none focus:ring-1 focus:ring-mint sm:text-sm'
        placeholder='상세 주소'
        autoComplete='address-line2'
      />
    </div>
  );
};

export default AddressInput;
