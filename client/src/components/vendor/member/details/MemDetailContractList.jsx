import { getMemberDetailContractList } from '@/apis/member';
import PagiNation from '@/components/common/PagiNation';
import Table from '@/components/common/tables/Table';
import { useEffect, useState } from 'react';
import { Navigate, useLocation, useNavigate, useParams } from 'react-router-dom';
import { cols } from '@/utils/tableElements/memberContractElement';
import { formatProducts } from '@/utils/format/formatProducts';

const MemDetailContractList = () => {
  const [contractList, setContractList] = useState([]); // 계약 목록
  const [totalCount, setTotalCount] = useState(0); // 전체 건수
  const [totalPages, setTotalPages] = useState(1); // 전체 페이지 수
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지
  const [pageGroup, setPageGroup] = useState(0); // 현재 페이지 그룹
  const buttonCount = 5; // 버튼 갯수

  const navigate = useNavigate();

  const memberId = useParams();

  // 회원 상세 조회 - 계약리스트
  const axiosMemberDetailContractList = async (page = currentPage) => {
    try {
      const res = await getMemberDetailContractList(memberId.id, {
        page: page,
        size: 8,
      });
      console.log('!----회원 상세 - 계약리스트 조회 성공----!'); // 삭제예정
      const transformdData = transformContractListItem(res.data.content);
      setContractList(transformdData);
      setTotalCount(res.data.totalCount);
      setTotalPages(res.data.totalPage || 1);
    } catch (err) {
      console.error('axiosMemberDetailContractList => ', err.response);
    }
  };

  // 데이터 변환
  const transformContractListItem = data => {
    return data.map(contract => {
      const {
        contractDay,
        contractPrice,
        contractStartDate,
        contractEndDate,
        contractProducts,
        contractStatus,
      } = contract;

      return {
        ...contract,
        contractDay: `${contractDay}일`,
        contractPrice: `${contractPrice.toLocaleString()}원`,
        contractProducts: formatProducts(contractProducts),
        contractPeriod: `${contractStartDate} ~ ${contractEndDate}`,
        contractStatus: contractStatus.title,
        paymentType: contract.paymentType.title,
      };
    });
  };

  // 계약 상세 조회 페이지 이동
  const MoveContractDetail = async contractId => {
    navigate(`/vendor/contracts/detail/${contractId}`);
    window.scrollTo(0, 0);
  };

  useEffect(() => {
    axiosMemberDetailContractList(currentPage);
  }, [currentPage]);

  return (
    <div className='sub-dashboard h-640 flex flex-col mb-5 pb-3'>
      <div className='flex justify-between items-center border-b border-ipt_border px-2 pt-1 pb-3 mb-2 '>
        <p className='text-text_black text-xl font-800'>계약목록</p>
      </div>
      <div className='flex flex-col h-full justify-between pt-5 px-5'>
        <Table
          size='h-[12.4%]'
          cols={cols}
          rows={contractList}
          currentPage={currentPage}
          onRowClick={item => MoveContractDetail(item.contractId)}
        />
        <PagiNation
          currentPage={currentPage}
          setCurrentPage={setCurrentPage}
          totalPages={totalPages}
          pageGroup={pageGroup}
          setPageGroup={setPageGroup}
          buttonCount={buttonCount}
        />
      </div>
    </div>
  );
};

export default MemDetailContractList;
