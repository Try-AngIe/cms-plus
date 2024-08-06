const MoveButton = ({ imgSrc, color, buttonText, onClick }) => {
  return (
    <div
      className={`flex justify-center items-center rounded-lg border bg-white border-mint mr-1
                ${color === 'mint' ? 'hover:border-mint_hover' : ''} `}>
      <button
        className={`flex items-center rounded-lg py-2 px-4 text-sm border border-white transition-all duration-200
                ${color === 'mint' ? 'bg-mint text-white font-700 hover:bg-mint_hover' : 'font-800 text-mint hover:bg-mint_hover_light'} `}
        onClick={onClick}>
        <img className='mr-2 w-5 h-5' src={imgSrc} alt='button icon' />
        <p>{buttonText}</p>
      </button>
    </div>
  );
};

export default MoveButton;
