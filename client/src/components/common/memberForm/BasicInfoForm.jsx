import InputWeb from '@/components/common/inputs/InputWeb';
import TextArea from '../inputs/TextArea';
import { formatPhone, removeDashes } from '@/utils/format/formatPhone';
import { useMemberBasicStore } from '@/stores/useMemberBasicStore';
import InputCalendar from '@/components/common/inputs/InputCalendar';
import { validateField } from '@/utils/validators';
import { disableFutureDates } from '@/utils/format/formatCalender';
import { useLocation } from 'react-router-dom';
import { getMemberCheck } from '@/apis/member';

// formType : CREATE, UPDATE, DETAIL
const BasicInfoForm = ({ formType, memberData }) => {
  const { basicInfo, setBasicInfoItem, setAddressInfoItem } = useMemberBasicStore();
  const location = useLocation();
  const { state } = location;
  const type = state?.type;

  // <------ 인풋 필드 입력값 변경 ------>
  const handleChangeValue = e => {
    const { id, value } = e.target;

    if (id === 'memberPhone' || id === 'memberHomePhone') {
      setBasicInfoItem({
        [id]: removeDashes(value === '' ? '' : value),
      });
    } else {
      setBasicInfoItem({
        [id]: value === '' ? '' : value,
      });
    }
  };

  // <------ 주소 필드 입력값 변경 ------>
  const handleChangeAddress = (id, value) => {
    setAddressInfoItem({
      [id]: value,
    });
  };

  // <------ 공백입력 불가 ------>
  const handleKeyDown = e => {
    e.key === ' ' && e.preventDefault();
  };

  // <------ 데이터 소스 선택 ------>
  const dataSource = formType === 'DETAIL' ? memberData : basicInfo;

  // <------ formType : DETAIL일 경우 입력창 비활성화 ------>
  // <------ 기존 회원 계약일 경우 입력창 비활성화 ------>
  let isDisabled = false;
  if (formType === 'DETAIL' || (formType === 'CREATE' && type === 'old')) {
    isDisabled = true;
  }

  return (
    <div className='flex flex-col pt-5 px-2 desktop:flex-row desktop:h-[calc(100%-100px)] extra_desktop:h-[520px]'>
      <div className='flex flex-col justify-between flex-1 '>
        <InputWeb
          id='memberName'
          label='회원명'
          placeholder='회원명(한글, 영문 대소문자 1~40)'
          type='text'
          required
          classInput='py-[14px]'
          value={dataSource.memberName}
          disabled={isDisabled}
          onChange={handleChangeValue}
          onKeyDown={handleKeyDown}
          maxLength={40}
          isValid={validateField('name', dataSource.memberName)}
          errorMsg='올바른 형식 아닙니다.'
        />
        <InputWeb
          id='memberPhone'
          label='휴대전화'
          placeholder='숫자만 입력해주세요.'
          classInput='py-[14px]'
          type='text'
          value={formatPhone(dataSource.memberPhone)}
          required
          disabled={isDisabled}
          onChange={handleChangeValue}
          onKeyDown={handleKeyDown}
          maxLength={13}
          isValid={validateField('phone', dataSource.memberPhone)}
          errorMsg='올바른 형식 아닙니다.'
        />
        <InputCalendar
          id='memberEnrollDate'
          label='가입일'
          placeholder='ex) 2024-11-02'
          classInput='py-[14px]'
          required
          height='47px'
          width='100%'
          classContainer='w-full'
          disabled={isDisabled}
          disabledDate={disableFutureDates}
          value={dataSource.memberEnrollDate}
          handleChangeValue={handleChangeValue}
        />
        <InputWeb
          id='memberHomePhone'
          label='유선전화'
          placeholder='숫자만 입력해주세요.'
          classInput='py-[14px]'
          type='text'
          value={dataSource.memberHomePhone !== null ? formatPhone(dataSource.memberHomePhone) : ''}
          disabled={isDisabled}
          onChange={handleChangeValue}
          onKeyDown={handleKeyDown}
          maxLength={12}
          isValid={validateField('homePhone', dataSource.memberHomePhone)}
          errorMsg='올바른 형식 아닙니다.'
        />
        <InputWeb
          id='memberEmail'
          label='이메일'
          placeholder='ex) example@gmail.com'
          classInput='py-[14px]'
          type='text'
          value={dataSource.memberEmail}
          required
          disabled={isDisabled}
          onChange={handleChangeValue}
          onKeyDown={handleKeyDown}
          maxLength={40}
          isValid={validateField('email', dataSource.memberEmail)}
          errorMsg='올바른 형식 아닙니다.'
        />
      </div>
      {/* TODO */}
      {/* 주소 입력 받기 */}
      <div className='flex-1 desktop:ml-10'>
        <div className='flex items-end mb-5'>
          <InputWeb
            id='zipcode'
            label='주소'
            placeholder='우편번호'
            classInput='py-[14px]'
            type='address'
            value={
              dataSource.memberAddress.zipcode !== null ? dataSource.memberAddress.zipcode : ''
            }
            classContainer='mr-5'
            disabled={isDisabled}
            readOnly
            handleChangeAddress={handleChangeAddress}
          />
          <InputWeb
            id='address'
            placeholder='주소'
            classInput='py-[14px]'
            type='text'
            value={
              dataSource.memberAddress.address !== null ? dataSource.memberAddress.address : ''
            }
            disabled={isDisabled}
            classContainer='w-full'
            readOnly
            onChange={handleChangeAddress}
          />
        </div>
        <InputWeb
          id='addressDetail'
          label='상세 주소'
          placeholder='상세 주소'
          classInput='py-[14px]'
          type='text'
          value={
            dataSource.memberAddress.addressDetail !== null
              ? dataSource.memberAddress.addressDetail
              : ''
          }
          disabled={isDisabled}
          classContainer='mb-3'
          onChange={e => handleChangeAddress(e.target.id, e.target.value)}
          maxLength={100}
        />
        <TextArea
          id='memberMemo'
          label='메모'
          disabled={isDisabled}
          classTextarea='h-52'
          value={dataSource.memberMemo !== null ? dataSource.memberMemo : ''}
          onChange={handleChangeValue}
          maxLength={2000}
        />
      </div>
    </div>
  );
};

export default BasicInfoForm;
