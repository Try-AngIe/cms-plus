import { getMemberCheck, postCreateMember } from '@/apis/member';
import NextButton from '@/components/common/buttons/StatusNextButton';
import PreviousButton from '@/components/common/buttons/StatusPreButton';
import ProgressBar from '@/components/common/ProgressBar';
import RegisterBasicInfo from '@/components/vendor/member/registers/RegisterBasicInfo';
import RegisterBillingInfo from '@/components/vendor/member/registers/RegisterBillingInfo';
import RegisterContractInfo from '@/components/vendor/member/registers/RegisterContractInfo';
import RegisterPaymentInfo from '@/components/vendor/member/registers/RegisterPaymentInfo';
import useStatusStepper from '@/hooks/useStatusStepper';
import { useMemberBasicStore } from '@/stores/useMemberBasicStore';
import { useMemberBillingStore } from '@/stores/useMemberBillingStore';
import { useMemberContractStore } from '@/stores/useMemberContractStore';
import { useMemberPaymentStore } from '@/stores/useMemberPaymentStore';
import { useStatusStore } from '@/stores/useStatusStore';
import { formatCardYearForStorage } from '@/utils/format/formatCard';
import { useLocation, useNavigate } from 'react-router-dom';
import close from '@/assets/close.svg';
import { sendReqSimpConsent } from '@/apis/simpleConsent';
import useAlert from '@/hooks/useAlert';
import { validateField } from '@/utils/validators';
import { useEffect, useState } from 'react';
import useConfirm from '@/hooks/useConfirm';
import { formatPhone } from '@/utils/format/formatPhone';
import MemberChooseModal from '@/components/vendor/modal/MemberChooseModal';
import user from '@/assets/user.svg';

