import { publicUploadFileAxios } from '.';

export const uploadSignature = async formData => {
  const res = await publicUploadFileAxios.post('/v1/simple-consent/sign', formData);
  return res.data.fileUrl;
};

export const uploadConsentFile = async formData => {
  const res = await publicUploadFileAxios.post('/v1/simple-consent/consent', formData);
  return res.data.fileUrl;
};