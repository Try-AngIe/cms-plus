import InputWeb from '@/components/common/inputs/InputWeb';
import { useMemberPaymentStore } from '@/stores/useMemberPaymentStore';
import {
  formatCardMonthForDisplay,
  formatCardNumber,
  formatCardYearForDisplay,
  unformatCardNumber,
} from '@/utils/format/formatCard';
import InputCalendar from '@/components/common/inputs/InputCalendar';
import FileUpload from '../../inputs/FileUpload';
import { verifyCard } from '@/apis/validation';
import { validateField } from '@/utils/validators';
import useAlert from '@/hooks/useAlert';
import { useState } from 'react';
import { uploadConsentFile } from '@/apis/signature';


const CardMethodForm = ({ paymentMethod, formType }) => {
  const [uploadStatus, setUploadStatus] = useState('');

  const {
    paymentMethodInfoReq_Card,
    setPaymentMethodInfoReq_Card,
    paymentTypeInfoReq_Auto,
    setPaymentTypeInfoReq_Auto,
  } = useMemberPaymentStore();

  const onAlert = useAlert();

  // <------ 인풋 필드 입력값 변경 ------>
  const handleChangeInput = e => {
    const { id, value } = e.target;
    if (id === 'cardNumber') {
      const unformattedNumber = unformatCardNumber(value);
      setPaymentMethodInfoReq_Card({ [id]: unformattedNumber });
    } else if (id === 'cardYear') {
      setPaymentMethodInfoReq_Card({ [id]: formatCardYearForDisplay(value) });
    } else if (id === 'cardMonth') {
      setPaymentMethodInfoReq_Card({ [id]: formatCardMonthForDisplay(value) });
    } else {
      setPaymentMethodInfoReq_Card({ [id]: value });
    }
  };

   // <------ 파일 업로드 ------>
   const handleUploadFile = async file => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      setUploadStatus('업로드 중...');
      const fileUrl = await uploadConsentFile(formData);
      
      if (fileUrl) {
        const currentTime = new Date().toISOString();
        setPaymentTypeInfoReq_Auto({
          consentImgUrl: fileUrl,
          consetImgName: file.name,
          simpleConsentReqDateTime: currentTime,
        });
        onAlert({
          msg: '동의서 파일이 성공적으로 업로드되었습니다.',
          type: 'success',
          title: '파일 업로드 성공',
        });
      }
    } catch (error) {
      console.error('Error uploading consent file:', error);
      onAlert({
        msg: '동의서 파일 업로드 중 오류가 발생했습니다.',
        type: 'error',
        title: '파일 업로드 실패',
      });
    } finally {
      setUploadStatus('');
    }
  };
  // <----- Card 인증 API ----->
  const axiosVerifyCard = async () => {
    try {
      const { cardMonth, cardYear, ...paymentCardinfo } = paymentMethodInfoReq_Card;
      const transformPaymentCard = {
        paymentMethod: paymentMethod,
        ...paymentCardinfo,
      };
      const res = await verifyCard(transformPaymentCard);
      console.log('!---- Card 인증 ----!'); // 삭제예정

      if (res) {
        onAlert({
          msg: '카드인증에 성공하셨습니다!',
          type: 'success',
          title: '카드 인증 성공',
        });
      } else {
        onAlert({
          msg: '카드인증에 실패하셨습니다.',
          type: 'error',
          title: '카드 인증 실패',
        });
      }
    } catch (err) {
      onAlert({
        msg: '카드인증에 실패하셨습니다.',
        type: 'error',
        title: '카드 인증 오류',
      });
      console.error('axiosVerifyCard => ', err.response);
    }
  };

  return (
    <>
      <div className='flex mt-6 mb-6'>
        <div className='flex w-full'>
          <InputWeb
            id='cardNumber'
            label='카드번호'
            placeholder='최대 14자리'
            type='text'
            required
            classContainer='w-[30%]'
            classInput='py-3 pr-20'
            value={formatCardNumber(paymentMethodInfoReq_Card.cardNumber)}
            onChange={handleChangeInput}
            maxLength={19}
            isValid={validateField(
              'cardNumber',
              unformatCardNumber(paymentMethodInfoReq_Card.cardNumber)
            )}
            errorMsg='올바른 형식 아닙니다.'
          />
          <div className='flex items-end w-[20%] mx-5'>
            <InputWeb
              id='cardMonth'
              label='유효기간'
              placeholder='월'
              type='text'
              required
              classInput='py-3'
              value={paymentMethodInfoReq_Card.cardMonth}
              onChange={handleChangeInput}
              isValid={validateField('cardMonth', paymentMethodInfoReq_Card.cardMonth)}
              errorMsg='1월 ~ 12월'
            />
            <span className='text-text_black mx-2 mb-3'>/</span>
            <InputWeb
              id='cardYear'
              placeholder='년'
              type='text'
              classInput='py-3 '
              value={paymentMethodInfoReq_Card.cardYear}
              onChange={handleChangeInput}
            />
          </div>
          {/* TODO */}
          {/* 파일 삭제 버튼이 필요함 */}
          <div className='flex flex-1 items-end w-[50%]'>
            <InputWeb
              id='agreement'
              label='동의서'
              placeholder='파일 업로드'
              type='text'
              classContainer='w-3/4'
              classInput='py-3 pr-20'
              value={paymentTypeInfoReq_Auto.consetImgName}
              readOnly
            />
            <FileUpload label='동의서 파일 선택' onFileSelect={handleUploadFile} />
          </div>
        </div>
      </div>
      <div className='flex'>
        <InputWeb
          id='cardOwner'
          label='명의자'
          placeholder='최대20자리'
          type='text'
          required
          classContainer='flex-1'
          classInput='py-3 pr-20'
          value={paymentMethodInfoReq_Card.cardOwner}
          onChange={handleChangeInput}
          maxLength={40}
          isValid={validateField('name', paymentMethodInfoReq_Card.cardOwner)}
          errorMsg='올바른 형식 아닙니다.'
        />
        <div className='flex flex-1 items-end ml-5'>
          <InputCalendar
            id='cardOwnerBirth'
            label='생년월일'
            placeholder='생년월일 8자리'
            required
            height='46px'
            width='100%'
            classContainer='w-3/4'
            value={paymentMethodInfoReq_Card.cardOwnerBirth}
            handleChangeValue={handleChangeInput}
          />
          <button
            className='h-[46px] w-1/4 bg-mint rounded-lg font-800 text-white transition-all duration-200 hover:bg-mint_hover ml-3'
            onClick={axiosVerifyCard}>
            카드 인증
          </button>
        </div>
      </div>
    </>
  );
};

export default CardMethodForm;
