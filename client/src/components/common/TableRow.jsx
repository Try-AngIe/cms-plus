const TableRow = ({ item, cols, itemKey, selection, handleClickCheckBox, onRowClick }) => (
  <tr
    className={`text-text_black hover:bg-gray-200 cursor-pointer
      ${selection.includes(item[itemKey]) && 'bg-gray-200'}
    `}
    onClick={() => onRowClick(item)} // onRowClick 이벤트 추가
  >
    <td className='p-4'>
      <input
        type='checkbox'
        checked={selection.includes(item[itemKey])}
        onChange={() => handleClickCheckBox(item[itemKey])}
        onClick={e => e.stopPropagation()} // 체크박스 클릭할 때 row 클릭되는거 막음
      />
    </td>

    {cols.map(col => (
      <td key={col} className='p-4'>
        {item[col]}
      </td>
    ))}
  </tr>
);

export default TableRow;
