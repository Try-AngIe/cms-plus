import MemDetailBasicInfo from '@/components/vendor/member/MemDetailBasicInfo';
import MemDetailContractList from '@/components/vendor/member/MemDetailContractList';
import MemDetailDisplay from '@/components/vendor/member/MemDetailDisplay';
import MemDetailHeader from '@/components/vendor/member/MemDetailHeader';

const MemberDetailPage = () => {
  return (
    <div>
      <MemDetailHeader />
      <MemDetailDisplay />
      <MemDetailBasicInfo />
      <MemDetailContractList />
    </div>
  );
};

export default MemberDetailPage;
