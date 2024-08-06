import { useState } from 'react';
import { ProductSelectField2 } from '@/components/common/selects/ProductSelectField';
import SelectField from '@/components/common/selects/SelectField';
import InputCalendar from '@/components/common/inputs/InputCalendar';
import InputWeb from '@/components/common/inputs/InputWeb';
import Remove from '@/assets/Remove';
import { disabledStartDate } from '@/utils/format/formatCalender';
import useAlert from '@/hooks/useAlert';

const typeOtions = [
  { value: '', label: '청구타입을 선택하세요' },
  { value: 'REGULAR', label: '정기' },
  { value: 'IRREGULAR', label: '추가' },
];

const BillingForm = ({
  billingData,
  handleBillingDataChange,
  products,
  handleProductChange,
  handleProductRemove,
}) => {
  const [editingState, setEditingState] = useState({});

  const onAlert = useAlert();

  const handleDateChange = date => {
    console.log('날짜 : ', date);
    handleBillingDataChange('billingDate', date);
  };

  const handleEditClick = (idx, field) => {
    setEditingState(prev => ({ ...prev, [idx]: { ...prev[idx], [field]: true } }));
  };

  const calcBillingPrice = mBillingProducts => {
    return mBillingProducts.reduce((sum, bp) => {
      return sum + bp.price * bp.quantity;
    }, 0);
  };

  const handleInputChange = (idx, field, value) => {
    if (field === 'price' && value && value.length >= 7) {
      onAlert({ msg: '상품 가격은 최대 99만원입니다.', type: 'success' });
      return;
    } else if (field === 'quantity' && value && value.length >= 2) {
      onAlert({ msg: '상품 수량은 최대 9개입니다.', type: 'success' });
      return;
    }
    const numericValue = value.replace(/\D/g, '');
    handleProductChange(idx, field, numericValue);
  };

  const renderEditableField = (item, idx, field) => {
    const value = item[field];

    return (
      <InputWeb
        id={field}
        type='text'
        placeholder={`${field === 'price' ? '가격' : '수량'}`}
        required
        classInput='text-center py-3 w-1/12 focus:border-mint focus:outline-none 
                    focus:ring-mint focus:ring-1 rounded-lg'
        value={value.toLocaleString()}
        onChange={e => handleInputChange(idx, field, e.target.value)}
        autoComplete='off'
        maxLength={field === 'price' ? 7 : 2}
      />
    );
  };

  return (
    <form className='flex flex-col h-full'>
      <div className='space-y-4 mb-4'>
        <div className='flex space-x-4'>
          <div className='w-full'>
            <div className='flex ml-1 mb-2 border-b border-ipt_border justify-between'>
              <SelectField
                label='청구타입'
                classContainer='mr-5 w-1/2'
                classLabel='text-15 text-text_black font-700'
                classSelect='py-3 rounded-lg'
                value={billingData.billingType}
                options={typeOtions}
                onChange={e => handleBillingDataChange('billingType', e.target.value)}
              />
              <div className='relative flex justify-center w-1/2'>
                <InputCalendar
                  id='billingDate'
                  label='결제일'
                  placeholder='년도-월-일'
                  width='100%'
                  readOnly
                  classLabel='text-15'
                  value={billingData.billingDate}
                  disabledDate={disabledStartDate}
                  handleChangeValue={e => handleDateChange(e.target.value)}
                  classContainer='w-full'
                />
              </div>
            </div>
          </div>
        </div>
        <div className='flex justify-between mb-5'>
          <div className='w-2/6 ml-1  flex-row'>
            <label className={`block text-text_black text-15 font-700 mb-2 ml-2`}>상품 추가</label>
            <ProductSelectField2
              label='상품을 선택하세요'
              options={products.map(p => ({
                value: {
                  productId: p.productId,
                  name: p.name,
                  price: p.price,
                  quantity: 1,
                },
                label: `${p.name}(${p.price.toLocaleString()}원)`,
              }))}
              selectedOptions={billingData.products.map(p => ({
                value: p,
                label: `${p.name} (${p.price.toLocaleString()}원)`,
              }))}
              onChange={newOptions => {
                handleBillingDataChange(
                  'products',
                  newOptions.map(option => option.value)
                );
              }}
            />
          </div>
          <div className='flex items-end text-lg font-800 text-text_black'>
            <p className=''>
              총 합계 : {`${calcBillingPrice(billingData.products).toLocaleString()}원`}
            </p>
          </div>
        </div>
      </div>
      <div className='relative h-[440px] overflow-y-scroll'>
        <table className='w-full'>
          <thead className='sticky top-0 z-10'>
            <tr className='flex bg-table_col shadow-column'>
              <th className='w-2/12 py-2 text-center text-text_black font-800'>상품명</th>
              <th className='w-3/12 py-2 text-center text-text_black font-800'>상품금액</th>
              <th className='w-2/12 py-2 text-center text-text_black font-800'>수량</th>
              <th className='w-3/12 py-2 text-center text-text_black font-800'>금액</th>
              <th className='w-2/12 py-2 text-center text-text_black font-800'>삭제</th>
            </tr>
          </thead>
          <tbody className='text-sm'>
            {billingData.products.map((product, idx) => (
              <tr
                key={product.productId}
                className='flex items-center border-b border-ipt_border py-2 -z-20'>
                <td className='w-2/12 text-center text-text_black'>{product.name}</td>
                <td
                  className='w-3/12 px-3 text-center  text-text_black'
                  onClick={() => handleEditClick(idx, 'price')}>
                  {renderEditableField(product, idx, 'price')}
                </td>
                <td
                  className='w-2/12 px-3 text-center  text-text_black'
                  onClick={() => handleEditClick(idx, 'quantity')}>
                  {renderEditableField(product, idx, 'quantity')}
                </td>
                <td className='w-3/12 text-center text-text_black'>
                  {(product.price * product.quantity).toLocaleString()}원
                </td>
                <td className='w-2/12 text-center text-text_black '>
                  <button>
                    <Remove onClick={() => handleProductRemove(product.productId)} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </form>
  );
};

export default BillingForm;
