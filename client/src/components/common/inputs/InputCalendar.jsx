import { useEffect, useState } from 'react';
import { ConfigProvider, DatePicker, Space } from 'antd';
import koKR from 'antd/es/locale/ko_KR';
import dayjs from 'dayjs';
import 'dayjs/locale/ko';

const InputCalendar = ({
  id,
  label,
  type,
  required,
  disabled,
  readOnly,
  placeholder,
  classContainer = '',
  classLabel = '',
  height = '45px',
  width = '290px',
  handleChangeValue,
  value = '',
  ...props
}) => {
  const [selectedDate, setSelectedDate] = useState('');

  // dayjs 로케일 설정
  dayjs.locale('ko');

  const onChange = (date, dateString) => {
    setSelectedDate(date);
    if (handleChangeValue) {
      handleChangeValue({ target: { id, value: dateString } });
    }
  };

  const datePickerCustomTheme = {
    token: {
      colorPrimary: '#4FD1C5', // 선택된 날짜의 색상을 초록색으로 변경
    },
  };

  const customStyles = {
    datePicker: {
      width: width,
      height: height,
      fontSize: `${type === 'search' ? '10px' : '14px'}`,
      color: '#344767',
    },
  };

  const customCSS = `
    .ant-picker {
      border-color: ${type === 'search' ? '#344767' : '#C7CCD0'} !important; 
      border-radius:  ${type === 'search' ? '6px' : '8px'} !important; 
    }

    .ant-picker-input > input {
      pointer-events: none;
      padding-left: 5px !important; 
      font-size: ${type === 'search' ? '13px' : '14px'} !important; 
      color: ${disabled ? '#000000' : 'inherit'} !important; // Text color change
    }

    .ant-picker-input > input::placeholder {
      color: #7B809A !important; 
      font-size: 13px;
    }

    .ant-picker-focused {
      border-color: ${type === 'search' ? '0 0 0 1px #344767' : '0 0 0 1px #4FD1C5'} !important; 
      outline: none !important; 
      box-shadow: ${type === 'search' ? '0 0 0 1px #344767' : '0 0 0 1px #4FD1C5'}  !important;
    }

    .ant-picker-cell-selected .ant-picker-cell-inner,
    .ant-picker-cell-inner:hover {
      background-color: #4FD1C5 !important; 
      color: #ffffff !important; 
      border: none !important; 
      font-weight: 700 !important; 
      border-radius: 6px !important;
    }

    .ant-picker-cell-inner {
      font-weight: 400;
      background-color: transparent !important;
    }

    .ant-picker-footer .ant-picker-today-btn:hover {
      color: #4FD1C5 !important;
    }

    .ant-picker-suffix {
      font-size: 14px; /* Increase icon size */
    }

    .ant-picker-clear {
      font-size: ${type === 'search' ? '14px' : '18px'} !important; /* 삭제 아이콘 크기 */
      color: ${type === 'search' ? '#344767' : '#C7CCD0'}  !important; /* 삭제 아이콘 색상 */
    }

    .anticon.anticon-calendar {
      font-size: ${type === 'search' ? '14px' : '18px'} !important; /* 캘린더 아이콘 크기 */
      color: ${type === 'search' ? '#344767' : '#C7CCD0'} !important; /* 캘린더 아이콘 색상 */
    }
  `;

  useEffect(() => {
    if (value && dayjs(value).isValid()) {
      setSelectedDate(dayjs(value));
    } else {
      setSelectedDate(null);
    }
  }, [value]);

  return (
    <div className={`${classContainer}`}>
      {label && (
        <label
          className={`${classLabel} block text-text_black text-15 font-700 mb-2 ml-2 
                    ${required ? "after:ml-1 after:text-red-500 after:content-['*']" : ''}`}
          htmlFor={id}>
          {label}
        </label>
      )}
      <div className='relative '>
        <ConfigProvider locale={koKR} theme={datePickerCustomTheme}>
          <div className='cursor-pointer'>
            <style>{customCSS}</style>
            <Space direction='vertical' style={{ width: '100%' }}>
              <DatePicker
                id={id}
                value={selectedDate}
                onChange={onChange}
                format={{
                  format: 'YYYY-MM-DD',
                  type: 'mask',
                }}
                disabled={disabled}
                readOnly={readOnly}
                inputReadOnly={true}
                style={customStyles.datePicker}
                placeholder={placeholder}
                {...props}
              />
            </Space>
          </div>
        </ConfigProvider>
      </div>
    </div>
  );
};

export default InputCalendar;
