import { getBillingList, payRealTimeBilling, sendInvoice } from '@/apis/billing';
import MoveButton from '@/components/common/buttons/MoveButton';
import PagiNation from '@/components/common/PagiNation';
import SortSelect from '@/components/common/selects/SortSelect';
import Table from '@/components/common/tables/Table';
import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import addItem from '@/assets/addItem.svg';
import card from '@/assets/card.svg';
import send from '@/assets/send.svg';
import cardwhite from '@/assets/cardwhite.svg';
import Card from '@/assets/Card';
import useDebounce from '@/hooks/useDebounce';
import { cols, initialSearch, selectOptions } from '@/utils/tableElements/billingElement';
import { formatPhone } from '@/utils/format/formatPhone';
import { formatProductsForList } from '@/utils/format/formatProducts';
import PayRealtimeErrorModal from '@/components/vendor/modal/PayRealtimeErrorModal';
import useAlert from '@/hooks/useAlert';
import LoadingSpinner from '@/components/common/loadings/LoadingSpinner';
import useConfirm from '@/hooks/useConfirm';

const BillingListPage = () => {
  const [billingList, setBillingList] = useState([]); // 청구 목록
  const [billingListCount, setBillingListCount] = useState(); // 청구 목록 전체 수
  const [filteredListCount, setFilteredListCount] = useState(); // 필터링 된 목록 전체 수
  const [search, setSearch] = useState(initialSearch); // 검색 조건
  const [currentSearchParams, setCurrentSearchParams] = useState({}); // 현재 검색 조건

  const [currentorder, setCurrentOrder] = useState(''); // 정렬 방향
  const [currentorderBy, setCurrentOrderBy] = useState(''); // 정렬 항목

  const [totalPages, setTotalPages] = useState(1); // 전체 페이지 수
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지
  const [pageGroup, setPageGroup] = useState(0); // 현재 페이지 그룹
  const buttonCount = 5; // 버튼 갯수

  const [payErrors, setPayErrors] = useState();
  const [isShowPayErrorModal, setIsShowPayErrorModal] = useState(false);

  const [invoiceErrors, setInvoiceErrors] = useState();
  const [isShowInvoiceErrorModal, setIsShowInvoiceErrorModal] = useState(false);

  const [selectedBillings, setSelectedBillings] = useState([]); // 선택된 청구 목록
  const isFirstRender = useRef(true); // 최초 렌더링 여부 확인

  const [isLoading, setIsLoading] = useState(false);
  const [requestType, setRequestType] = useState('');

  const onAlert = useAlert();
  const onConfirm = useConfirm();
  const navigate = useNavigate();

  // <----- 청구 목록 조회 ----->
  const axiosBillingList = useCallback(
    async (
      searchParams = {},
      order = currentorder,
      orderBy = currentorderBy,
      page = currentPage
    ) => {
      try {
        const res = await getBillingList({
          ...searchParams,
          order: order,
          orderBy: orderBy,
          page: page,
          size: 10,
        });
        const transformdData = transformBillingListItem(res.data.content);
        console.log('!---- 청구 목록 조회 성공 ----!'); // 삭제예정
        setBillingList(transformdData);
        setFilteredListCount(res.data.totalCount);
        if (Object.keys(searchParams).length === 0) {
          setBillingListCount(res.data.totalCount);
        }
        setTotalPages(res.data.totalPage || 1);
      } catch (err) {
        console.error('axiosMemberList => ', err);
      }
    },
    [currentPage, currentorder, currentorderBy]
  );

  // <--------데이터 변환-------->
  const transformBillingListItem = data => {
    return data.map(billing => {
      const {
        billingPrice,
        firstProductName,
        totalProductCount,
        paymentType,
        billingStatus,
        memberPhone,
      } = billing;

      return {
        ...billing,
        billingPrice: `${billingPrice.toLocaleString()}원`,
        billingProducts: formatProductsForList(firstProductName, totalProductCount),
        paymentType: paymentType.title,
        billingStatus: billingStatus.title,
        memberPhone: formatPhone(memberPhone),
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

  // <--------검색 클릭 이벤트 핸들러-------->
  const handleClickSearch = async () => {
    axiosBillingList(debouncedSearchParams);
    setCurrentPage(1); // 검색 후 현재 페이지 초기화
    setPageGroup(0); // 검색 후 페이지 그룹 초기화
  };

  // 청구 선택 항목
  const handleChangeSelection = newSelections => {
    setSelectedBillings(newSelections);
  };

  // 실시간 결제
  const handleRealtimePay = async () => {
    if (!selectedBillings || selectedBillings.length === 0) {
      onAlert({ msg: '선택된 청구가 없습니다.', type: 'error' });
      return;
    }

    const confirmMessage = `${selectedBillings.length}건의 청구를 결제하시겠습니까?`;

    const isSend = await onConfirm({
      msg: confirmMessage,
      title: '실시간 결제',
    });

    if (!isSend) {
      return;
    }

    // TODO loading
    setIsLoading(true);
    setRequestType('실시간 결제 요청중...');
    setTimeout(async () => {
      const errors = [];
      await Promise.allSettled(
        selectedBillings.map(async billing =>
          payRealTimeBilling(billing.billingId).catch(err =>
            errors.push({
              from: billing,
              res: err.response.data,
              total: selectedBillings.length,
            })
          )
        )
      );
      setIsLoading(false);

      // 실패항목이 있는 경우
      if (errors.length !== 0) {
        setPayErrors(errors);
        setIsShowPayErrorModal(true);
      }
      // 실패항목이 없는 경우
      else {
        onAlert({
          msg: `${selectedBillings.length}개의 청구 결제를 성공했습니다.`,
          type: 'success',
        });
        axiosBillingList();
      }
    }, 1500);
  };

  // 청구서 발송
  const handleInvoiceSend = async () => {
    if (!selectedBillings || selectedBillings.length === 0) {
      onAlert({ msg: '선택된 청구가 없습니다.', type: 'error' });
      return;
    }

    const confirmMessage = `${selectedBillings.length}건의 청구서를 발송하시겠습니까?`;

    const isSend = await onConfirm({
      msg: confirmMessage,
      title: '청구서 발송',
    });

    if (!isSend) {
      return;
    }

    // TODO loading
    setIsLoading(true);
    setRequestType('청구서 발송 대기중...');
    setTimeout(async () => {
      const errors = [];
      await Promise.allSettled(
        selectedBillings.map(async billing =>
          sendInvoice(billing.billingId).catch(err =>
            errors.push({
              from: billing,
              res: err.response.data,
              total: selectedBillings.length,
            })
          )
        )
      );
      setIsLoading(false);

      // 실패항목이 있는 경우
      if (errors.length !== 0) {
        setInvoiceErrors(errors);
        setIsShowInvoiceErrorModal(true);
      } else {
        onAlert({
          msg: `${selectedBillings.length}개의 청구서 발송을 성공했습니다.`,
          type: 'success',
        });
        axiosBillingList();
      }
    }, 1500);
  };

  // <--------청구 상세 조회 페이지 이동-------->
  const MoveBillingDetail = async billingId => {
    console.log(billingId);
    navigate(`detail/${billingId}`);
  };

  // 청구 생성 페이지 이동
  const MoveBillingCreate = async () => {
    navigate(`create`);
  };

  // <--------디바운스 커스텀훅-------->
  const debouncedSearchParams = useDebounce(currentSearchParams, 300);

  useEffect(() => {
    if (!isFirstRender.current) {
      handleClickSearch();
    }
  }, [debouncedSearchParams]);

  useEffect(() => {
    axiosBillingList(currentSearchParams, currentorder, currentorderBy, currentPage);
  }, [currentPage]);

  // <----- 최초 렌더링 판단 ----->
  useEffect(() => {
    isFirstRender.current = false;
  }, []);

  return (
    <div className='table-dashboard flex flex-col h-1500 extra_desktop:h-full '>
      <div className='flex justify-between pt-2 pb-4 w-full'>
        <div className='flex items-center'>
          <div className='bg-mint h-7 w-7 rounded-md ml-1 mr-3 flex items-center justify-center'>
            <Card fill='#ffffff' />
          </div>
          <p className='text-text_black font-700 mr-5'>
            {' '}
            {filteredListCount} / {billingListCount}건
          </p>
          <SortSelect
            setCurrentOrder={setCurrentOrder}
            setCurrentOrderBy={setCurrentOrderBy}
            selectOptions={selectOptions}
            currentSearchParams={currentSearchParams}
            axiosList={axiosBillingList}
          />
        </div>

        <div>
          <div className='flex'>
            <MoveButton
              imgSrc={card}
              color='white'
              buttonText='실시간 결제'
              onClick={handleRealtimePay}
            />
            <MoveButton
              imgSrc={send}
              color='mint'
              buttonText='청구서 발송'
              onClick={handleInvoiceSend}
            />
            <MoveButton
              imgSrc={addItem}
              color='mint'
              buttonText='청구생성'
              onClick={MoveBillingCreate}
            />
          </div>
        </div>
      </div>

      <Table
        cols={cols}
        rows={billingList}
        search={search}
        currentPage={currentPage}
        handleChangeSearch={handleChangeSearch}
        handleClickSearch={handleClickSearch}
        onRowClick={item => MoveBillingDetail(item.billingId)}
        handleChangeSelection={handleChangeSelection}
      />

      <PagiNation
        currentPage={currentPage}
        setCurrentPage={setCurrentPage}
        totalPages={totalPages}
        pageGroup={pageGroup}
        setPageGroup={setPageGroup}
        buttonCount={buttonCount}
      />
      {isShowPayErrorModal && (
        <PayRealtimeErrorModal
          errors={payErrors}
          isShowModal={isShowPayErrorModal}
          icon={cardwhite}
          setIsShowModal={setIsShowPayErrorModal}
          modalTitle={'실시간 결제'}
        />
      )}
      {isShowInvoiceErrorModal && (
        <PayRealtimeErrorModal
          errors={invoiceErrors}
          isShowModal={isShowInvoiceErrorModal}
          icon={send}
          setIsShowModal={setIsShowInvoiceErrorModal}
          modalTitle={'청구서 발송'}
        />
      )}
      {isLoading && <LoadingSpinner size='xl' text={requestType} shape='box' />}
    </div>
  );
};

export default BillingListPage;
