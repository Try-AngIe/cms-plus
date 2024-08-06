import axios from 'axios';
import { privateAxios, publicAxios } from '.';

// 청구 생성
export const createBilling = async billingReq => {
  const res = await privateAxios.post('/v2/vendor/billing', billingReq);
  return res;
};

// 청구 목록 조회
export const getBillingList = async (searchParams = {}) => {
  const res = await privateAxios.get('/v2/vendor/billing', {
    params: {
      ...searchParams,
    },
  });
  return res;
};

// 청구 상세 조회
export const getBillingDetail = async billingId => {
  const res = await privateAxios.get(`/v2/vendor/billing/${billingId}`);
  return res;
};

// 청구 상세 조회 - 청구상품 목록
export const getBillingProducts = async billingId => {
  const res = await privateAxios.get(`/v2/vendor/billing/${billingId}/product`);
  return res;
};

// 청구 수정
export const updateBilling = async (billingId, billingReq) => {
  const res = await privateAxios.put(`/v2/vendor/billing/${billingId}`, billingReq);
  return res;
};

// 청구 삭제
export const deleteBilling = async billingId => {
  const res = await privateAxios.delete(`/v2/vendor/billing/${billingId}`);
  return res;
};

// 청구서 발송
export const sendInvoice = async billingId => {
  const res = await privateAxios.get(`/v2/vendor/billing/${billingId}/sendInvoice`);
  return res;
};

// 청구서 발송취소
export const cancelSendInvoice = async billingId => {
  const res = await privateAxios.get(`/v2/vendor/billing/${billingId}/cancelInvoice`);
  return res;
};

// 청구 실시간 결제
export const payRealTimeBilling = async billingId => {
  const res = await privateAxios.get(`/v2/vendor/billing/${billingId}/realtimePay`);
  return res;
};

// 청구 실시간 결제취소
export const cancelPayBilling = async billingId => {
  const res = await privateAxios.get(`/v2/vendor/billing/${billingId}/cancelPay`);
  return res;
};

// 회원 수정 - 청구 정보
export const updateMemberBilling = async (memberId, billingData) => {
  const res = await privateAxios.put(
    `/v1/vendor/management/members/billing/${memberId}`,
    billingData
  );
  return res;
};

const BASE_URL = import.meta.env.VITE_BASE_URL;

// 모바일 - 청구서 정보 조회
export const getBillingInfo = async billingId => {
  const mAxios = axios.create({
    baseURL: BASE_URL,
    headers: {
      'Access-Control-Allow-Origin': `${BASE_URL}`,
      'Content-Type': 'application/json',
    },
  });
  const res = mAxios.get(`/v2/vendor/billing/invoice/${billingId}`);
  return res;
};
