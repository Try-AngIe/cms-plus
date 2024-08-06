import Checkbox from '../inputs/CheckBox';

const TableRow = ({
  index,
  row,
  cols,
  size = 'h-[8.8%]',
  currentPage,
  selection,
  handleClickCheckBox,
  onRowClick,
  searchConditions,
}) => {
  // 검색어 하이라이트 함수
  const highlightSearchTerm = (text, searchConditions, field) => {
    if ('contractPrice billingPrice productPrice'.includes(field)) {
      return text;
    }

    field = field === 'contractProducts' || field === 'billingProducts' ? 'productName' : field;
    let searchTerm = searchConditions[field];
    if (!searchTerm) return text;

    let regex;
    if (field === 'memberPhone') {
      // 휴대폰 번호 검색을 위한 정규식
      const escapedSearchTerm = searchTerm.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
      const pattern = escapedSearchTerm.split('').join('[-]?');
      regex = new RegExp(`(${pattern})`, 'gi');
    } else if (field === 'productName') {
      text = text.split('+')[0];
      regex = new RegExp(`(${searchTerm.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');
    } else {
      regex = new RegExp(`(${searchTerm.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');
    }

    const parts = text.toString().split(regex);
    return parts.map((part, index) =>
      regex.test(part) ? (
        <span key={index} className='bg-red-200 text-red-800'>
          {part}
        </span>
      ) : (
        part
      )
    );
  };

  return (
    <tr
      className={`${size} text-text_black hover:bg-ipt_disa cursor-pointer flex items-center border-b border-ipt_border 
        ${selection.includes(row) && 'bg'}`}
      onClick={() => onRowClick(row)}>
      <td className='w-16 flex justify-center'>
        <Checkbox
          classBox='h-4 w-4 rounded-sm '
          checked={selection.includes(row)}
          onChange={() => handleClickCheckBox(row)}
          onClick={e => e.stopPropagation()}
        />
      </td>

      {cols.map((col, idx) => (
        <td
          key={idx}
          className={` ${col.key === 'order' ? 'w-16' : `${col.width}`} 
                    text-sm font-700 flex justify-center`}>
          {col.key !== 'order'
            ? highlightSearchTerm(row[col.key], searchConditions, col.key)
            : index + 1 + (currentPage - 1) * 10}
        </td>
      ))}
    </tr>
  );
};

export default TableRow;
