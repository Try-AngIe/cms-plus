import { getProductDetail, getProductList } from '@/apis/product';
import Table from '@/components/common/Table';
import ProductModal from '@/components/vendor/modal/ProductModal';
import { useEffect, useState } from 'react';

// 상품 목록 조회 컬럼
const cols = ['No', '상품명', '금액', '계약수', '생성일', '비고'];

// 상품 조건 설정
const initialSearch = [
  { key: 'checkbox', type: 'hidden', value: '' },
  { key: 'No', type: 'hidden', value: '' },
  { key: '상품명', type: 'text', value: '' },
  { key: '금액', type: 'text', value: '' },
  {
    key: '계약수',
    type: 'text',
    value: '',
  },
  { key: '생성일', type: 'text', value: '' },
  { key: '비고', type: 'text', value: '' },
];

const ProductListPage = () => {
  const [isShowModal, setIsShowModal] = useState(false); // 모달 on,off
  const [modalTitle, setModalTitle] = useState(''); // 모달 제목 상태
  const [productId, setProductId] = useState(''); // 생성일ID
  const [productList, setProductList] = useState([]); // 생성일 목록
  const [productDetailData, setProductDetailData] = useState(null); // 생성일 상세 정보
  const [search, setSearch] = useState(initialSearch); // 상품 조건
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지 상태
  const [totalPages, setTotalPages] = useState(1); // 전체 페이지 수

  // 상품 목록 조회 함수
  const fetchProductList = async (searchParams = {}) => {
    try {
      const res = await getProductList(searchParams);
      const formattedData = res.data.data.map((data, index) => ({
        //받아온 data 테이블 형식에 맞게 포맷팅
        No: index + 1,
        상품명: data.productName,
        금액: data.productPrice,
        계약수: data.contractNumber,
        생성일: data.productCreatedDate,
        비고: data.productMemo || '',
        productId: data.productId, // 상품 상세 조회에 사용할 상품ID 매핑
      }));
      setProductList(formattedData);
      setTotalPages(res.data.totalPages || 1); // 전체 페이지 수 설정
    } catch (err) {
      console.error('fetchProductList => ', err.response.data);
    }
  };

  // 페이지 진입 시 상품 목록 조회
  useEffect(() => {
    fetchProductList({ page: currentPage });
  }, [currentPage]);

  // 상품 등록 모달 열기용 이벤트핸들러
  const handleOpenRegisterModal = () => {
    setModalTitle('상품 등록');
    setIsShowModal(true);
  };

  // 상품 상세조회 모달 열기용 이벤트핸들러
  const handleOpenDetailModal = async productId => {
    setModalTitle('상품 상세 정보');
    try {
      const res = await getProductDetail(productId);
      setProductDetailData(res.data);
      setIsShowModal(true);
    } catch (err) {
      console.error('axiosProductDetail => ', err.response.data);
    }
  };

  // 조건 변경 핸들러
  const handleSearchChange = (key, value) => {
    setSearch(prev =>
      prev.map(searchProduct =>
        searchProduct.key === key ? { ...searchProduct, value: value } : searchProduct
      )
    );
  };

  // 검색 클릭 이벤트 핸들러
  const handleSearchClick = async () => {
    const searchParams = {
      page: 1, // 기본 페이지
      size: 10, // 기본 사이즈
    };
    search.forEach(searchProduct => {
      if (searchProduct.value) {
        const keyMapping = {
          상품명: 'productName',
          금액: 'productPrice',
          계약수: 'contractNumber',
          생성일: 'productCreatedDate',
          비고: 'productMemo',
        };
        const paramKey = keyMapping[searchProduct.key];
        if (paramKey) {
          searchParams[paramKey] = searchProduct.value;
        }
      }
    });
    await fetchProductList(searchParams);
    setCurrentPage(1); // 검색 후 페이지(현재 보이는 페이지 버튼 숫자)를 1로 초기화
  };

  // 페이지 변경 핸들러
  const handlePageChange = page => {
    setCurrentPage(page);
    fetchProductList({ page });
  };

  return (
    <div className='primary-dashboard h-full w-full flex flex-col'>
      <button
        className='rounded-lg bg-mint hover:bg-mint_hover p-3 font-bold text-white w-32'
        onClick={handleOpenRegisterModal}>
        임시 상품 등록 모달
      </button>

      <div className='flex-grow overflow-auto'>
        <Table
          cols={cols}
          search={search}
          items={productList}
          handleSearchChange={handleSearchChange}
          onRowClick={item => handleOpenDetailModal(item.productId)}
          onSearchClick={handleSearchClick}
        />
      </div>

      {/* 페이지네이션(미완; 데이터 많아야 검토 가능할 것 같음) */}
      <div className='flex justify-center mt-5'>
        {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
          <button
            key={page}
            className={`mx-1 px-3 py-1 border rounded ${page === currentPage ? 'bg-mint hover:bg-mint_hover text-white' : 'bg-gray-200'}`}
            onClick={() => handlePageChange(page)}>
            {page}
          </button>
        ))}
      </div>

      <ProductModal
        isShowModal={isShowModal}
        setIsShowModal={setIsShowModal}
        modalTitle={modalTitle}
        productDetailData={productDetailData}
      />
    </div>
  );
};

export default ProductListPage;
