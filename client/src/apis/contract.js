import { privateAxios } from '.';

// 계약 목록 조회
export const getContractList = async (searchParams = {}) => {
  const res = await privateAxios.get('/v2/vendor/contract', {
    params: {
      ...searchParams,
    },
  });
  return res;
};

// 계약 상세 조회
export const getContractDetail = async contractId => {
  const res = await privateAxios.get(`/v1/vendor/contract/${contractId}`);
  return res;
};

// 계약상품 조회
export const getContractProducts = async contractId => {
  const res = await privateAxios.get(`/v2/vendor/contract/${contractId}/product`);
  return res;
};

// 계약 상세 조회 - 청구리스트
export const getContractDetailBillingList = async contractId => {
  const res = await privateAxios.get(`/v2/vendor/contract/${contractId}/billing`);
  return res;
};

// 회원 수정 - 계약 정보
export const updateContractDetail = async (contractId, data) => {
  const res = await privateAxios.put(`/v1/vendor/contract/${contractId}`, data);
  return res;
};
