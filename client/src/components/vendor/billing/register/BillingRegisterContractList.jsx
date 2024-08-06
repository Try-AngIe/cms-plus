import PagiNation from '@/components/common/PagiNation';
import SelectField from '@/components/common/selects/SelectField';
import InputWeb from '@/components/common/inputs/InputWeb';
import { formatProductsForList } from '@/utils/format/formatProducts';
import { useState } from 'react';

const typeOtions = [
  { value: 'memberName', label: '회원명' },
  { value: 'contractDay', label: '약정일' },
  { value: 'productName', label: '상품명' },
  { value: 'contractPrice', label: '계약금액' },
];

const typePlaceholers = {
  memberName: '회원명 입력',
  contractDay: '약정일 입력',
  contractProducts: '상품명 입력',
  contractPrice: '계약금액 이하',
};

const ContractList = ({
  searchType,
  setSearchType,
  setSearchTerm,
  contractList,
  handleSelectContract,
  currentPage,
  setCurrentPage,
  totalPages,
  pageGroup,
  setPageGroup,
}) => {
  const [selectedContractId, setSelectedContractId] = useState(null);
  const [mSearchTerm, setMSearchTerm] = useState('');

  const onSelected = contract => {
    setSelectedContractId(contract.contractId);
    handleSelectContract(contract);
  };

  const handleSelectChange = event => {
    setMSearchTerm('');
    setSearchTerm('');
    setSearchType(event.target.value);
  };

  const handleInputChange = e => {
    let value = e.target.value;

    if (searchType === 'contractDay' || searchType === 'contractPrice') {
      value = value.replace(/\D/g, '');
    }

    setSearchTerm(value);
    setMSearchTerm(value);
  };

  return (
    <div className='flex flex-col relative w-2/5 pr-5 border-r border-ipt_border'>
      <h2 className='text-text_black text-xl font-800 mb-6'>계약 목록</h2>
      <div className='flex justify-between w-full mb-2'>
        <SelectField
          label='카테고리'
          classContainer='mr-3 w-1/3 h-full'
          classLabel='text-15 text-text_black font-700'
          classSelect='py-3 rounded-lg'
          value={searchType}
          options={typeOtions}
          onChange={handleSelectChange}
        />
        <InputWeb
          label='검색어'
          id='searchTerm'
          type='text'
          classContainer='w-full'
          classInput='text-15 py-3'
          placeholder={typePlaceholers[searchType]}
          value={mSearchTerm}
          onChange={handleInputChange}
        />
      </div>
      <table className='flex flex-col h-full w-full  mb-3'>
        <thead>
          <tr className='flex bg-table_col shadow-column'>
            <th className='w-3/12 py-2 text-center text-text_black font-800 '>회원명</th>
            <th className='w-2/12 py-2 text-center text-text_black font-800 '>약정일</th>
            <th className='w-4/12 py-2 text-center text-text_black font-800 '>상품</th>
            <th className='w-3/12 py-2 text-center text-text_black font-800 '>계약금액</th>
          </tr>
        </thead>
        <tbody className='flex flex-col h-full text-sm '>
          {contractList.map(contract => (
            <tr
              key={contract.contractId}
              onClick={() => onSelected(contract)}
              className={`flex items-center h-[9%] border-b border-ipt_border cursor-pointer  
                ${selectedContractId === contract.contractId ? 'bg-blue-100' : 'hover:bg-ipt_disa'}`}>
              <td className='w-3/12 text-center text-text_black'>{contract.memberName}</td>
              <td className='w-2/12 text-center text-text_black'>{`${contract.contractDay}일`}</td>
              <td className='w-4/12 text-center text-text_black'>
                {formatProductsForList(contract.firstProductName, contract.totalProductCount)}
              </td>
              <td className='w-3/12 text-center text-text_black'>{`${contract.contractPrice.toLocaleString()}원`}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className='absolute bottom-0 w-full '>
        <PagiNation
          currentPage={currentPage}
          setCurrentPage={setCurrentPage}
          totalPages={totalPages}
          pageGroup={pageGroup}
          setPageGroup={setPageGroup}
          buttonCount={5}
        />
      </div>
    </div>
  );
};

export default ContractList;
