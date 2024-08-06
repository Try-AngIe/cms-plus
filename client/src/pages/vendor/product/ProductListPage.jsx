import { getProductDetail, getProductList } from '@/apis/product';
import MoveButton from '@/components/common/buttons/MoveButton';
import PagiNation from '@/components/common/PagiNation';
import SortSelect from '@/components/common/selects/SortSelect';
import Table from '@/components/common/tables/Table';
import ProductModal from '@/components/vendor/modal/ProductModal';
import { validateField } from '@/utils/validators';
import { useCallback, useEffect, useRef, useState } from 'react';
import addItem from '@/assets/addItem.svg';
import Item from '@/assets/Item';
import useDebounce from '@/hooks/useDebounce';
import { cols, initialSearch, selectOptions } from '@/utils/tableElements/productElement';
import formatLongText from '@/utils/format/formatLongText';
import item from '@/assets/item.svg';

const ProductListPage = () => {
  const [productList, setProductList] = useState([]); // 상품 목록
  const [productListListCount, setProductListListCount] = useState(); // 상품 목록 전체 수
  const [filteredListCount, setFilteredListCount] = useState(); // 필터링 된 목록 전체 수
  const [search, setSearch] = useState(initialSearch); // 상품 조건
  const [currentSearchParams, setCurrentSearchParams] = useState({}); // 현재 검색 조건
  const [productDetailData, setProductDetailData] = useState(null); // 상품 상세 정보

  const [currentorder, setCurrentOrder] = useState('ASC'); // 정렬 방향
  const [currentorderBy, setCurrentOrderBy] = useState(''); // 정렬 항목

  const [totalPages, setTotalPages] = useState(1); // 전체 페이지 수
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지
  const [pageGroup, setPageGroup] = useState(0); // 현재 페이지 그룹
  const buttonCount = 5;

  const [modalTitle, setModalTitle] = useState(''); // 모달 제목
  const [isShowModal, setIsShowModal] = useState(false); // 모달 on,off
  const [isValid, setIsValid] = useState(true); // 유효성 flag

  const isFirstRender = useRef(true); // 최초 렌더링 여부 확인

  // axiosProductLists는 useEffect 훅 내부에서 사용하고 있기 때문에
  // 종속성 배열에 포함시키고 useCallback으로 함수 재생성 방지
  // 의도하지 않은 렌더링 에러 방지

  // <--------상품 목록 조회-------->
  const axiosProductList = useCallback(
    async (
      searchParams = {},
      order = currentorder,
      orderBy = currentorderBy,
      page = currentPage
    ) => {
      try {
        const res = await getProductList({
          ...searchParams,
          order: order,
          orderBy: orderBy,
          page: page,
          size: 10,
        });
        const transformdData = transformProductListItem(res.data.content);
        setProductList(transformdData);
        setFilteredListCount(res.data.totalCount);
        if (Object.keys(searchParams).length === 0) {
          setProductListListCount(res.data.totalCount);
        }
        setTotalPages(res.data.totalPage || 1);
      } catch (err) {
        console.error('axiosProductList => ', err);
      }
    },
    [currentPage, currentorder, currentorderBy]
  );

  // <--------데이터 변환-------->
  const transformProductListItem = data => {
    if (!data) return;
    return data.map(product => {
      const { productPrice, contractNumber } = product;

      return {
        ...product,
        productMemo: `${formatLongText(product.productMemo, 15) || '-'}`,
        productPrice: `${productPrice.toLocaleString()}원`,
        contractNumber: `${contractNumber.toLocaleString()}건`,
      };
    });
  };

  // <--------검색 변경 핸들러-------->
  const handleChangeSearch = (key, value) => {
    const updatedSearch = search.map(searchItem =>
      searchItem.key === key ? { ...searchItem, value: value } : searchItem
    );

    let searchParams = {};
    updatedSearch.forEach(searchMember => {
      if (searchMember.value) {
        searchParams[searchMember.key] = searchMember.value;
      }
    });

    setSearch(updatedSearch);
    setCurrentSearchParams(searchParams);
  };

  // <--------검색 안에 입력값 유효성 검사-------->
  const validateSearchParams = () => {
    for (const searchProduct of search) {
      if (searchProduct.value) {
        if (!validateField(searchProduct.key, searchProduct.value)) {
          alert(`[${searchProduct.key}]은(는) 숫자만 입력할 수 있습니다.`);
          setIsValid(false);
          return false;
        }
      }
    }
    setIsValid(true);
    return true;
  };

  // <--------검색 클릭 이벤트 핸들러-------->
  const handleClickSearch = async () => {
    if (!validateSearchParams()) return;
    axiosProductList(debouncedSearchParams);
    setCurrentPage(1); // 검색 후 현재 페이지 초기화
    setPageGroup(0); // 검색 후 페이지 그룹 초기화
    setIsValid(true); // 검색 후 유효성 flag 초기화
  };

  // <--------상품 등록 모달-------->
  const handleCreateModalOpen = () => {
    setModalTitle('상품 등록');
    setIsShowModal(true);
  };

  // <--------상품 상세조회 모달-------->
  const handleDetailModalOpen = async productId => {
    setModalTitle('상품 상세 정보');
    try {
      const res = await getProductDetail(productId);
      console.log('상품상세 : ', res.data);
      setProductDetailData(res.data);
      setIsShowModal(true);
    } catch (err) {
      console.error('axiosProductDetail => ', err.response);
    }
  };

  // <--------디바운스 커스텀훅-------->
  const debouncedSearchParams = useDebounce(currentSearchParams, 300);

  useEffect(() => {
    if (!isFirstRender.current) {
      handleClickSearch();
    }
  }, [debouncedSearchParams]);

  // <-------- 페이지 진입 시 상품 목록 조회-------->
  useEffect(() => {
    axiosProductList(currentSearchParams, currentorder, currentorderBy, currentPage);
  }, [currentPage]);

  // <----- 최초 렌더링 판단 ----->
  useEffect(() => {
    isFirstRender.current = false;
  }, []);

  return (
    <div className='table-dashboard flex flex-col h-1500 extra_desktop:h-full '>
      <div className='flex justify-between pt-2 pb-4 w-full'>
        <div className='flex items-center '>
          <div className='bg-mint h-7 w-7 rounded-md ml-1 mr-3 flex items-center justify-center'>
            <Item fill='#4FD1C5' stroke='#ffffff' />
          </div>
          <p className='text-text_black font-700 mr-5'>
            {filteredListCount} / {productListListCount}건
          </p>
          <SortSelect
            setCurrentOrder={setCurrentOrder}
            setCurrentOrderBy={setCurrentOrderBy}
            selectOptions={selectOptions}
            currentSearchParams={currentSearchParams}
            axiosList={axiosProductList}
          />
        </div>

        <div>
          <div className='flex'>
            <MoveButton
              imgSrc={addItem}
              buttonText='상품 등록'
              color='mint'
              onClick={handleCreateModalOpen}
            />
          </div>
        </div>
      </div>

      <Table
        cols={cols}
        search={search}
        rows={productList}
        currentPage={currentPage}
        handleChangeSearch={handleChangeSearch}
        handleClickSearch={handleClickSearch}
        onRowClick={item => handleDetailModalOpen(item.productId)}
      />

      <PagiNation
        currentPage={currentPage}
        setCurrentPage={setCurrentPage}
        totalPages={totalPages}
        pageGroup={pageGroup}
        setPageGroup={setPageGroup}
        buttonCount={buttonCount}
      />

      <ProductModal
        isShowModal={isShowModal}
        setIsShowModal={setIsShowModal}
        modalTitle={modalTitle}
        icon={item}
        productDetailData={productDetailData}
        refreshProductList={() =>
          axiosProductList({ searchParams: currentSearchParams, page: currentPage })
        } // 모달에서 상품 등록 완료되면 추가된거 포함해서 상품 목록 다시 렌더링
      />
    </div>
  );
};

export default ProductListPage;
