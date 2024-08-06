import MemberChooseModal from '@/components/vendor/modal/MemberChooseModal';
import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Table from '@/components/common/tables/Table.jsx';
import { getContractList } from '@/apis/contract.js';
import PagiNation from '@/components/common/PagiNation';
import SortSelect from '@/components/common/selects/SortSelect';
import MoveButton from '@/components/common/buttons/MoveButton';
import file from '@/assets/file.svg';
import sign from '@/assets/sign.svg';
import user from '@/assets/user.svg';
import send from '@/assets/send.svg';
import File from '@/assets/File';
import { formatPhone } from '@/utils/format/formatPhone';
import useDebounce from '@/hooks/useDebounce';
import { cols, initialSearch, selectOptions } from '@/utils/tableElements/contractElement';
import { formatProductsForList } from '@/utils/format/formatProducts';
import ReqSimpConsentErrorModal from '@/components/vendor/modal/ReqSimpConsentErrorModal';
import { sendReqSimpConsent } from '@/apis/simpleConsent';
import useAlert from '@/hooks/useAlert';
import LoadingSpinner from '@/components/common/loadings/LoadingSpinner';
import useConfirm from '@/hooks/useConfirm';

const ContractListPage = () => {
  const [contractList, setContractList] = useState([]); // 계약 목록
  const [contractListCount, setContractListCount] = useState(); // 계약 목록 전체 수
  const [filteredListCount, setFilteredListCount] = useState(); // 필터링 된 목록 전체 수
  const [search, setSearch] = useState(initialSearch); // 검색 조건
  const [currentSearchParams, setCurrentSearchParams] = useState({}); // 현재 검색 조건

  const [currentorder, setCurrentOrder] = useState(''); // 정렬 방향
  const [currentorderBy, setCurrentOrderBy] = useState(''); // 정렬 항목

  const [totalPages, setTotalPages] = useState(1); // 전체 페이지 수
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지
  const [pageGroup, setPageGroup] = useState(0); // 현재 페이지 그룹
  const buttonCount = 5; // 버튼 갯수

  const [selectedContracts, setSelectedContracts] = useState([]); // 선택된 계약 목록
  const isFirstRender = useRef(true); // 최초 렌더링 여부 확인

  const [isShowModal, setIsShowModal] = useState(false);

  const [simpErrors, setSimpErrors] = useState();
  const [isShowSimpModal, setIsShowSimpModal] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const onAlert = useAlert();
  const onConfirm = useConfirm();
  const navigate = useNavigate();

  // <--------계약 목록 조회-------->
  const axiosContractList = useCallback(
    async (
      searchParams = {},
      order = currentorder,
      orderBy = currentorderBy,
      page = currentPage
    ) => {
      try {
        const res = await getContractList({
          ...searchParams,
          order: order,
          orderBy: orderBy,
          page: page,
          size: 10,
        });

        const transformdData = transformContractListItem(res.data.content);
        setContractList(transformdData);
        setFilteredListCount(res.data.totalCount);
        if (Object.keys(searchParams).length === 0) {
          setContractListCount(res.data.totalCount);
        }
        setTotalPages(res.data.totalPage || 1);
      } catch (err) {
        console.error('axiosContractList => ', err);
      }
    },
    [currentPage, currentorder, currentorderBy]
  );

  // <--------데이터 변환-------->
  const transformContractListItem = data => {
    return data.map(contract => {
      const {
        contractDay,
        contractPrice,
        firstProductName,
        totalProductCount,
        contractStatus,
        memberPhone,
      } = contract;

      return {
        ...contract,
        contractDay: `${contractDay}일`,
        contractPrice: `${contractPrice.toLocaleString()}원`,
        contractProducts: formatProductsForList(firstProductName, totalProductCount),
        contractStatus: contractStatus.title,
        paymentType: contract.paymentType.title,
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

  // <--------계약 선택 핸들러-------->
  const handleChangeSelection = newSelections => {
    setSelectedContracts(newSelections);
  };

  // <--------간편동의 버튼 클릭 핸들러-------->
  const handleClickSimpConsent = async () => {
    if (!selectedContracts || selectedContracts.length === 0) {
      onAlert({ msg: '선택된 계약이 없습니다.', type: 'error', title: '요청 실패' });
      return;
    }

    const confirmMessage = `${selectedContracts.length}건의 간편동의를 요청하시겠습니까?`;

    const isSend = await onConfirm({
      msg: confirmMessage,
      title: '간편동의 발송',
    });

    if (!isSend) {
      return;
    }

    setIsLoading(true);
    setTimeout(async () => {
      const errors = [];
      await Promise.allSettled(
        selectedContracts.map(async contract =>
          sendReqSimpConsent(contract.contractId).catch(err =>
            errors.push({
              from: contract,
              res: err.response.data,
              total: selectedContracts.length,
            })
          )
        )
      );
      setIsLoading(false);

      // 실패항목이 있는 경우
      if (errors.length !== 0) {
        setSimpErrors(errors);
        setIsShowSimpModal(true);
      } else {
        onAlert({
          msg: `${selectedContracts.length}개의 간편 서명 동의 요청에 성공했습니다.`,
          type: 'success',
          title: '요청 성공',
        });
      }
    }, 1500);
  };

  // <--------검색 클릭 이벤트 핸들러-------->
  const handleClickSearch = async () => {
    axiosContractList(debouncedSearchParams);
    setCurrentPage(1); // 검색 후 현재 페이지 초기화
    setPageGroup(0); // 검색 후 페이지 그룹 초기화
  };

  // <--------계약 상세 조회 페이지 이동-------->
  const MoveContractDetail = async contractId => {
    navigate(`detail/${contractId}`);
  };

  // <--------디바운스 커스텀훅-------->
  const debouncedSearchParams = useDebounce(currentSearchParams, 300);

  useEffect(() => {
    if (!isFirstRender.current) {
      handleClickSearch();
    }
  }, [debouncedSearchParams]);

  useEffect(() => {
    axiosContractList(currentSearchParams, currentorder, currentorderBy, currentPage);
  }, [currentPage]);

  // <----- 최초 렌더링 판단 ----->
  useEffect(() => {
    isFirstRender.current = false;
  }, []);

  return (
    <div className='table-dashboard flex flex-col h-1500 extra_desktop:h-full '>
      <div className='flex justify-between pt-2 pb-4 w-full '>
        <div className='flex items-center '>
          <div className='bg-mint h-7 w-7 rounded-md ml-1 mr-3 flex items-center justify-center'>
            <File fill='#ffffff' />
          </div>
          <p className='text-text_black font-700 mr-5'>
            {filteredListCount} / {contractListCount}건
          </p>
          <SortSelect
            setCurrentOrder={setCurrentOrder}
            setCurrentOrderBy={setCurrentOrderBy}
            selectOptions={selectOptions}
            currentSearchParams={currentSearchParams}
            axiosList={axiosContractList}
          />
        </div>
        <div>
          <div className='flex'>
            <MoveButton
              imgSrc={sign}
              color='white'
              buttonText='간편 서명 동의'
              onClick={handleClickSimpConsent}
            />
            <MoveButton
              imgSrc={file}
              color='mint'
              buttonText='신규 회원 계약 등록'
              onClick={() => navigate('/vendor/contracts/register', { state: { type: 'new' } })}
            />
            <MoveButton
              imgSrc={file}
              color='mint'
              buttonText='기존 회원 계약 등록'
              onClick={() => setIsShowModal(true)}
            />
          </div>
        </div>
      </div>

      <Table
        cols={cols}
        rows={contractList}
        search={search}
        currentPage={currentPage}
        handleChangeSearch={handleChangeSearch}
        handleClickSearch={handleClickSearch}
        onRowClick={item => MoveContractDetail(item.contractId)}
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

      <MemberChooseModal
        isShowModal={isShowModal}
        icon={user}
        setIsShowModal={setIsShowModal}
        modalTitle={'회원선택'}
      />

      <ReqSimpConsentErrorModal
        errors={simpErrors}
        icon={send}
        isShowModal={isShowSimpModal}
        setIsShowModal={setIsShowSimpModal}
        modalTitle={'간편동의 요청'}
      />
      {isLoading && <LoadingSpinner size='xl' text='간편 동의 요청중...' shape='box' />}
    </div>
  );
};

export default ContractListPage;
