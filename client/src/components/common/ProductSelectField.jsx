import React, { useState } from 'react';
import Checkbox from '@/components/common/CheckBox';

const ProductSelectField = ({ label, required, options, onChange, selectedOptions, ...props }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  const filteredOptions = options.filter(option =>
    option.label.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const toggleOption = value => {
    const newSelectedOptions = selectedOptions.includes(value)
      ? selectedOptions.filter(v => v !== value)
      : [...selectedOptions, value];
    onChange(newSelectedOptions);
  };

  return (
    <div className='mb-4 relative'>
      <button
        onClick={() => setIsOpen(!isOpen)}
        className='text-grey border border-gray-300 bg-white focus:ring-1 focus:outline-none focus:ring-mint focus:border-mint_hover font-medium rounded-lg text-base px-5 py-2.5 text-center inline-flex items-center'
        type='button'>
        {label}
        <svg
          className='w-2.5 h-2.5 ms-3'
          aria-hidden='true'
          xmlns='http://www.w3.org/2000/svg'
          fill='none'
          viewBox='0 0 10 6'>
          <path
            stroke='currentColor'
            strokeLinecap='round'
            strokeLinejoin='round'
            strokeWidth='2'
            d='m1 1 4 4 4-4'
          />
        </svg>
      </button>

      {isOpen && (
        <div className='z-10 absolute mt-1 bg-white rounded-lg shadow w-60'>
          <div className='p-3'>
            <input
              type='text'
              className='block w-full p-2 ps-5 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-1 focus:outline-none focus:ring-mint focus:border-mint_hover'
              placeholder='검색'
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
            />
          </div>
          <ul className='h-48 px-3 pb-3 overflow-y-auto text-sm text-gray-700'>
            {filteredOptions.map((option, index) => (
              <li key={index}>
                <div className='flex items-center font-medium p-2 ps-2 rounded hover:bg-gray-100'>
                  <Checkbox
                    name={`checkbox-item-${index}`}
                    label={option.label}
                    checked={selectedOptions.includes(option.value)}
                    onChange={() => toggleOption(option.value)}
                  />
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default ProductSelectField;