const ContractRegisterPage = () => {
  const start = 0;
  const end = 3;
  const { status, reset, setStatus } = useStatusStore();
  const { handleClickPrevious, handleClickNext } = useStatusStepper('memberRegister', start, end);
  const [isShowModal, setIsShowModal] = useState(false);
  const navigate = useNavigate();
  const onAlert = useAlert();
  const onConfirm = useConfirm();

  const location = useLocation();
  const { state } = location;
  const type = state?.type;

  // <------ 회원등록 입력 데이터 ------>
  const { basicInfo, resetBasicInfo } = useMemberBasicStore();
  const { contractInfo, resetContractInfo } = useMemberContractStore();
  const { billingInfo, resetBillingInfo } = useMemberBillingStore();
  const {
    paymentType,
    paymentMethod,
    paymentTypeInfoReq_Virtual,
    paymentTypeInfoReq_Buyer,
    paymentTypeInfoReq_Auto,
    paymentMethodInfoReq_Cms,
    paymentMethodInfoReq_Card,
    isSimpConsentCheck,
    ...paymentResetFunctions
  } = useMemberPaymentStore();

  // <----- 등록 폼 교체 ----->
  const componentMap = {
    0: { title: '기본정보', component: RegisterBasicInfo }, // 기본정보
    1: { title: '계약정보', component: RegisterContractInfo }, // 계약정보
    2: { title: '결제정보', component: RegisterPaymentInfo }, // 결제정보
    3: { title: '청구정보', component: RegisterBillingInfo }, // 청구정보
  };

  // <----- loading 컴포넌트로 교체 시 UX적으로 좋아질듯 ----->
  const { title, component: Content } = componentMap[status] || {
    title: '',
    component: () => '',
  };

  // <----- 회원등록 API ----->
  const axiosCreateMember = async () => {
    try {
      if (status === 3) {
        const data = {
          ...basicInfo, // 기본정보
          contractCreateReq: transformContractInfo(), // 계약정보
          paymentCreateReq: transformPaymentInfo(), // 결제정보
          ...billingInfo, // 청구정보
        };

        // validation 체크
        if (!validateBasicInfo()) return;
        if (!validateContractInfo()) return;
        if (!validatePaymentInfo(data.paymentCreateReq)) return;

        const res = await postCreateMember(data);
        console.log('!----회원등록 성공----!'); // 삭제예정

        if (isSimpConsentCheck && !paymentMethod && paymentType === 'AUTO') {
          await axiosSendReqSimpConsent(res.data);
        }
        await navigate('/vendor/contracts');
        onAlert({ msg: '회원의 계약이 등록되었습니다!', type: 'success' });
      }
    } catch (err) {
      // onAlert({ err });
      console.error('axiosCreateMember => ', err.response);
    }
  };

  // <----- 간편서명동의 링크 발송하기 ----->
  const axiosSendReqSimpConsent = async contractId => {
    try {
      const res = await sendReqSimpConsent(contractId);
      console.log('!----간편서명동의 링크 발송하기 성공----!'); // 삭제예정
    } catch (err) {
      console.error('axiosSendReqSimpConsent => ', err.response);
    }
  };

  // <----- 회원 등록 여부 체크 ----->
  const axiosMemberCheck = async () => {
    try {
      if (status === 0 && type === 'new') {
        // validation 체크
        if (!validateBasicInfo()) {
          return;
        }

        setStatus(0);
        const res = await getMemberCheck(basicInfo.memberPhone, basicInfo.memberEmail);
        console.log('!---- 회원 등록 여부 체크 성공----!'); // 삭제예정
        setStatus(0);
        let message = '';
        const { phoneExist, emailExist } = res.data;
        if (phoneExist) {
          message = `"${formatPhone(basicInfo.memberPhone)}" 번호로 이미 회원이 존재합니다. 기존회원 계약을 진행하시겠습니까?`;
        } else if (emailExist) {
          message = `"${basicInfo.memberEmail}" 이메일로 이미 회원이 존재합니다. 기존회원 계약을 진행하시겠습니까?`;
        } else {
          setStatus(1);
        }

        if (message) {
          const isExtendRegister = await onConfirm({
            msg: message,
            type: 'warning',
            title: '입력 정보 오류',
          });

          if (isExtendRegister) {
            setIsShowModal(true);
            // navigate('/vendor/contracts', { state: { type: 'old' } });
          }
        }
      }
    } catch (err) {
      console.error('axiosMemberCheck => ', err.response);
    }
  };

  // <----- 상품 목록을 contractProducts형식으로 변환 ----->
  const mapContractProducts = products => {
    return products.map(option => ({
      productId: option.productId,
      price: option.price,
      quantity: option.quantity || 1, // 기본 값으로 1 설정
    }));
  };

  // <----- 계약정보 데이터 변화 ----->
  const transformContractInfo = () => {
    const { contractName, contractStartDate, contractEndDate, contractDay, contractProducts } =
      contractInfo;
    return {
      contractName,
      contractStartDate,
      contractEndDate,
      contractDay,
      contractProducts: mapContractProducts(contractProducts),
    };
  };

  // <----- 결제정보 데이터 변화 ----->
  const transformPaymentInfo = () => {
    const paymentCreateReq = {
      paymentTypeInfoReq: {
        paymentType,
        ...(paymentType === 'VIRTUAL' && paymentTypeInfoReq_Virtual),
        ...(paymentType === 'BUYER' && paymentTypeInfoReq_Buyer),
        ...(paymentType === 'AUTO' &&
          (() => {
            const { consetImgName, ...rest } = paymentTypeInfoReq_Auto;
            return rest;
          })()),
      },
    };

    if (paymentType === 'AUTO' && paymentMethod !== '') {
      paymentCreateReq.paymentMethodInfoReq = {
        paymentMethod,
        ...(paymentMethod === 'CMS' && paymentMethodInfoReq_Cms),
        ...(paymentMethod === 'CARD' && {
          ...paymentMethodInfoReq_Card,
          cardYear: formatCardYearForStorage(paymentMethodInfoReq_Card.cardYear),
        }),
      };
    }

    return paymentCreateReq;
  };

  // <----- 유효성 검사 : BasicInfo ----->
  const validateBasicInfo = () => {
    const isValidName = validateField('name', basicInfo.memberName);
    const isValidPhone = validateField('phone', basicInfo.memberPhone);
    const isValidEnrollDate = basicInfo.memberEnrollDate !== '';
    const isValidHomePhone = basicInfo.memberHomePhone
      ? validateField('homePhone', basicInfo.memberHomePhone)
      : true;
    const isValidEmail = validateField('email', basicInfo.memberEmail);
    const isSuccess =
      isValidName && isValidPhone && isValidEmail && isValidEnrollDate && isValidHomePhone;

    if (!isSuccess) {
      setStatus(0);
      onAlert({ msg: '기본정보가 잘못 입력되었습니다.', type: 'error', title: '입력 정보 오류' });
    }
    return isSuccess;
  };

  // <----- 유효성 검사 : ContractInfo ----->
  const validateContractInfo = () => {
    const isValidContractName = validateField('contractName', contractInfo.contractName);

    const isValidContractProducts =
      contractInfo.contractProducts.length >= 1 &&
      contractInfo.contractProducts.length <= 10 &&
      contractInfo.contractProducts.every(product => product.quantity && product.quantity > 0);

    const isSuccess = isValidContractName && isValidContractProducts;

    if (!isSuccess) {
      setStatus(1);
      onAlert({ msg: '계약정보가 잘못 입력되었습니다.', type: 'error', title: '입력 정보 오류' });
    }
    return isSuccess;
  };

  // <----- 유효성 검사 : PaymentInfo ----->
  const validatePaymentInfo = data => {
    const isValidStartDate =
      contractInfo.contractStartDate && !isNaN(new Date(contractInfo.contractStartDate).getTime());
    const isValidEndDate =
      contractInfo.contractEndDate &&
      !isNaN(new Date(contractInfo.contractEndDate).getTime()) &&
      new Date(contractInfo.contractEndDate) >= new Date(contractInfo.contractStartDate);
    const isValidContractDay = contractInfo.contractDay >= 1 && contractInfo.contractDay <= 31;

    let isSuccess = isValidStartDate && isValidEndDate && isValidContractDay;

    const paymentTypeInfoReq = data.paymentTypeInfoReq;
    const paymentMethodInfoReq = data.paymentMethodInfoReq;

    // 자동결제 선택한 경우
    if (paymentTypeInfoReq.paymentType === 'AUTO') {
      // Cms 선택한 경우
      if (paymentMethodInfoReq && paymentMethodInfoReq.paymentMethod === 'CMS') {
        const isValidBank = paymentMethodInfoReq.bank !== '';
        const isValidAccountNumber = validateField(
          'accountNumber',
          paymentMethodInfoReq.accountNumber
        );
        const isValidAccountOwner = validateField('name', paymentMethodInfoReq.accountOwner);
        const isValidAccountOwnerBirth = paymentMethodInfoReq.accountOwnerBirth !== '';

        isSuccess =
          isSuccess &&
          isValidBank &&
          isValidAccountNumber &&
          isValidAccountOwner &&
          isValidAccountOwnerBirth;
      }
      // Card 선택한 경우
      else if (paymentMethodInfoReq && paymentMethodInfoReq.paymentMethod === 'CARD') {
        const isValidCardNumber = validateField('cardNumber', paymentMethodInfoReq.cardNumber);
        const isValidCardMonth = validateField('cardMonth', paymentMethodInfoReq.cardMonth);
        const isValidCardYear = paymentMethodInfoReq.cardYear !== '';
        const isValidCardOwner = validateField('name', paymentMethodInfoReq.cardOwner);
        const isValidCardOwnerBirth = paymentMethodInfoReq.cardOwnerBirth !== '';

        isSuccess =
          isSuccess &&
          isValidCardNumber &&
          isValidCardMonth &&
          isValidCardYear &&
          isValidCardOwner &&
          isValidCardOwnerBirth;
      }
    }
    if (paymentTypeInfoReq.paymentType === 'VIRTUAL') {
      const isValidBank = paymentTypeInfoReq.bank !== '';
      const isValidAccountOwner = validateField('name', paymentTypeInfoReq.accountOwner);
      isSuccess = isSuccess && isValidBank && isValidAccountOwner;
    }

    if (!isSuccess) {
      setStatus(2);
      onAlert({ msg: '결제정보가 잘못 입력되었습니다.', type: 'error', title: '입력 정보 오류' });
    }
    return isSuccess;
  };

  useEffect(() => {
    const handleBeforeUnload = event => {
      event.preventDefault();
      event.returnValue = '';
      reset();
    };

    window.addEventListener('beforeunload', handleBeforeUnload);

    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, []);

  // <----- 페이지 이탈 시 Status reset ----->
  useEffect(() => {
    reset();
    return () => {
      reset();
      resetBasicInfo();
      resetBillingInfo();
      resetContractInfo();
      paymentResetFunctions.resetPaymentType();
      paymentResetFunctions.resetPaymentMethod();
      paymentResetFunctions.resetPaymentTypeInfoReq_Virtual();
      paymentResetFunctions.resetPaymentTypeInfoReq_Buyer();
      paymentResetFunctions.resetPaymentTypeInfoReq_Auto();
      paymentResetFunctions.resetPaymentMethodInfoReq_Cms();
      paymentResetFunctions.resetPaymentMethodInfoReq_Card();
    };
  }, []);

  return (
    <>
      <div className='up-dashboard relative flex justify-center items-center mb-4 w-full desktop:h-[18%]'>
        <ProgressBar steps={['기본정보', '계약정보', '결제정보', '청구정보']} />
        <img
          src={close}
          alt='back'
          className='absolute right-6 top-6 cursor-pointer w-4 h-4'
          onClick={() => navigate('/vendor/contracts')}
        />
      </div>
      <div className='primary-dashboard flex flex-col relative h-[1000px] large_desktop:h-[80%] '>
        <div className='flex items-center h-[50px] px-2 pb-[10px] '>
          <p className='text-text_black text-xl font-800'>{title}</p>
        </div>
        <Content formType={'CREATE'} />
        <div className='absolute bottom-0 left-0 flex h-[65px] w-full justify-between px-7 pb-5 font-800 text-lg '>
          <PreviousButton
            onClick={handleClickPrevious}
            status={status}
            type={'memberRegister'}
            start={start}
            end={end}
          />
          <NextButton
            onClick={() => {
              handleClickNext();
              axiosCreateMember();
              axiosMemberCheck();
            }}
            status={status}
            type={'memberRegister'}
            end={end}
          />
        </div>
        <MemberChooseModal
          isShowModal={isShowModal}
          icon={user}
          setIsShowModal={setIsShowModal}
          modalTitle={'회원선택'}
        />
      </div>
    </>
  );
};

export default ContractRegisterPage;
