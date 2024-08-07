import { useState, useEffect, useCallback, useMemo } from 'react';
import Input from '@/components/common/inputs/Input';
import SelectField from '@/components/common/selects/SelectField';
import ProductItem from '@/components/common/ProductItem';
import { getAvailableOptions } from '@/apis/simpleConsent';
import InputCalendar from '@/components/common/inputs/InputCalendar';
import { validateField } from '@/utils/validators';

const ContractInfo = ({
  userData,
  setUserData,
  vendorId,
  contractId,
  isExistingContract,
  name,
}) => {
  const [availableProducts, setAvailableProducts] = useState([]);

  useEffect(() => {
    const fetchAvailableOptions = async () => {
      try {
        const options = await getAvailableOptions(vendorId);
        console.log(options);
        setAvailableProducts(options.availableProducts);
      } catch (error) {
        console.error('상품 리스트 업데이트 실패:', error);
      }
    };

    fetchAvailableOptions();
  }, []);

  const handleInputChange = useCallback(
    e => {
      const { name, value } = e.target;
      setUserData({
        contractDTO: { [name]: value },
      });
    },
    [setUserData]
  );

  const handleProductChange = useCallback(
    e => {
      const productId = e.target.value;
      if (productId) {
        const selectedProduct = availableProducts.find(p => p.productId.toString() === productId);
        const newProduct = {
          productId: selectedProduct.productId,
          productName: selectedProduct.productName,
          price: selectedProduct.productPrice,
          quantity: 1,
        };

        if (!userData.contractDTO.items.some(item => item.productId === newProduct.productId)) {
          const updatedItems = [...userData.contractDTO.items, newProduct];
          setUserData({
            ...userData,
            contractDTO: {
              ...userData.contractDTO,
              selectedProduct: productId,
              items: updatedItems,
            },
          });
        }
      } else {
        setUserData({
          ...userData,
          contractDTO: { ...userData.contractDTO, selectedProduct: '' },
        });
      }
    },
    [availableProducts, userData, setUserData]
  );

  const updateQuantity = useCallback(
    (index, change) => {
      const updatedItems = userData.contractDTO.items.map((item, i) =>
        i === index ? { ...item, quantity: Math.max(1, item.quantity + change) } : item
      );
      setUserData({
        ...userData,
        contractDTO: {
          ...userData.contractDTO,
          items: updatedItems,
        },
      });
    },
    [userData, setUserData]
  );

  const removeItem = useCallback(
    index => {
      const updatedItems = userData.contractDTO.items.filter((_, i) => i !== index);
      setUserData({
        ...userData,
        contractDTO: {
          ...userData.contractDTO,
          items: updatedItems,
        },
      });
    },
    [userData, setUserData]
  );

  const handleDateChange = useCallback(
    (e, field) => {
      const newDate = e.target.value;
      setUserData({
        contractDTO: { [field]: newDate },
      });

      if (field === 'startDate' && new Date(newDate) > new Date(userData.contractDTO.endDate)) {
        setUserData({
          contractDTO: { endDate: newDate },
        });
      } else if (
        field === 'endDate' &&
        new Date(newDate) < new Date(userData.contractDTO.startDate)
      ) {
        setUserData({
          contractDTO: { startDate: newDate },
        });
      }
    },
    [userData.contractDTO.startDate, userData.contractDTO.endDate, setUserData]
  );

  const productOptions = useMemo(
    () => [
      { value: '', label: '상품을 선택해주세요' },
      ...availableProducts.map(product => ({
        value: product.productId.toString(),
        label: `${product.productName}(${product.productPrice.toLocaleString()}원)`,
      })),
    ],
    [availableProducts]
  );

  const totalPrice = useMemo(
    () => userData.contractDTO.items.reduce((sum, item) => sum + item.quantity * item.price, 0),
    [userData.contractDTO.items]
  );

  return (
    <div className='flex flex-col bg-white p-1'>
      <div className='w-full text-left'>
        <h3 className='mb-8 text-base font-semibold text-gray-700'>
          {name}님의
          <br />
          계약정보를 확인해주세요.
        </h3>
      </div>

      <Input
        label='계약명'
        name='contractName'
        type='text'
        required
        placeholder='최대 20자리'
        className='pb-6'
        value={userData.contractDTO.contractName}
        onChange={handleInputChange}
        maxLength={20}
        isValid={
          !userData.contractDTO.contractName ||
          validateField('contractName', userData.contractDTO.contractName)
        }
        errorMsg='올바른 계약명을 입력해주세요.'
        disabled={!!contractId}
      />

      <SelectField
        label='상품'
        required
        options={productOptions}
        value={userData.contractDTO.selectedProduct}
        onChange={handleProductChange}
        disabled={!!contractId}
      />

      {userData.contractDTO.items.map((item, index) => (
        <ProductItem
          key={index}
          item={item}
          onUpdateQuantity={change => updateQuantity(index, change)}
          onRemove={() => removeItem(index)}
          isExisting={isExistingContract}
        />
      ))}

      <div className='mb-4 text-right text-sm font-semibold'>
        합계: {totalPrice.toLocaleString()}원
      </div>

      <div className='mb-4'>
        <label className='mb-1 block text-sm font-medium text-gray-700'>
          <span
            className={`block text-sm font-medium text-slate-700 after:ml-0.5 after:text-red-500 after:content-['*']`}>
            기간
          </span>
        </label>
        <div className='flex w-full items-center'>
          <InputCalendar
            id='startDate'
            readOnly={true}
            value={userData.contractDTO.startDate}
            handleChangeValue={e => handleDateChange(e, 'startDate')}
            placeholder='시작 날짜'
            width='100%'
            disabled={!!contractId}
          />
          <span className='mx-2 flex-shrink-0 text-gray-500'>~</span>
          <InputCalendar
            id='endDate'
            readOnly={true}
            value={userData.contractDTO.endDate}
            handleChangeValue={e => handleDateChange(e, 'endDate')}
            placeholder='종료 날짜'
            width='100%'
            disabled={!!contractId}
          />
        </div>
      </div>

      <SelectField
        label='약정일'
        required
        options={[...Array(31)].map((_, i) => ({ value: i + 1, label: `${i + 1}일` }))}
        value={userData.contractDTO.contractDay}
        disabled={!!contractId}
        onChange={e => {
          const value = parseInt(e.target.value);
          setUserData({
            contractDTO: { contractDay: value },
          });
        }}
      />
    </div>
  );
};

//forwardRef를 사용하여 정의된 컴포넌트에 displayName을 명시적으로 설정
ContractInfo.displayName = 'ContractInfo';

export default ContractInfo;
