import SimpConsentQrUrlModal from '@/components/vendor/modal/SimpConsentQrUrlModal';
import { useState } from 'react';
import ProductSelectField from '@/components/common/ProductSelectField';
import Checkbox from '@/components/common/CheckBox';

const SettingSimpConsentPage = () => {
  const [isShowModal, setIsShowModal] = useState(false);
  const [selectedProducts, setSelectedProducts] = useState([]);
  const [checkedItems, setCheckedItems] = useState({
    '실시간 CMS': true,
    카드: true,
  });

  const handleProductChange = newSelectedOptions => {
    setSelectedProducts(newSelectedOptions);
  };

  const handleRemoveProduct = product => {
    setSelectedProducts(prev => {
      const newSelectedProducts = prev.filter(p => p !== product);
      handleProductChange(newSelectedProducts);
      return newSelectedProducts;
    });
  };

  const handleCheckboxChange = name => {
    setCheckedItems(prev => ({
      ...prev,
      [name]: !prev[name],
    }));
  };

  return (
    <div className='relative h-full w-full rounded-xl border border-red-500 p-6 shadow-dash-board'>
      <div className='mx-8 my-8 flex h-[90%] flex-col border border-blue-500'>
        <h2 className='mb-8 text-xl font-bold'>간편서명동의 설정</h2>

        <div className='flex flex-grow flex-col justify-between'>
          <div>
            <div className='mb-6'>
              <h3 className='mb-3 ml-1 text-lg font-medium'>
                상품<span className='ml-1 text-red-500'>*</span>
              </h3>
              <div className='flex items-center'>
                <ProductSelectField
                  onChange={handleProductChange}
                  label='상품을 선택해주세요.'
                  required={true}
                  options={[
                    { value: 'JPA(5,000원)', label: 'JPA(5,000원)' },
                    { value: 'JPA2(3,000원)', label: 'JPA2(3,000원)' },
                    { value: 'JPA3(3,000원)', label: 'JPA3(3,000원)' },
                    { value: 'JPA4(3,000원)', label: 'JPA4(3,000원)' },
                    { value: 'JPA5(3,000원)', label: 'JPA5(3,000원)' },
                    { value: 'JPA6(3,000원)', label: 'JPA6(3,000원)' },
                    { value: 'JPA7(3,000원)', label: 'JPA7(3,000원)' },
                  ]}
                  selectedOptions={selectedProducts}
                  className='h-13 w-64 rounded-md border border-gray-300 bg-white p-4 pr-10 text-base focus:border-teal-400 focus:outline-none focus:ring-teal-400'
                />
                <div className='ml-4 flex flex-wrap'>
                  {selectedProducts.map((product, index) => (
                    <span
                      key={index}
                      className='mb-2 mr-2 flex items-center justify-between rounded-full bg-teal-400 px-7 py-2 text-base text-white'
                      style={{ minWidth: '200px' }}>
                      <span className='flex-grow'>{product}</span>
                      <button
                        onClick={() => handleRemoveProduct(product)}
                        className='ml-2 flex-shrink-0 text-white hover:text-gray-200'>
                        ×
                      </button>
                    </span>
                  ))}
                </div>
              </div>
            </div>
            <hr className='my-6 border-gray-400' />

            <div className='mb-6'>
              <h3 className='mb-4 ml-1 text-lg font-medium'>
                결제수단<span className='ml-1 text-red-500'>*</span>
              </h3>
              <div className='ml-1 flex items-center'>
                <Checkbox
                  name='실시간 CMS'
                  label='실시간 CMS'
                  checked={checkedItems['실시간 CMS']}
                  onChange={() => handleCheckboxChange('실시간 CMS')}
                />
                <div className='w-8' />
                <Checkbox
                  name='카드'
                  label='카드'
                  checked={checkedItems['카드']}
                  onChange={() => handleCheckboxChange('카드')}
                />
              </div>
            </div>
          </div>

          <div className='mt-auto flex justify-end pt-4'>
            <button className='mr-2 rounded-lg bg-teal-400 px-6 py-2 font-bold text-white'>
              저장
            </button>
            <button
              className='rounded-lg border border-teal-400 px-6 py-2 font-bold text-teal-400'
              onClick={() => setIsShowModal(true)}>
              URL 확인
            </button>
          </div>
        </div>

        <SimpConsentQrUrlModal
          isShowModal={isShowModal}
          setIsShowModal={setIsShowModal}
          modalTitle={'간편서명동의 QR/URL'}
        />
      </div>
    </div>
  );
};

export default SettingSimpConsentPage;
