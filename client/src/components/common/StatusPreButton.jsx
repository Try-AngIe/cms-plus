const PreviousButton = ({ onClick, status, start, type = '', end }) => {
  if (status === start || status === end) {
    return null;
  }

  const width = type === 'memberRegister' ? 'w-28' : 'w-1/3';

  return (
    <button className={`mr-3 rounded-lg border border-mint text-mint ${width}`} onClick={onClick}>
      이전
    </button>
  );
};

export default PreviousButton;
