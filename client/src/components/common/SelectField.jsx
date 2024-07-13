import React from 'react';

const SelectField = ({ label, required, options, ...props }) => (
  <div className='mb-4'>
    <label className='mb-1 block text-sm font-medium text-gray-700'>
      {required && (
        <span
          className={`block text-sm font-medium text-slate-700 ${required ? "after:ml-0.5 after:text-red-500 after:content-['*']" : ''}`}>
          {label}
        </span>
      )}
    </label>

    <div className='relative'>
      <select
        className='w-full appearance-none rounded-md border border-gray-300 bg-white p-2 pr-10 text-sm focus:border-mint focus:outline-none focus:ring-mint'
        style={{
          WebkitAppearance: 'none',

          MozAppearance: 'none',

          appearance: 'none',
        }}
        {...props}>
        {options.map((option, index) => (
          <option key={index} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>

      <div className='pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700'>
        <svg className='h-5 w-5 fill-current' viewBox='0 0 20 20'>
          <path
            d='M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z'
            clipRule='evenodd'
            fillRule='evenodd'></path>
        </svg>
      </div>
    </div>
  </div>
);

export default SelectField;
