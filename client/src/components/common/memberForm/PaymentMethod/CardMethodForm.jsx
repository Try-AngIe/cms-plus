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

const CardMethodForm = ({ paymentMethod, formType }) => {
  const [selectedFile, setSelectedFile] = useState(null);

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
      setPaymentMethodInfoReq_Card({ ...paymentMethodInfoReq_Card, [id]: unformattedNumber });
    } else if (id === 'cardYear') {
      setPaymentMethodInfoReq_Card({ ...paymentMethodInfoReq_Card, [id]: formatCardYearForDisplay(value) });
    } else if (id === 'cardMonth') {
      setPaymentMethodInfoReq_Card({ ...paymentMethodInfoReq_Card, [id]: formatCardMonthForDisplay(value) });
    } else {
      setPaymentMethodInfoReq_Card({ ...paymentMethodInfoReq_Card, [id]: value });
    }
  };

  const handleFileSelect = file => {
    setSelectedFile(file);
    setPaymentTypeInfoReq_Auto({
      ...paymentTypeInfoReq_Auto,
      consetImgName: file.name,
    });
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
            <FileUpload label='동의서 파일 선택' onFileSelect={handleFileSelect} />
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