import File from '@/assets/File';
import User from '@/assets/User';
import wallet from '@/assets/wallet.svg';

const MemDetailDisplay = ({ memberData }) => {
  return (
    <div className='sub-dashboard mb-5 h-40 pb-6  w-full flex justify-around items-center '>
      <div className='flex items-center ml-24'>
        <File fill='#FFFFFF' className='bg-mint rounded-xl w-12 h-12 p-3 mr-4' />
        <div>
          <div className=' text-text_grey font-800'>전체 계약수</div>
          <div className='text-2xl text-text_black font-700'>
            {`총 ${memberData.contractCount.toLocaleString()}건`}
          </div>
        </div>
      </div>

      <div className='flex items-center'>
        <img src={wallet} alt='wallet' className='bg-mint rounded-xl w-12 h-12 p-3 mr-4' />
        <div>
          <div className=' text-text_grey font-800'>전체 청구건</div>
          <div className='text-2xl text-text_black font-700'>
            {`총 ${memberData.billingCount.toLocaleString()}건`}
          </div>
        </div>
      </div>

      <div className='flex items-center mr-24'>
        <User fill='#FFFFFF' className='bg-mint rounded-xl w-12 h-12 p-3 mr-4' />
        <div>
          <div className=' text-text_grey font-800'>전체 청구금액</div>
          <div className='text-2xl text-text_black font-700'>
            {memberData.totalBillingPrice ? memberData.totalBillingPrice.toLocaleString() : 0}원
          </div>
        </div>
      </div>
    </div>
  );
};

export default MemDetailDisplay;
