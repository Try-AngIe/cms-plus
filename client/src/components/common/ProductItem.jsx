import React from 'react';

const ProductItem = ({ item, onUpdateQuantity, onRemove }) => (
  <div className='mb-2 flex items-center justify-between border-b pb-2 text-sm'>
    <span>{item.name}</span>
    <div className='flex items-center'>
      <div className='mr-2 flex items-center rounded-md border'>
        <button onClick={() => onUpdateQuantity(-1)} className='bg-gray-100 px-2 py-1'>
          -
        </button>
        <span className='px-4 py-1'>{item.quantity}</span>
        <button onClick={() => onUpdateQuantity(1)} className='bg-gray-100 px-2 py-1'>
          +
        </button>
      </div>
      <span className='mr-2'>{item.quantity * item.price}원</span>
      <button onClick={onRemove} className='text-gray-400'>
        ×
      </button>
    </div>
  </div>
);

export default ProductItem;
