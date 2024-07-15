import ConDetailBilling from '@/components/vendor/contract/ConDetailBilling';
import ConDetailContract from '@/components/vendor/contract/ConDetailContract';
import ConDetailHeader from '@/components/vendor/contract/ConDetailHeader';
import ConDetailPayment from '@/components/vendor/contract/ConDetailPayment';

const ContractDetailPage = () => {
  return (
    <div>
      <ConDetailHeader />
      <div className='flex'>
        <ConDetailContract />
        <ConDetailPayment />
      </div>
      <ConDetailBilling />
    </div>
  );
};

export default ContractDetailPage;
