import { postFindIdentifier } from '@/apis/auth';
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

const FindVendorIdModal = ({
  isShowModal,
  setIsShowModal,
  setFindedId,
  setIsShowSuccessFindIdModal,
  axiosRequestAuthenticationNumber,
  time,
  setTime,
  icon,
  modalTitle,
}) => {
  const [selectedSendMethod, setSelectedSendMethod] = useState('SMS');
  const [vendorIdFormData, setVendorIdFormData] = useState({
    method: 'SMS',
    name: '',
    phone: '',
    authenticationNumber: '',
  });
  const [isAuthentication, setIsAuthentication] = useState(true);

  // <---- 사용자 입력값 ---->
  const handleChangeValue = e => {
    const { id, value } = e.target;
    if (id === 'phone') {
      setVendorIdFormData(prev => ({ ...prev, [id]: removeDashes(value == '' ? '' : value) }));
    } else {
      setVendorIdFormData(prev => ({ ...prev, [id]: value == '' ? '' : value }));
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
  const settingvendorIdFormData = () => {
    if (selectedSendMethod === 'SMS') {
      setVendorIdFormData({
        method: 'SMS',
        name: '',
        phone: '',
        authenticationNumber: '',
      });
    } else {
      setVendorIdFormData({ method: 'EMAIL', name: '', email: '', authenticationNumber: '' });
    }
  };

  // <---- 아이디 찾기 API ---->
  const axiosFindIdentifier = async () => {
    try {
      const res = await postFindIdentifier(vendorIdFormData);
      console.log('!----아이디 찾기 요청 성공----!'); // 삭제예정
      if (res.data) {
        await setFindedId(res.data.username);
        setIsShowSuccessFindIdModal(true);
        setIsShowModal(false);
      } else {
        setIsAuthentication(false);
      }
    } catch (err) {
      setIsAuthentication(false);
      console.error('axiosFindIdentifier => ', err.response);
    }
  };

  // <----- SMS 폼의 유효성 검사 ----->
  const isValidateSmsForm = () => {
    const isValidName = validateField('name', vendorIdFormData.name);
    const isValidPhone = validateField('phone', vendorIdFormData.phone);
    const authenticationNumber = vendorIdFormData.authenticationNumber;
    return isValidName && isValidPhone && authenticationNumber;
  };

  // <----- 이메일 폼의 유효성 검사 ----->
  const isValidateEmailForm = () => {
    const isValidName = validateField('name', vendorIdFormData.name);
    const isValidEmail = validateField('email', vendorIdFormData.email);
    const authenticationNumber = vendorIdFormData.authenticationNumber;
    return isValidName && isValidEmail && authenticationNumber;
  };

  useEffect(() => {
    setSelectedSendMethod('SMS');
  }, [isShowModal]);

  useEffect(() => {
    settingvendorIdFormData();
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
          id='name'
          label='이름'
          type='text'
          placeholder='이름을 입력해 주세요.'
          value={vendorIdFormData.name}
          classContainer='mb-6'
          classInput='py-4 rounded-lg'
          onChange={handleChangeValue}
          onKeyDown={handleKeyDown}
          maxLength={40}
          isValid={validateField('name', vendorIdFormData.name)}
          errorMsg='올바른 형식 아닙니다.'
        />
        <div className='mb-6 flex items-end'>
          {selectedSendMethod === 'SMS' ? (
            <InputWeb
              id='phone'
              label='휴대전화번호'
              type='text'
              placeholder='휴대전화번호를 입력해 주세요.'
              value={formatPhone(vendorIdFormData.phone)}
              onChange={handleChangeValue}
              onKeyDown={handleKeyDown}
              classContainer='w-9/12'
              classInput='rounded-lg'
              isValid={validateField('phone', vendorIdFormData.phone)}
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
              value={vendorIdFormData.email ? vendorIdFormData.email : ''}
              onChange={handleChangeValue}
              onKeyDown={handleKeyDown}
              classContainer='w-9/12'
              classInput='rounded-lg'
              isValid={validateField('email', vendorIdFormData.email)}
              errorMsg='올바른 형식 아닙니다.'
              time={time}
              setTime={setTime}
              maxLength={40}
            />
          )}
          <button
            tabIndex='-1'
            className={`ml-3 w-3/12 disabled: text-white text-base font-700 h-54 rounded-lg focus:outline-none
              ${
                (vendorIdFormData.phone || vendorIdFormData.email) &&
                (vendorIdFormData.phone?.length > 0 || vendorIdFormData.email?.length > 0)
                  ? 'bg-mint hover:bg-mint_hover transition-all duration-200'
                  : 'bg-btn_disa'
              }`}
            disabled={!vendorIdFormData.phone && !vendorIdFormData.email}
            onClick={() => axiosRequestAuthenticationNumber(vendorIdFormData)}>
            {time && time !== 0 ? '인증번호 재전송' : '인증번호 받기'}
          </button>
        </div>
        <InputWeb
          id='authenticationNumber'
          label='인증번호'
          type='text'
          placeholder='인증번호 6자리를 입력해 주세요.'
          value={vendorIdFormData.authenticationNumber}
          classContainer='mb-6'
          classInput='py-4 rounded-lg'
          onChange={handleChangeValue}
          onKeyDown={handleKeyDown}
          isValid={isAuthentication}
          errorMsg='인증번호가 틀렸습니다.'
          maxLength={6}
        />
        <button
          disabled={
            vendorIdFormData.method === 'SMS' ? !isValidateSmsForm() : isValidateEmailForm()
          }
          className={`font-700 px-4 py-3 text-white rounded-lg transition-all duration-200 ${
            (vendorIdFormData.method === 'SMS' && isValidateSmsForm()) ||
            (vendorIdFormData.method === 'EMAIL' && isValidateEmailForm())
              ? 'hover:bg-mint_hover bg-mint curson-pointer'
              : 'bg-btn_disa'
          }`}
          onClick={axiosFindIdentifier}>
          아이디 찾기
        </button>
      </div>
    </BaseModal>
  );
};

export default FindVendorIdModal;
