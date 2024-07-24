import AlertContext from '@/utils/dialog/alert/AlertContext';
import AlertWdithContext from '@/utils/dialog/alertwidth/AlertWidthContext';
import ConfirmContext from '@/utils/dialog/confirm/ConfirmContext';
import { useContext } from 'react';

const Test = () => {
  // 결제
  const { alert: alertComp } = useContext(AlertContext);
  const onAlertClick = async () => {
    const result = await alertComp('회원정보가 수정되었습니다!');
  };

  const { alertWidth: alertWidthComp } = useContext(AlertWdithContext);
  const onAlertWidthClick = async () => {
    const result = await alertWidthComp('회원정보가 수정되었습니다!');
  };

  const { confirm: confrimComp } = useContext(ConfirmContext);
  const confirmClick = async () => {
    const result = await confrimComp('회원정보가 수정되었습니다!');
  };

  console.log("221112323111112");

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
          onClick={onAlertClick}>
          alert
        </button>
        <button
          style={{ border: '1px solid black', height: 60, padding: '10px', marginRight: '5px' }}
          onClick={onAlertWidthClick}>
          alertwide
        </button>
        <button
          style={{ border: '1px solid black', height: 60, padding: '10px', marginRight: '5px' }}
          onClick={confirmClick}>
          confirm
        </button>
      </div>
    </div>
  );
};

export default Test;
