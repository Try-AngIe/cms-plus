import AlertContext from '@/utils/dialog/alert/AlertContext';
import ConfirmContext from '@/utils/dialog/confirm/ConfirmContext';
import { useContext, useState } from 'react';

const Test = () => {
  // msg : 원하는 메세지
  // type : 4가지(default, width, success, error), "" = defalut
  // title : alert제목 , "" = 효성 CMS#
  const { alert: alertComp } = useContext(AlertContext);
  const onAlert = async (msg, type, title, err = null) => {
    if (err != null) {
      const data = err.response.data;
      const fieldErrors = data.errors;

      msg = data.messeage;
      if (fieldErrors && fieldErrors.length > 0) {
        msg = fieldErrors.join('\n');
      }
    }

    const result = await alertComp(msg, type, title);
  };

  console.log("infra test v0730-1");

  // type:
  // msg : 원하는 메세지
  // type : 2가지(default, warning), "" = defalut
  // title : alert제목 , "" = 효성 CMS#
  const { confirm: confrimComp } = useContext(ConfirmContext);
  const onConfirm = async (msg, type, title) => {
    const result = await confrimComp(msg, type, title);
    console.log(result);
  };

  const [isChecked, setIsChecked] = useState(false);

  const handleCheckboxChange = () => {
    console.log('???', isChecked);
    setIsChecked(!isChecked);
  };

  return (
    <div>
      <div style={{ display: 'flex', marginBottom: '20px' }}>
        <button
          style={{ border: '1px solid black', height: 60, padding: '10px', marginRight: '5px' }}>
          카드결제버튼
        </button>
        <button
          style={{ border: '1px solid black', height: 60, padding: '10px', marginRight: '5px' }}>
          계좌결제버튼
        </button>
        <button
          style={{ border: '1px solid black', height: 60, padding: '10px', marginRight: '5px' }}>
          가상계좌결제버튼
        </button>
      </div>
      <div style={{ display: 'flex' }}>
        <button
          style={{ border: '1px solid black', height: 60, padding: '10px', marginRight: '5px' }}
          onClick={() => onAlert('회원정보가 수정되었습니다!', 'default')}>
          alert
        </button>
        <button
          style={{ border: '1px solid black', height: 60, padding: '10px', marginRight: '5px' }}
          onClick={() => onAlert('회원정보가 수정되었습니다!', 'width')}>
          alertwide
        </button>
        <button
          style={{ border: '1px solid black', height: 60, padding: '10px', marginRight: '5px' }}
          onClick={() => onAlert('회원정보가 수정되었습니다!', 'success', '회원정보 수정 성공')}>
          alertSuccess
        </button>
        <button
          style={{ border: '1px solid black', height: 60, padding: '10px', marginRight: '5px' }}
          onClick={() => onAlert('회원정보가 수정되었습니다!', 'error', '회원정보 수정 실패')}>
          alertError
        </button>
        <button
          style={{ border: '1px solid black', height: 60, padding: '10px', marginRight: '5px' }}
          onClick={() => onConfirm('회원정보가 수정되었습니다!', 'default')}>
          confirm
        </button>
        <button
          style={{ border: '1px solid black', height: 60, padding: '10px', marginRight: '5px' }}
          onClick={() => onConfirm('회원정보가 수정하시겠습니까?', 'warning', '회원정보 수정')}>
          confirmWarning
        </button>
      </div>

      <div className='flex flex-col items-center justify-center p-12 bg-white rounded-lg'>
        <div className='mb-8'>
          <svg className='w-24 h-24 text-teal-400' fill='currentColor' viewBox='0 0 24 24'>
            <path d='M19 5v14H5V5h14m0-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z' />
          </svg>
        </div>
        <h2 className='text-2xl font-semibold text-gray-800 mb-2'>할 일 목록이 비어있습니다</h2>
        <p className='text-gray-600 text-center mb-8'>
          새로운 할 일을 추가하여 생산적인 하루를 시작하세요.
        </p>
        <button className='px-6 py-3 bg-teal-400 text-white rounded-md hover:bg-teal-500 transition duration-300 focus:outline-none focus:ring-2 focus:ring-teal-500 focus:ring-opacity-50'>
          할 일 추가하기
        </button>
      </div>

      <div className='flex items-center justify-center h-14 bg-gray-100'>
        <div className='flex items-center'>
          <input
            id='custom-checkbox'
            type='checkbox'
            className='hidden peer'
            checked={isChecked}
            onChange={handleCheckboxChange}
          />
          <label htmlFor='custom-checkbox' className='flex items-center cursor-pointer'>
            <span
              className={`w-5 h-5 flex items-center justify-center border border-gray-300 rounded-md bg-white ${isChecked ? 'bg-mint border-mint' : ''}`}>
              {isChecked && (
                <svg
                  className='w-3 h-3 text-white'
                  viewBox='0 0 24 24'
                  fill='none'
                  stroke='currentColor'>
                  <path
                    d='M5 13l4 4L19 7'
                    strokeWidth='2'
                    strokeLinecap='round'
                    strokeLinejoin='round'
                  />
                </svg>
              )}
            </span>
            <span className='ml-2 text-gray-700'>Custom Checkbox</span>
          </label>
        </div>
      </div>

      <label className='flex cursor-pointer gap-2'>
        <svg
          xmlns='http://www.w3.org/2000/svg'
          width='20'
          height='20'
          viewBox='0 0 24 24'
          fill='none'
          stroke='currentColor'
          strokeWidth='2'
          strokeLinecap='round'
          strokeLinejoin='round'>
          <circle cx='12' cy='12' r='5' />
          <path d='M12 1v2M12 21v2M4.2 4.2l1.4 1.4M18.4 18.4l1.4 1.4M1 12h2M21 12h2M4.2 19.8l1.4-1.4M18.4 5.6l1.4-1.4' />
        </svg>
        <input type='checkbox' value='synthwave' className='toggle theme-controller' />
        <svg
          xmlns='http://www.w3.org/2000/svg'
          width='20'
          height='20'
          viewBox='0 0 24 24'
          fill='none'
          stroke='currentColor'
          strokeWidth='2'
          strokeLinecap='round'
          strokeLinejoin='round'>
          <path d='M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z' />
        </svg>
      </label>
    </div>
  );
};

export default Test;
