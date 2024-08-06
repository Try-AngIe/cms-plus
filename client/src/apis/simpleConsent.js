import { privateAxios, publicAxios } from '.';

/* 간편서명동의 가능한 옵션 가져오기 */
export const getAvailableOptions = async vendorId => {
  const res = await publicAxios.get(`/v1/vendor/simple-consent/available-options/${vendorId}`);
  return res.data;
};

/* 간편서명동의 기존 계약 데이터 조회 */
export const getContractInfo = async (vendorId, contractId) => {
  const res = await publicAxios.get('/v1/simple-consent', {
    params: {
      vendor: vendorId,
      contract: contractId,
    },
  });
  return res.data;
};

/* 간편서명동의 회원/비회원 데이터 보내기 */
export const sendSimpleConsentData = async (vendorId, userData) => {
  const res = await publicAxios.post(`/v1/simple-consent/${vendorId}`, userData);
  return res.data;
};

/* 간편서명동의 기존 계약 서명이미지 업데이트*/
export const sendSimpleConsentSignImage = async (vendorId, data) => {
  const res = await publicAxios.post(`/v1/simple-consent/sign-image/${vendorId}`, data);
  return res.data;
};

/* 간편서명동의 설정 업데이트 */
export const updateSimpleConsent = async data => {
  const res = await privateAxios.put('/v1/vendor/simple-consent', data);
  return res.data;
};

/* 간편서명동의 데이터 가져오기 */
export const getSimpleConsent = async () => {
  const res = await privateAxios.get('/v1/vendor/simple-consent');
  return res.data;
};

/* 간편서명동의 링크 발송하기 */
export const sendReqSimpConsent = async contractId => {
  const res = await privateAxios.get(`/v1/vendor/simple-consent/send/${contractId}`);
  return res.data;
};

/* 간편서명동의 URL 전송 */
export const sendSimpleConsentUrl = async urlData => {
  const res = await privateAxios.post('/v1/kafka/messaging/simpconsent', urlData);
  return res;
};
