import { useState, useEffect, useCallback, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { getContractList, getContractProducts } from '@/apis/contract';
import { getAllProductList } from '@/apis/product';
import useDebounce from '@/hooks/useDebounce';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes, faSave } from '@fortawesome/free-solid-svg-icons';
import BillingForm from '@/components/vendor/billing/register/BillingRegisterBillingForm';
import ContractList from '@/components/vendor/billing/register/BillingRegisterContractList';
import { createBilling } from '@/apis/billing';
import useAlert from '@/hooks/useAlert';

const BillingRegisterPage = () => {
  /**
   * 계약 목록 페이징
   */
  const [contractList, setContractList] = useState([]);
  const [searchType, setSearchType] = useState('memberName');
  const [searchTerm, setSearchTerm] = useState('');
  const [totalPages, setTotalPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageGroup, setPageGroup] = useState(0);
  const [selectedContract, setSelectedContract] = useState(null);
  const debouncedSearchTerm = useDebounce(searchTerm, 300);

  const [products, setProducts] = useState([]); // 전체 상품 목록

  /**
   * 청구 생성 정보
   */
  const [billingData, setBillingData] = useState({
    billingType: '',
    billingDate: '',
    products: [],
  });

  const onAlert = useAlert();

  const fetchContractList = useCallback(
    async (page = currentPage) => {
      try {
        const res = await getContractList({
          [searchType]: debouncedSearchTerm,
          page,
          size: 10,
        });
        setContractList(res.data.content);
        setTotalPages(res.data.totalPage || 1);
      } catch (err) {
        console.error('청구 생성 - 계약 목록 조회 실패', err);
      }
    },
    [searchType, debouncedSearchTerm, currentPage]
  );

  const fetchContractProducts = useCallback(async contractId => {
    try {
      const res = await getContractProducts(contractId);
      return res.data;
    } catch (err) {
      console.error('청구 생성 - 계약상품 목록 조회 실패', err);
      return [];
    }
  }, []);

  const fetchAllProducts = useCallback(async () => {
    try {
      const res = await getAllProductList();
      setProducts(res.data);
    } catch (err) {
      console.error('청구 생성 - 상품 목록 조회 실패', err);
    }
  }, []);

  useEffect(() => {
    fetchAllProducts();
  }, [fetchAllProducts]);

  useEffect(() => {
    fetchContractList();
    setCurrentPage(1);
    setPageGroup(0);
  }, [debouncedSearchTerm]);

  useEffect(() => {
    fetchContractList(currentPage);
  }, [currentPage, fetchContractList]);

  const navigate = useNavigate();

  /**
   * 선택한 계약의 약정일을 기반으로 청구의 결제일을 계산한다.
   */
  const calculateBillingDate = contractDay => {
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth();
    const day = parseInt(contractDay);

    let billingDate = new Date(year, month, day);

    // 결제일은 오늘 이후 날짜여야한다.
    if (billingDate < today) {
      billingDate.setMonth(billingDate.getMonth() + 1);
    }

    return billingDate.toLocaleDateString('en-CA');
  };

  const handleSelectContract = async contract => {
    const contractProducts = await fetchContractProducts(contract.contractId);
    setBillingData(prev => ({
      ...prev,
      contractId: contract.contractId,
      billingDate: calculateBillingDate(contract.contractDay),
      products: contractProducts,
    }));
    setSelectedContract(contract);
  };

  const handleBillingDataChange = (key, value) => {
    if (key !== 'billingDate' && billingData.products.length >= 10) {
      onAlert({ msg: ' 청구는 최대 10 개의 상품을 지녀야합니다.', type: 'success' });
      return;
    }
    setBillingData(prev => ({ ...prev, [key]: value }));
  };

  // TODO
  // 현재 이 함수는 사용이 안되는것 같음 : 확인이 필요
  const handleProductAdd = newProduct => {
    if (!newProduct) return;
    if (billingData.products.length > 10) {
      onAlert({ msg: ' 청구는 최대 10 개의 상품을 지녀야합니다.', type: 'success' });
    }
    setBillingData(prev => ({
      ...prev,
      products: [...prev.products, { ...newProduct, quantity: 1 }],
    }));
  };

  const handleProductChange = (idx, column, to) => {
    const newBillingProducts = [...billingData.products];
    newBillingProducts[idx] = { ...newBillingProducts[idx], [column]: to };
    setBillingData(prev => ({
      ...prev,
      products: newBillingProducts,
    }));
  };

  const handleProductRemove = productId => {
    if (billingData.products.length < 2) {
      onAlert({ msg: ' 청구는 최소 한 개의 상품을 지녀야합니다.', type: 'success' });
      return;
    }
    setBillingData(prev => ({
      ...prev,
      products: prev.products.filter(p => p.productId !== productId),
    }));
  };

  const handleBillingSubmit = async () => {
    try {
      await createBilling(billingData);
      onAlert({ msg: ' 청구가 생성되었습니다.', type: 'success', title: '청구 생성 완료' });

      navigate(-1);
    } catch (err) {
      console.log(err);
      onAlert({ msg: ' 청구 생성에 실패했습니다..', type: 'error', err: err });

      console.error('axiosBillingCreate => ', err.response);
    }
  };

  return (
    <div className='primary-dashboard relative flex flex-col h-full'>
      <div className='flex flex-1 pt-3 '>
        {/* 왼쪽: 계약 목록 */}
        <ContractList
          searchType={searchType}
          setSearchType={setSearchType}
          searchTerm={searchTerm}
          setSearchTerm={setSearchTerm}
          contractList={contractList}
          handleSelectContract={handleSelectContract}
          currentPage={currentPage}
          setCurrentPage={setCurrentPage}
          totalPages={totalPages}
          pageGroup={pageGroup}
          setPageGroup={setPageGroup}
        />

        {/* 오른쪽: 청구 생성 정보 */}
        <div className='w-3/5 pl-4 flex flex-col h-full overflow-hidden '>
          <h2 className='ml-1  text-text_black text-xl font-800 mb-6'>청구 생성 정보</h2>
          {selectedContract ? (
            <div className='flex-1 overflow-hidden flex flex-col '>
              <BillingForm
                billingData={billingData}
                handleBillingDataChange={handleBillingDataChange}
                products={products}
                handleProductAdd={handleProductAdd}
                handleProductChange={handleProductChange}
                handleProductRemove={handleProductRemove}
              />
            </div>
          ) : (
            <div className='flex-1 flex items-center justify-center'>
              <p className='text-center text-xl text-gray-500'>계약을 선택해주세요.</p>
            </div>
          )}
        </div>
      </div>

      {/* 하단 버튼 영역 */}
      <div className='absolute -bottom-2 -right-1 flex justify-end space-x-4 p-6 font-700 '>
        <button
          type='button'
          onClick={() => navigate('/vendor/billings')}
          className='flex justify-between items-center px-5 py-2 ml-2 text-red-500 border-red-500
            rounded-lg border ease-in-out hover:bg-red-50 cursor-pointer transition-all duration-200 '>
          <FontAwesomeIcon icon={faTimes} className='mr-2' />
          <p>취소</p>
        </button>
        <button
          onClick={handleBillingSubmit}
          className='flex items-center px-5 py-2 text-white border border-mint bg-mint hover:bg-mint_hover rounded-lg transition-all duration-200'>
          <FontAwesomeIcon icon={faSave} className='mr-2' />
          <p>청구 생성</p>
        </button>
      </div>
    </div>
  );
};

export default BillingRegisterPage;
