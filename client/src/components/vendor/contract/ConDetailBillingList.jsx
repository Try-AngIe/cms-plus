import { useNavigate, useParams } from 'react-router-dom';
import Table from '@/components/common/tables/Table';
import PagiNation from '@/components/common/PagiNation';
import { cols } from '@/utils/tableElements/billingElement';
import { useEffect, useState } from 'react';
import { getContractDetailBillingList } from '@/apis/contract';
import { formatProductsForList } from '@/utils/format/formatProducts';

const ConDetailBillingList = () => {
  const [billingList, setBillingList] = useState([]); // 청구 목록
  const [totalCount, setTotalCount] = useState(0); // 전체 건수
  const [totalPages, setTotalPages] = useState(1); // 전체 페이지 수
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지
  const [pageGroup, setPageGroup] = useState(0); // 현재 페이지 그룹
  const buttonCount = 5;

  const navigate = useNavigate();

  const contractId = useParams();

  // 계약 상세 조회 - 청구리스트
  const axiosContractDetailBillingList = async (page = currentPage) => {
    try {
      const res = await getContractDetailBillingList(contractId.id, {
        page: page,
        size: 8,
      });
      console.log('!----계약 상세 - 청구리스트 조회 성공----!'); // 삭제예정
      console.log(res.data.content);
      const transformdData = transformBillingListItem(res.data.content);
      setBillingList(transformdData);
      setTotalCount(res.data.totalCount);
      setTotalPages(res.data.totalPage || 1);
    } catch (err) {
      console.error('axiosContractDetailBillingList => ', err);
    }
  };

  // 데이터 변환
  const transformBillingListItem = items => {
    return items.map(billing => {
      const {
        memberName,
        memberPhone,
        firstProductName,
        totalProductCount,
        billingPrice,
        billingDate,
        paymentType,
        billingStatus,
      } = billing;

      return {
        ...billing,
        memberName: memberName,
        memberPhone: memberPhone,
        billingProducts: formatProductsForList(firstProductName, totalProductCount),
        billingPrice: `${billingPrice.toLocaleString()}원`,
        billingDate: billingDate,
        paymentType: paymentType.title,
        billingStatus: billingStatus.title,
      };
    });
  };

  // 청구 상세 조회 페이지 이동
  const MoveBillingDetail = async billingId => {
    navigate(`/vendor/billings/detail/${billingId}`);
    window.scrollTo(0, 0);
  };

  useEffect(() => {
    axiosContractDetailBillingList(currentPage);
  }, [currentPage]);

  return (
    <div className='h-480 flex flex-col '>
      <div className='h-full flex flex-col justify-between pt-2 px-5'>
        <Table
          size='h-[12.4%]'
          cols={cols}
          rows={billingList}
          currentPage={currentPage}
          onRowClick={item => MoveBillingDetail(item.billingId)}
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

export default ConDetailBillingList;
