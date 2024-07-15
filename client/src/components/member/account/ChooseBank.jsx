import React, { useState } from 'react';

const ChooseBank = () => {
  const [selectedCard, setSelectedCard] = useState(null);

  const cardOptions = [
    '기업은행',
    '국민은행',
    '우리은행',
    '수협은행',
    'NH농협은행',
    '부산은행',
    '신한은행',
    '하나은행',
    '광주은행',
    '우체국',
    'iM뱅크',
    '경남은행',
  ];

  const handleCardSelect = card => {
    setSelectedCard(card);
  };

  return (
    <>
      <h3 className='mb-8 text-base font-semibold text-gray-700'>
        이니시스
        <br />
        결제
      </h3>

      <h4 className='text-sm text-gray-500 mb-2 font-semibold'>결제금액</h4>
      <div className='mb-4 h-24 border border-mint rounded-lg p-4 flex flex-col justify-between'>
        <div>
          <p className='text-base font-semibold'>상품명1 외 1</p>
          <p className='text-xs text-gray-500'>2024년 06월</p>
        </div>
        <div className='self-end'>
          <p className='font-semibold text-lg'>11,000원</p>
        </div>
      </div>

      <h4 className='text-sm text-gray-500 mb-2 font-semibold'>결제카드</h4>
      <div className='grid grid-cols-3 gap-2 mb-4'>
        {cardOptions.map((card, index) => (
          <button
            key={index}
            className={`p-4 border rounded-lg font-semibold text-xs ${selectedCard === card ? 'bg-mint text-white' : 'bg-white text-text_grey'}`}
            onClick={() => handleCardSelect(card)}>
            {card}
          </button>
        ))}
      </div>
    </>
  );
};

export default ChooseBank;
