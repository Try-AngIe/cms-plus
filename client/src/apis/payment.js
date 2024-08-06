import { privateAxios, publicAxios } from '.';

// 납부자-카드 결제
export const requestCardPayment = async paymentData => {
  const res = await publicAxios.post('/v1/kafka/payment/card', paymentData);
  return res;
};

// 납부자-계좌 결제
export const requestAccountPayment = async paymentData => {
  console.log(paymentData);
  const res = await publicAxios.post('/v1/kafka/payment/account', paymentData);
  return res;
};

// 가상계좌 결제
export const requestVirtualAccountPayment = async paymentData => {
  const res = await publicAxios.post('/v1/kafka/payment/virtual-account', paymentData);
  return res;
};

// 회원 수정 - 결제 정보
export const updatePaymentDetail = async (contractId, data) => {
  const res = await privateAxios.put(`/v1/vendor/management/members/payment/${contractId}`, data);
  return res;
};

// 은행정보 불러오기
export const getBanks = async () => {
  const res = await publicAxios.get(`/v1/payment/banks`);
  return res;
};
