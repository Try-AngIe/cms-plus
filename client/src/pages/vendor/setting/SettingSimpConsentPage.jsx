import React, { useState, useEffect } from 'react';
import SimpConsentQrUrlModal from '@/components/vendor/modal/SimpConsentQrUrlModal';
import ProductSelectField from '@/components/common/selects/ProductSelectField';
import Checkbox from '@/components/common/inputs/CheckBox';
import { getSimpleConsent, updateSimpleConsent } from '@/apis/simpleConsent';
import { getAllProductList } from '@/apis/product';
import useAlert from '@/hooks/useAlert';
import useSimpleConsentStore from '@/stores/useSimpleConsentStore';

const MAX_VISIBLE_PRODUCTS = 5;

const SettingSimpConsentPage = () => {
  const [isShowModal, setIsShowModal] = useState(false);
  const [products, setProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [simpleConsentId, setSimpleConsentId] = useState(null);
  const [vendorUsername, setVendorUsername] = useState('');
  const [showAllProducts, setShowAllProducts] = useState(false);

  const { selectedProducts, setSelectedProducts, checkedItems, setCheckedItems } =
    useSimpleConsentStore();

  const onAlert = useAlert();

  useEffect(() => {
    const fetchSimpleConsentAndProducts = async () => {
      setIsLoading(true);
      try {
        const simpleConsentData = await getSimpleConsent();
        setSimpleConsentId(simpleConsentData.id);
        setVendorUsername(simpleConsentData.vendorUsername);

        const newCheckedItems = {
          '실시간 CMS': simpleConsentData.paymentMethods.some(method => method.code === 'CMS'),
          카드: simpleConsentData.paymentMethods.some(method => method.code === 'CARD'),
        };
        setCheckedItems(newCheckedItems);

        const res = await getAllProductList();
        const allProducts = res.data;
        setProducts(allProducts);

        const selectedProductsData = allProducts.filter(product =>
          simpleConsentData.productIds.includes(product.productId)
        );
        setSelectedProducts(selectedProductsData);
      } catch (error) {
        console.error('데이터 가져오기 오류:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchSimpleConsentAndProducts();
  }, [setCheckedItems, setSelectedProducts]);

  const handleProductChange = newSelectedOptions => {
    setSelectedProducts(newSelectedOptions);
  };

  const handleRemoveProduct = product => {
    setSelectedProducts(selectedProducts.filter(p => p !== product));
  };

  const handleCheckboxChange = name => {
    // 현재 체크된 항목의 개수를 계산
    const checkedCount = Object.values(checkedItems).filter(Boolean).length;

    // 체크를 해제하려는 경우이고 현재 체크된 항목이 1개뿐이라면 변경하지 않음
    if (checkedItems[name] && checkedCount === 1) {
      onAlert({ msg: '최소 한 개 이상의 수단이 등록되어야 합니다.', type: 'success' });
      onAlert({ msg: '최소 한 개 이상의 수단이 등록되어야 합니다.', type: 'success' });
      return;
    }

    setCheckedItems({
      ...checkedItems,
      [name]: !checkedItems[name],
    });
  };

  const handleSave = async () => {
    try {
      const paymentMethods = Object.entries(checkedItems)
        .filter(([, isChecked]) => isChecked)
        .map(([name]) => (name === '실시간 CMS' ? 'CMS' : 'CARD'));

      const productIds = selectedProducts.map(product => product.productId);

      await updateSimpleConsent({ paymentMethods, productIds });
      alert('설정이 저장되었습니다.');
    } catch (error) {
      console.error('설정 저장 실패:', error);
      alert('설정 저장에 실패했습니다.');
    }
  };

  const toggleShowAllProducts = () => {
    setShowAllProducts(prev => !prev);
  };

  const visibleProducts = showAllProducts
    ? selectedProducts
    : selectedProducts.slice(0, MAX_VISIBLE_PRODUCTS);
  const additionalCount = selectedProducts.length - MAX_VISIBLE_PRODUCTS;

  return (
    <div className='primary-dashboard relative h-full w-full'>
      <div className='mx-2 my-4 flex h-[90%] flex-col'>
        <h2 className='mb-8 text-xl font-700 text-text_black'>간편서명동의 설정</h2>

        <div className='flex flex-grow flex-col justify-between'>
          <div>
            <div className='mb-6'>
              <h3 className='mb-3 ml-1 text-lg font-medium'>
                상품<span className='ml-1 text-red-500'>*</span>
              </h3>
              <div className='flex items-start'>
                {isLoading ? (
                  <div>상품 목록을 불러오는 중...</div>
                ) : (
                  <ProductSelectField
                    onChange={handleProductChange}
                    label='상품을 선택해주세요.'
                    required={true}
                    options={products.map(product => ({
                      value: product,
                      label: `${product.name}(${product.price.toLocaleString()}원)`,
                    }))}
                    selectedOptions={selectedProducts}
                    className='h-13 rounded-md border border-gray-300 bg-white p-4 pr-10 text-base focus:border-teal-400 focus:outline-none focus:ring-teal-400'
                  />
                )}
                <div
                  className='ml-4 flex flex-wrap'
                  style={{ maxHeight: '150px', overflowY: 'auto', flex: 1 }}>
                  {visibleProducts.map((product, index) => (
                    <span
                      key={index}
                      className='mb-2 mr-2 flex items-center justify-between rounded-full bg-teal-400 px-7 py-2 text-base text-white'
                      style={{ minWidth: '200px' }}>
                      <span className='flex-grow'>{product.name}</span>
                      <button
                        onClick={() => handleRemoveProduct(product)}
                        className='ml-2 flex-shrink-0 text-white hover:text-gray-200'>
                        ×
                      </button>
                    </span>
                  ))}
                  {!showAllProducts && additionalCount > 0 && (
                    <span
                      className='mb-2 mr-2 flex items-center justify-center rounded-full bg-gray-300 px-7 py-2 text-base text-gray-700 cursor-pointer'
                      onClick={toggleShowAllProducts}>
                      더보기 +{additionalCount}
                    </span>
                  )}
                  {showAllProducts && selectedProducts.length > MAX_VISIBLE_PRODUCTS && (
                    <span
                      className='mb-2 mr-2 flex items-center justify-center rounded-full bg-gray-300 px-7 py-2 text-base text-gray-700 cursor-pointer'
                      onClick={toggleShowAllProducts}>
                      접기
                    </span>
                  )}
                </div>
              </div>
            </div>
            <hr className='my-6 border-gray-400' />

            <div className='mb-6'>
              <h3 className='mb-4 ml-1 text-lg font-medium'>
                결제수단<span className='ml-1 text-red-500'>*</span>
              </h3>
              {!isLoading && (
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
              )}
            </div>
          </div>

          <div className='mt-auto flex justify-end pt-4 absolute bottom-5 right-5'>
            <button
              className='mr-2 rounded-lg bg-teal-400 px-6 py-2 font-bold text-white'
              onClick={handleSave}>
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
