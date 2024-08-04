import { updatePaymentDetail } from '@/apis/payment';
import { uploadConsentFile } from '@/apis/signature';
import PaymentInfoForm from '@/components/common/memberForm/PaymentInfoForm';
import useAlert from '@/hooks/useAlert';
import { useMemberContractStore } from '@/stores/useMemberContractStore';
import { useMemberPaymentStore } from '@/stores/useMemberPaymentStore';
import { formatCardYearForStorage } from '@/utils/format/formatCard';
import { useNavigate, useParams } from 'react-router-dom';

const UpdatePaymentInfo = ({ formType }) => {
  const { ...payment } = useMemberPaymentStore(); // 결제정보 - 수정목적
  const { contractInfo } = useMemberContractStore();
  const { contractId, memberId } = useParams();
  const onAlert = useAlert();
  const navigate = useNavigate();

  // <------ 결제 정보 수정 요청Data 형태변환 ------>
  const transformPaymentInfo = () => {
    const paymentCreateReq = {
      paymentTypeInfoReq: {
        paymentType: payment.paymentType,
        ...(payment.paymentType === 'VIRTUAL' && payment.paymentTypeInfoReq_Virtual),
        ...(payment.paymentType === 'BUYER' && payment.paymentTypeInfoReq_Buyer),
        ...(payment.paymentType === 'AUTO' &&
          (() => {
            const { consetImgName, ...rest } = payment.paymentTypeInfoReq_Auto;
            return rest;
          })()),
      },
      ...(payment.paymentType === 'AUTO' && {
        paymentMethodInfoReq: {
          paymentMethod: payment.paymentMethod,
          ...(payment.paymentMethod === 'CMS' && payment.paymentMethodInfoReq_Cms),
          ...(payment.paymentMethod === 'CARD' && {
            ...payment.paymentMethodInfoReq_Card,
            cardYear: formatCardYearForStorage(payment.paymentMethodInfoReq_Card.cardYear),
          }),
        },
      }),
      contractDay: contractInfo.contractDay,
    };
    console.log(paymentCreateReq);
    return paymentCreateReq;
  };

  // <------ 파일 업로드 및 결제 정보 수정 API ------>
  const axiosUpdatePaymentDetail = async () => {
    try {
      let fileUrl = null;
      if (payment.paymentType === 'AUTO' && payment.paymentTypeInfoReq_Auto.consetImgName) {
        const file = await fetch(payment.paymentTypeInfoReq_Auto.consetImgName).then(r => r.blob());
        const formData = new FormData();
        formData.append('file', file, payment.paymentTypeInfoReq_Auto.consetImgName);
        fileUrl = await uploadConsentFile(formData);
      }

      const paymentInfo = transformPaymentInfo();
      if (fileUrl) {
        paymentInfo.paymentTypeInfoReq.consentImgUrl = fileUrl;
      }

      const res = await updatePaymentDetail(contractId, paymentInfo);
      console.log('!----결제 정보 수정 성공----!', paymentInfo); // 삭제예정
      await navigate(`/vendor/contracts/detail/${contractId}`);
      onAlert({ msg: '결제정보가 수정되었습니다!', type: 'success', title: '결제정보수정' });
    } catch (err) {
      console.error('axiosUpdatePaymentDetail => ', err.response);
      onAlert({ msg: '결제정보 수정 중 오류가 발생했습니다.', type: 'error', title: '결제정보수정 실패' });
    }
  };

  return (
    <>
      <PaymentInfoForm formType={formType} />
      <div className='absolute bottom-0 left-0 flex h-[65px] w-full justify-end px-7 pb-5 font-800 text-lg '>
        <button
          className=' px-10 py-2 border border-mint rounded-lg text-mint'
          onClick={() => navigate(-1)}>
          취소
        </button>
        <button
          className=' px-10 py-2 bg-mint rounded-lg text-white transition-all duration-200 hover:bg-mint_hover ml-3'
          onClick={axiosUpdatePaymentDetail}
          >
          저장
        </button>
      </div>
    </>
  );
};

export default UpdatePaymentInfo;