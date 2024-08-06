import MoveButton from '@/components/common/buttons/MoveButton';
import PagiNation from '@/components/common/PagiNation';
import SortSelect from '@/components/common/selects/SortSelect';
import Table from '@/components/common/tables/Table';
import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import registerManyUser from '@/assets/registerManyUser.svg';
import registerUser from '@/assets/registerUser.svg';
import { getMemberList } from '@/apis/member';
import User from '@/assets/User';
import { formatPhone } from '@/utils/format/formatPhone';
import useDebounce from '@/hooks/useDebounce';
import { cols, initialSearch, selectOptions } from '@/utils/tableElements/memberElement';
import MemberExcelModal from '@/components/vendor/modal/MemberExcelModal';
import user from '@/assets/user.svg';

const MemberListPage = () => {
  const [memberList, setMemberList] = useState([]); // 회원 목록
  const [memberListCount, setMemberListCount] = useState(); // 회원 목록 전체 수
  const [filteredListCount, setFilteredListCount] = useState(); // 필터링 된 목록 전체 수
  const [search, setSearch] = useState(initialSearch); // 검색 조건
  const [currentSearchParams, setCurrentSearchParams] = useState({}); // 현재 검색 조건

  const [currentorder, setCurrentOrder] = useState(''); // 정렬 방향
  const [currentorderBy, setCurrentOrderBy] = useState(''); // 정렬 항목

  const [totalPages, setTotalPages] = useState(1); // 전체 페이지 수
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지
  const [pageGroup, setPageGroup] = useState(0); // 현재 페이지 그룹
  const buttonCount = 5; // 버튼 갯수

  const [isShowExcelModal, setIsShowExcelModal] = useState(false); // 대량 회원 등록
  const isFirstRender = useRef(true); // 최초 렌더링 여부 확인

  const navigate = useNavigate();

  // <----- 회원 목록 조회 ----->
  const axiosMemberList = useCallback(
    async (
      searchParams = {},
      order = currentorder,
      orderBy = currentorderBy,
      page = currentPage
    ) => {
      try {
        const res = await getMemberList({
          ...searchParams,
          order: order,
          orderBy: orderBy,
          page: page,
          size: 10,
        });
        const transformdData = transformMemberListItem(res.data.content);
        console.log('!---- 회원 목록 조회 성공 ----!'); // 삭제예정
        setMemberList(transformdData);
        setFilteredListCount(res.data.totalCount);
        if (Object.keys(searchParams).length === 0) {
          setMemberListCount(res.data.totalCount);
        }
        setTotalPages(res.data.totalPage || 1);
      } catch (err) {
        console.error('axiosMemberList => ', err.response);
      }
    },
    [currentPage, currentorder, currentorderBy]
  );

  // <----- 데이터 변환 ----->
  const transformMemberListItem = data => {
    return data.map(member => {
      const { contractPrice, contractCount, memberPhone } = member;

      return {
        ...member,
        contractPrice: `${contractPrice.toLocaleString()}원`,
        contractCount: `${contractCount.toLocaleString()}건`,
        memberPhone: formatPhone(memberPhone),
      };
    });
  };

  // <----- 검색 변경 핸들러 ----->
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

  const handleSetMemberExcelModal = value => {
    if (isShowExcelModal) {
      axiosMemberList();
    }
    setIsShowExcelModal(value);
  };

  // <----- 검색 클릭 이벤트 핸들러 ----->
  const handleClickSearch = async () => {
    axiosMemberList(debouncedSearchParams);
    setCurrentPage(1); // 검색 후 현재 페이지 초기화
    setPageGroup(0); // 검색 후 페이지 그룹 초기화
  };

  // <----- 회원 상세 조회 페이지 이동 ----->
  const MoveMemberDetail = async memberId => {
    navigate(`detail/${memberId}`);
  };

  // <----- 디바운스 커스텀훅 ----->
  const debouncedSearchParams = useDebounce(currentSearchParams, 300);

  useEffect(() => {
    if (!isFirstRender.current) {
      handleClickSearch();
    }
  }, [debouncedSearchParams]);

  useEffect(() => {
    axiosMemberList(currentSearchParams, currentorder, currentorderBy, currentPage);
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
            <User fill='#ffffff' />
          </div>
          <p className='text-text_black font-700 mr-5'>
            {filteredListCount} / {memberListCount} 건
          </p>
          <SortSelect
            setCurrentOrder={setCurrentOrder}
            setCurrentOrderBy={setCurrentOrderBy}
            selectOptions={selectOptions}
            currentSearchParams={currentSearchParams}
            axiosList={axiosMemberList}
          />
        </div>
        <div>
          <div className='flex'>
            <MoveButton
              imgSrc={registerManyUser}
              color='mint'
              buttonText='대량 회원 등록'
              onClick={() => setIsShowExcelModal(true)}
            />
            <MoveButton
              imgSrc={registerUser}
              color='mint'
              buttonText='회원 등록'
              onClick={() => navigate('register')}
            />
          </div>
        </div>
      </div>
      <Table
        cols={cols}
        rows={memberList}
        search={search}
        currentPage={currentPage}
        handleChangeSearch={handleChangeSearch}
        handleClickSearch={handleClickSearch}
        onRowClick={item => MoveMemberDetail(item.memberId)}
      />

      <PagiNation
        currentPage={currentPage}
        setCurrentPage={setCurrentPage}
        totalPages={totalPages}
        pageGroup={pageGroup}
        setPageGroup={setPageGroup}
        buttonCount={buttonCount}
      />

      <MemberExcelModal
        isShowModal={isShowExcelModal}
        setIsShowModal={handleSetMemberExcelModal}
        icon={user}
        modalTitle={'대량 회원 등록'}
      />
    </div>
  );
};

export default MemberListPage;
