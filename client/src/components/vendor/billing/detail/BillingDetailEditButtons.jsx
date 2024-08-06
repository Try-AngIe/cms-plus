import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes, faSave } from '@fortawesome/free-solid-svg-icons';

const BillingDetailEditButtons = ({ onSave, onCancel }) => {
  const buttonClass = (isCancel = false) => `
    flex justify-between items-center px-4 py-2 ml-4 
    font-700 rounded-md border cursor-pointer transition-all duration-200
    ${
      isCancel
        ? 'text-red-500 border-red-500 hover:bg-red-50'
        : 'text-white bg-mint hover:bg-mint_hover'
    }
  `;

  return (
    <>
      <button className={buttonClass(true)} onClick={onCancel}>
        <FontAwesomeIcon icon={faTimes} className='mr-2 ' />
        <p>취소</p>
      </button>
      <button className={buttonClass()} onClick={onSave}>
        <FontAwesomeIcon icon={faSave} className='mr-2' />
        <p>저장</p>
      </button>
    </>
  );
};

export default BillingDetailEditButtons;
