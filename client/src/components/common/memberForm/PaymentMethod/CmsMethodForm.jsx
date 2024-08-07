import SelectField from '@/components/common/selects/SelectField';
import InputWeb from '@/components/common/inputs/InputWeb';
import { useMemberPaymentStore } from '@/stores/useMemberPaymentStore';
import InputCalendar from '@/components/common/inputs/InputCalendar';
import FileUpload from '../../inputs/FileUpload';
import { verifyCMS } from '@/apis/validation';
import { validateField } from '@/utils/validators';
import useAlert from '@/hooks/useAlert';
import { bankOptions } from '@/utils/bank/bank';
import { useEffect } from 'react';

const CmsMethodForm = ({ paymentMethod, formType }) => {
  const {
    paymentMethodInfoReq_Cms,
    setPaymentMethodInfoReq_Cms,
    paymentTypeInfoReq_Auto,
    setPaymentTypeInfoReq_Auto,
    isVerified,
    setIsVerified,
  } = useMemberPaymentStore();

  const onAlert = useAlert();

  // <----- 셀렉터 필드 은행명 변경 ----->
  const handleChangeSelect = e => {
    setIsVerified(false);
    setPaymentMethodInfoReq_Cms({ bank: e.target.value });
  };
  // <----- 인풋 필드 입력값 변경 ----->
  const handleChangeInput = e => {
    setIsVerified(false);
    const { id, value } = e.target;
    setPaymentMethodInfoReq_Cms({ [id]: value });
  };

  // <----- 파일 업로드 ----->
  const handleUploadFile = file => {
    // TODO
    // simpleConsentReqDateTime 동의 요청시간 일단은 현재시각으로 설정
    const currentTime = new Date().toISOString();
    setPaymentTypeInfoReq_Auto({
      consentImgUrl: URL.createObjectURL(file),
      consetImgName: file.name,
      simpleConsentReqDateTime: currentTime,
    });
  };

  // <----- 실시간 CMS 계좌인증 API ----->
  const axiosVerifyCMS = async () => {
    try {
      const { bank, ...paymentCmsinfo } = paymentMethodInfoReq_Cms;
      const transformPaymentCms = {
        paymentMethod: paymentMethod,
        ...paymentCmsinfo,
      };
      const res = await verifyCMS(transformPaymentCms);
      setIsVerified(true);
      console.log('!----실시간 CMS 계좌인증 API----!'); // 삭제예정

      if (res) {
        onAlert({ msg: '계좌인증에 성공하셨습니다!', type: 'success', title: '계좌 인증 성공' });
      } else {
        onAlert({ msg: '계좌인증에 실패하셨습니다.', type: 'error', title: '계좌 인증 실패' });
      }
    } catch (err) {
      console.error('axiosVerifyCMS => ', err.response);
      onAlert({ msg: '계좌인증에 실패하셨습니다.', type: 'error', title: '계좌 인증 오류' });
    }
  };

  useEffect(() => {
    if (formType === 'CREATE') setIsVerified(false);
    if (formType === 'UPDATE') setIsVerified(true);
  }, []);

  return (
    <>
      <div className='flex mt-6 mb-2'>
        <div className='flex flex-1 items-end '>
          <SelectField
            label='은행'
            classContainer='w-1/4'
            classLabel='text-15 text-text_black font-700 ml-2'
            classSelect='py-3 p-4 rounded-lg'
            options={bankOptions}
            required
            value={paymentMethodInfoReq_Cms.bank}
            onChange={handleChangeSelect}
          />
          <InputWeb
            id='accountOwner'
            label='예금주'
            placeholder='최대 20자리'
            type='text'
            required
            classContainer='w-3/4 ml-3 mb-4'
            classInput='py-3 pr-20'
            value={paymentMethodInfoReq_Cms.accountOwner}
            onChange={handleChangeInput}
            maxLength={40}
            isValid={validateField('name', paymentMethodInfoReq_Cms.accountOwner)}
            errorMsg='올바른 형식 아닙니다.'
          />
        </div>
        {/* TODO */}
        {/* 파일 삭제 버튼이 필요함 */}
        <div className='flex flex-1 items-end mb-4 ml-5 '>
          <InputWeb
            id='agreement'
            label='동의서'
            placeholder='최대 20자리'
            type='close'
            classContainer='w-3/4'
            classInput='py-3 pr-20'
            value={paymentTypeInfoReq_Auto.consetImgName}
            readOnly
          />
          <FileUpload label='동의서 파일 선택' onFileSelect={handleUploadFile} />
        </div>
      </div>
      <div className='flex'>
        <InputCalendar
          id='accountOwnerBirth'
          label='생년월일'
          placeholder='생년월일 8자리'
          required
          height='45px'
          width='100%'
          classContainer='flex-1'
          value={paymentMethodInfoReq_Cms.accountOwnerBirth}
          handleChangeValue={handleChangeInput}
        />
        <div className='relative flex flex-1 items-end ml-5'>
          <InputWeb
            id='accountNumber'
            label='계좌번호'
            placeholder='최대 14자리'
            type='text'
            required
            classContainer='w-3/4'
            classInput='py-3 pr-20'
            value={paymentMethodInfoReq_Cms.accountNumber}
            onChange={handleChangeInput}
            maxLength={14}
            isValid={validateField('accountNumber', paymentMethodInfoReq_Cms.accountNumber)}
            errorMsg='올바른 형식 아닙니다.'
          />
          <button
            className={`h-[46px] w-1/4 bg-mint rounded-lg font-800 text-white ml-3
                        ${!isVerified && 'transition-all duration-200 hover:bg-mint_hover '}`}
            disabled={isVerified}
            onClick={axiosVerifyCMS}>
            {isVerified ? '인증완료' : '계좌번호 인증'}
          </button>
        </div>
      </div>
    </>
  );
};

export default CmsMethodForm;
