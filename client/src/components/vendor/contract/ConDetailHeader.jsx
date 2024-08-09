import clock from '@/assets/clock.svg';
import File from '@/assets/File';

// 날짜 형식화를 위한 유틸리티 함수
const formatDateTime = dateTimeString => {
  if (!dateTimeString) return '정보 없음';
  try {
    return dateTimeString.replace('T', ' ').split('.')[0];
  } catch (error) {
    console.error('Date formatting error:', error);
    return '형식 오류';
  }
};

const ConDetailHeader = ({ contractData }) => {
  const contractName = contractData?.contractName ?? '계약명 없음';
  const contractId = contractData?.contractId ?? 0;
  const createdDateTime = contractData?.createdDateTime ?? '';
  const modifiedDateTime = contractData?.modifiedDateTime ?? '';

  return (
    <div className='sub-dashboard pb-6 mb-5 h-24 w-full flex justify-around items-center font-800 '>
      <div className='flex items-center text-text_grey '>
        <File fill='#344767' className='w-5 h-5 mr-2' />
        <p className='text-xl text-text_black mr-2'>{contractName}</p>
        <p>({String(contractId).padStart(8, '0')})</p>
      </div>

      <div className='flex items-center text-text_grey'>
        <img src={clock} alt='clock' className='w-5 h-5 mr-2' />
        <p className='text-text_black mr-1'>생성일시 : </p>
        <span className=' mr-2'>{formatDateTime(createdDateTime)}</span>
      </div>

      <div className='flex items-center text-text_grey'>
        <img src={clock} alt='clock' className='w-5 h-5 mr-2' />
        <p className='text-text_black mr-1'>변경일시 : </p>
        <span className=' mr-2'>{formatDateTime(modifiedDateTime)}</span>
      </div>
    </div>
  );
};

export default ConDetailHeader;
