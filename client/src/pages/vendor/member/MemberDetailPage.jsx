import { getMemberDetail } from '@/apis/member';
import MemDetailBasicInfo from '@/components/vendor/member/details/MemDetailBasicInfo';
import MemDetailContractList from '@/components/vendor/member/details/MemDetailContractList';
import MemDetailDisplay from '@/components/vendor/member/details/MemDetailDisplay';
import MemDetailHeader from '@/components/vendor/member/details/MemDetailHeader';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

const MemberDetailPage = () => {
  const [memberData, setMemberData] = useState({
    billingCount: '',
    contractCount: '',
    createdDateTime: '',
    memberAddress: {
      address: '',
      addressDetail: '',
      zipcode: '',
    },
    memberEmail: '',
    memberEnrollDate: '',
    memberHomePhone: '',
    memberId: '',
    memberMemo: '',
    memberName: '',
    memberPhone: '',
    modifiedDateTime: '',
    totalBillingPrice: '',
  });

  const memberId = useParams();

  // <------ 회원 상세 조회 ------>
  const axiosMemberDetail = async () => {
    try {
      const res = await getMemberDetail(memberId.id);
      console.log('!----회원 상세 조회 성공----!'); // 삭제예정
      setMemberData(res.data);
    } catch (err) {
      console.error('axiosMemberDetail => ', err.response);
    }
  };

  // <----- 회원 정보 zustand와 로컬에 저장 ----->
  useEffect(() => {
    axiosMemberDetail();
  }, []);

  return (
    <div>
      <MemDetailHeader memberData={memberData} />
      <MemDetailDisplay memberData={memberData} />
      <MemDetailBasicInfo memberData={memberData} />
      <MemDetailContractList />
    </div>
  );
};

export default MemberDetailPage;
