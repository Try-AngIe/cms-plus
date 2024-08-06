import { getContractDetail } from '@/apis/contract';
import ConDetailBilling from '@/components/vendor/contract/ConDetailBilling';
import ConDetailBillingList from '@/components/vendor/contract/ConDetailBillingList';
import ConDetailContract from '@/components/vendor/contract/ConDetailContract';
import ConDetailHeader from '@/components/vendor/contract/ConDetailHeader';
import ConDetailPayment from '@/components/vendor/contract/ConDetailPayment';
import { useMemberBillingStore } from '@/stores/useMemberBillingStore';
import { useMemberContractStore } from '@/stores/useMemberContractStore';
import { useMemberPaymentStore } from '@/stores/useMemberPaymentStore';
import { bankCode } from '@/utils/bank/bank';
import { formatCardYearForDisplay } from '@/utils/format/formatCard';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router';

const ContractDetailPage = () => {
  const { setContractInfoItem, setContractProducts } = useMemberContractStore(); // 계약정보 - 수정목적
  const { setBillingInfoItem } = useMemberBillingStore(); // 청구정보 - 수정목적
  const {
    setPaymentType,
    setPaymentTypeInfoReq_Virtual,
    setAvailableMethods,
    setPaymentTypeInfoReq_Auto,
    setPaymentMethod,
    setPaymentMethodInfoReq_Cms,
    setPaymentMethodInfoReq_Card,
  } = useMemberPaymentStore(); // 결제정보 - 수정목적

  const [contractData, setContractData] = useState({
    contractId: 1,
    contractName: '',
    createdDateTime: '',
    modifiedDateTime: '',
    contractProducts: [],
    contractPrice: 0,
    contractStartDate: '',
    contractEndDate: '',
    paymentTypeInfo: {
      paymentType: {},
    },
    invoiceSendMethod: {
      title: '',
    },
    paymentMethodInfo: null,
    totalBillingCount: 0,
    totalBillingPrice: 0,
  });

  const [isLoading, setLoading] = useState(false);
  const { id: contractId } = useParams();

  // <----- 계약 상세 조회 ----->
  const axiosContractDetail = async () => {
    try {
      setLoading(true);
      const res = await getContractDetail(contractId);
      console.log('!----계약 상세 조회 성공----!', res);
      setContractData(res.data);
      setLoading(false);

      console.log('계약상세 : ', res.data);

      console.log(res.data);
    } catch (err) {
      console.log('effect');
      console.error('axiosContrctDetail => ', err.response.data);
    }
  };

  // <----- 회원 계약 정보 zustand에 입력 ----->
  const updateContractInfo = data => {
    setContractInfoItem({
      contractName: data.contractName,
      contractDay: data.contractDay,
      contractStartDate: data.contractStartDate,
      contractEndDate: data.contractEndDate,
    });
    setContractProducts(data.contractProducts);
  };

  // <------ 회원 청구 정보 zustand에 입력 ------>
  const updateBillingInfo = data => {
    setBillingInfoItem({
      invoiceSendMethod: data.invoiceSendMethod.code,
      autoInvoiceSend: data.autoInvoiceSend,
      autoBilling: data.autoBilling,
    });
  };

  // <----- 회원 결제 정보(결제방식) zustand에 입력 ----->
  const updatePaymentTypeInfo = data => {
    setPaymentType(data.paymentType.code);

    if (data.paymentType.code === 'VIRTUAL') {
      setPaymentTypeInfoReq_Virtual({
        bank: bankCode[data.bank.code],
        accountOwner: data.accountOwner,
      });
    }

    if (data.paymentType.code === 'BUYER') {
      setAvailableMethods(
        data.availableMethods.map(method => {
          return method.code;
        })
      );
    }

    if (data.paymentType.code === 'AUTO') {
      setPaymentTypeInfoReq_Auto({
        consentImgUrl: data.consentImgUrl,
        simpleConsentReqDateTime: data.simpleConsentReqDateTime,
      });
    }
  };

  // <----- 회원 결제 정보(결제수단) zustand에 입력 ----->
  const updatePaymentMethodInfo = data => {
    if (data) {
      setPaymentMethod(data.paymentMethod.code);

      if (data.paymentMethod.code === 'CMS') {
        setPaymentMethodInfoReq_Cms({
          bank: bankCode[data.bank.code],
          accountNumber: '',
          accountOwner: data.accountOwner,
          accountOwnerBirth: data.accountOwnerBirth,
        });
      }

      if (data.paymentMethod.code === 'CARD') {
        setPaymentMethodInfoReq_Card({
          cardNumber: '',
          cardMonth: data.cardMonth,
          cardYear: formatCardYearForDisplay(data.cardYear),
          cardOwner: data.cardOwner,
          cardOwnerBirth: data.cardOwnerBirth,
        });
      }
    }
  };

  // <----- 수정을 위한 모든 상태 업데이트 ----->
  const updateAllInfo = () => {
    updateContractInfo(contractData);
    updateBillingInfo(contractData);
    updatePaymentTypeInfo(contractData.paymentTypeInfo);
    updatePaymentMethodInfo(contractData.paymentMethodInfo);
  };

  useEffect(() => {
    axiosContractDetail();
  }, []);

  return (
    <div>
      <ConDetailHeader contractData={contractData} />
      <div className='flex gap-6 mb-6 h-730'>
        <ConDetailContract contractData={contractData} updateAllInfo={updateAllInfo} />
        <ConDetailPayment contractData={contractData} updateAllInfo={updateAllInfo} />
      </div>
      <ConDetailBilling contractData={contractData} updateAllInfo={updateAllInfo}>
        <ConDetailBillingList />
      </ConDetailBilling>
    </div>
  );
};

export default ContractDetailPage;
