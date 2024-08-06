const SortSelect = ({
  setCurrentOrder,
  setCurrentOrderBy,
  selectOptions,
  axiosList,
  currentSearchParams,
}) => {
  const handleChangeOption = async event => {
    const selectedOption = selectOptions.find(option => option.label === event.target.value);
    console.log(event, ' : '.selectedOption);
    if (event.target.value === 'No') {
      setCurrentOrder('');
      setCurrentOrderBy('');
      await axiosList(currentSearchParams, '', '');
    } else if (selectedOption) {
      setCurrentOrder(selectedOption.order);
      setCurrentOrderBy(selectedOption.orderBy);
      await axiosList(currentSearchParams, selectedOption.order, selectedOption.orderBy);
    }
  };

  return (
    <div className='relative flex items-center'>
      <div className='relative mr-3'>
        <select
          onChange={handleChangeOption}
          className='block appearance-none text-xs text-text_grey border border-text_grey 
                      rounded-md px-4 py-2 pr-10 cursor-pointer focus:outline-none'>
          <option value='No'>최신순</option>
          {selectOptions.map((option, index) => (
            <option key={index} value={option.label}>
              {option.label}
            </option>
          ))}
        </select>
        <div className='pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-text_grey'>
          <svg
            className='fill-text_grey h-6 w-6'
            xmlns='http://www.w3.org/2000/svg'
            viewBox='0 0 20 20'>
            <path d='M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z' />
          </svg>
        </div>
      </div>
    </div>
  );
};

export default SortSelect;
