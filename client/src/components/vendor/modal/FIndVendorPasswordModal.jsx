import { postFindPassword } from '@/apis/auth';
import BaseModal from '@/components/common/BaseModal';
import InputWeb from '@/components/common/inputs/InputWeb';
import RadioGroup from '@/components/common/inputs/RadioGroup';
import { formatPhone, removeDashes } from '@/utils/format/formatPhone';
import { validateField } from '@/utils/validators';
import { useEffect, useState } from 'react';

const MessageSendMethod = [
  { label: '전화번호 인증', value: 'SMS' },
  { label: '이메일 인증', value: 'EMAIL' },
];

const FindVendoPasswordModal = ({
  isShowModal,
  setIsShowModal,
  setIsShowResetPasswordModal,
  axiosRequestAuthenticationNumber,
  setFindedId,
  time,
  setTime,
  icon,
  modalTitle,
}) => {
  const [selectedSendMethod, setSelectedSendMethod] = useState('SMS');
  const [vendorPasswordFormData, setVendorPasswordFormData] = useState({
    method: 'SMS',
    username: '',
    phone: '',
    authenticationNumber: '',
  });
  const [isAuthentication, setIsAuthentication] = useState(true);

  // <---- 사용자 입력값 ---->
  const handleChangeValue = e => {
    const { id, value } = e.target;
    if (id === 'phone') {
      setVendorPasswordFormData(prev => ({
        ...prev,
        [id]: removeDashes(value == '' ? '' : value),
      }));
    } else {
      setVendorPasswordFormData(prev => ({ ...prev, [id]: value == '' ? '' : value }));
    }
    if (id === 'username') {
      setFindedId(value);
    }
  };

  // <---- 공백입력 막기 ---->
  const handleKeyDown = e => {
    e.key === ' ' && e.preventDefault();
  };

  // <---- 메세지 전송 방법 서택 ---->
  const handleChangeMessageSendMethod = value => {
    setSelectedSendMethod(value);
  };

  // <---- method에 따른 입력폼 변경 ---->
  const settingvendorPasswordFormData = () => {
    if (selectedSendMethod === 'SMS') {
      setVendorPasswordFormData({
        method: 'SMS',
        username: '',
        phone: '',
        authenticationNumber: '',
      });
    } else {
      [];
      setVendorPasswordFormData({
        method: 'EMAIL',
        username: '',
        email: '',
        authenticationNumber: '',
      });
    }
  };

  // <---- 비밀번호 찾기 API ---->
  const axiosFindPassword = async () => {
    try {
      const res = await postFindPassword(vendorPasswordFormData);
      console.log('!----비밀번호 찾기 요청 성공----!'); // 삭제예정
      if (res.data) {
        setIsShowModal(false);
        setIsShowResetPasswordModal(true);
      } else {
        setIsAuthentication(false);
      }
    } catch (err) {
      // 아이디가 없을 시 예외 처리
      setIsAuthentication(false);
      console.error('axiosFindPassword => ', err.response);
    }
  };

  // <----- SMS 폼의 유효성 검사 ----->
  const isValidateSmsForm = () => {
    const isValiduserName = validateField('name', vendorPasswordFormData.name);
    const isValidPhone = validateField('phone', vendorPasswordFormData.phone);
    const authenticationNumber = vendorPasswordFormData.authenticationNumber;
    return isValiduserName && isValidPhone && authenticationNumber;
  };

  // <----- 이메일 폼의 유효성 검사 ----->
  const isValidateEmailForm = () => {
    const isValiduserName = validateField('username', vendorPasswordFormData.username);
    const isValidEmail = validateField('email', vendorPasswordFormData.email);
    const authenticationNumber = vendorPasswordFormData.authenticationNumber;
    return isValiduserName && isValidEmail && authenticationNumber;
  };

  useEffect(() => {
    setSelectedSendMethod('SMS');
  }, [isShowModal]);

  useEffect(() => {
    settingvendorPasswordFormData();
    setTime(0);
  }, [isShowModal, selectedSendMethod]);

  return (
    <BaseModal
      isShowModal={isShowModal}
      setIsShowModal={setIsShowModal}
      modalTitle={modalTitle}
      icon={icon}
      height={'h-540'}
      width={'w-640'}>
      <div className='w-full h-full flex flex-col justify-between'>
        <RadioGroup
          name='MessageSendMethod'
          options={MessageSendMethod}
          selectedOption={selectedSendMethod}
          onChange={handleChangeMessageSendMethod}
          classContainer='ml-2'
          classRadio='text-15 font-400'
        />
        <InputWeb
          id='username'
          label='아이디'
          type='text'
          placeholder='이름을 입력해 주세요.'
          value={vendorPasswordFormData.username}
          classContainer='mb-6'
          classInput='py-4 rounded-lg'
          onChange={handleChangeValue}
          onKeyDown={handleKeyDown}
          maxLength={20}
          isValid={validateField('username', vendorPasswordFormData.username)}
          errorMsg='올바른 형식 아닙니다.'
        />
        <div className='mb-6 flex items-end'>
          {selectedSendMethod === 'SMS' ? (
            <InputWeb
              id='phone'
              label='휴대전화번호'
              type='text'
              placeholder='휴대전화번호를 입력해 주세요.'
              value={formatPhone(vendorPasswordFormData.phone)}
              onChange={handleChangeValue}
              onKeyDown={handleKeyDown}
              classContainer='w-9/12'
              classInput='rounded-lg'
              isValid={validateField('phone', vendorPasswordFormData.phone)}
              errorMsg='올바른 형식 아닙니다.'
              time={time}
              setTime={setTime}
              maxLength={13}
            />
          ) : (
            <InputWeb
              id='email'
              label='이메일'
              type='text'
              placeholder='이메일을 입력해 주세요.'
              value={vendorPasswordFormData.email ? vendorPasswordFormData.email : ''}
              onChange={handleChangeValue}
              onKeyDown={handleKeyDown}
              classContainer='w-9/12'
              classInput='rounded-lg'
              isValid={validateField('email', vendorPasswordFormData.email)}
              errorMsg='올바른 형식 아닙니다.'
              time={time}
              setTime={setTime}
              maxLength={40}
            />
          )}
          <button
            className={`ml-3 w-3/12 disabled: text-white text-base font-700 h-54 rounded-lg 
              ${
                (vendorPasswordFormData.phone || vendorPasswordFormData.email) &&
                (vendorPasswordFormData.phone?.length > 0 ||
                  vendorPasswordFormData.email?.length > 0)
                  ? 'bg-mint hover:bg-mint_hover transition-all duration-200'
                  : 'bg-btn_disa'
              }`}
            disabled={!vendorPasswordFormData.phone && !vendorPasswordFormData.email}
            onClick={() => axiosRequestAuthenticationNumber(vendorPasswordFormData)}>
            {time && time !== 0 ? '인증번호 재전송' : '인증번호 받기'}
          </button>
        </div>
        <InputWeb
          id='authenticationNumber'
          label='인증번호'
          type='text'
          placeholder='인증번호 6자리를 입력해 주세요.'
          value={vendorPasswordFormData.authenticationNumber}
          classInput='mb-8 rounded-lg'
          onChange={handleChangeValue}
          onKeyDown={handleKeyDown}
          isValid={isAuthentication}
          errorMsg='인증번호가 틀렸습니다.'
          maxLength={6}
        />
        <button
          disabled={
            vendorPasswordFormData.method === 'SMS' ? !isValidateSmsForm() : isValidateEmailForm()
          }
          className={`font-700 px-4 py-3 text-white rounded-lg transition-all duration-200 ${
            (vendorPasswordFormData.method === 'SMS' && isValidateSmsForm()) ||
            (vendorPasswordFormData.method === 'EMAIL' && isValidateEmailForm())
              ? 'hover:bg-mint_hover bg-mint curson-pointer'
              : 'bg-btn_disa'
          }`}
          onClick={axiosFindPassword}>
          비밀번호 찾기
        </button>
      </div>
    </BaseModal>
  );
};

export default FindVendoPasswordModal;
