import searchIcon from '@/assets/search.svg';

const TableSearch = ({ search, handleSearchChange, show, onSearchClick }) => {
  return (
    <tr className='text-sm'>
      {search.map((searchItem, idx) => (
        <th key={idx} className='text-left py-3'>
          {searchItem.type === 'select' ? (
            <select
              className='px-3 py-1  border border-text_grey rounded-md'
              value={searchItem.value}
              onChange={e => {
                handleSearchChange(searchItem.key, e.target.value);
                show();
              }}>
              <option value=''>선택</option>
              {searchItem.options &&
                searchItem.options.map((option, idx) => (
                  <option key={idx} value={option}>
                    {option}
                  </option>
                ))}
            </select>
          ) : (
            <div className='relative w-28 '>
              <input
                className='px-3 py-1 w-full border border-text_grey rounded-md pr-8'
                type={searchItem.type}
                placeholder='입력'
                value={searchItem.value}
                onChange={e => handleSearchChange(searchItem.key, e.target.value)}
                onKeyDown={e => e.key === 'Enter' && show()}
              />
              {idx > 1 && ( // 2번 컬럼부터 돋보기 버튼 보이게
                <button
                  className='absolute right-2 top-1/2 transform -translate-y-1/2'
                  onClick={onSearchClick}>
                  <img src={searchIcon} alt='search' className='w-4 h-4' />
                </button>
              )}
            </div>
          )}
        </th>
      ))}
    </tr>
  );
};

export default TableSearch;
