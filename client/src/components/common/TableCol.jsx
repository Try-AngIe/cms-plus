const TableCol = ({ cols, isSelectedAll, handleClickCheckBoxAll }) => (
  <thead>
    <tr className='bg-ipt_disa text-text_black text-base shadow-[0_1px_2px_rgba(0,0,0,0.19)]'>
      <th className='text-left px-4 py-3'>
        <input type='checkbox' checked={isSelectedAll()} onChange={handleClickCheckBoxAll} />
      </th>
      {cols.map((col, idx) => (
        <th key={idx} className='text-left p-2 '>
          {col}
        </th>
      ))}
    </tr>
  </thead>
);

export default TableCol;
