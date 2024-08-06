import { privateAxios } from '.';

// 회원 등록
export const postCreateMember = async memberData => {
  const res = await privateAxios.post('/v1/vendor/management/members', memberData);
  return res;
};

// 회원 등록 - 기본정보
export const postCreateMemberBasic = async memberData => {
  const res = await privateAxios.post('/v1/vendor/management/members/basic', memberData);
  return res;
};

// 회원 등록 여부 체크
export const getMemberCheck = async (phone, email) => {
  const res = await privateAxios.get('/v1/vendor/management/members/check', {
    params: {
      phone: phone,
      email: email,
    },
  });
  return res;
};

// 회원 목록 조회
export const getMemberList = async (searchParams = {}) => {
  const res = await privateAxios.get('/v2/vendor/management/members', {
    params: {
      ...searchParams,
    },
  });
  return res;
};

// 회원 기본정보 목록 조회
export const getMemberBasicInfoList = async (searchParams = {}) => {
  const res = await privateAxios.get('/v1/vendor/management/basicinfo/members', {
    params: {
      ...searchParams,
    },
  });
  return res;
};

// 회원 상세 조회
export const getMemberDetail = async memberId => {
  const res = await privateAxios.get(`/v1/vendor/management/members/${memberId}`);
  return res;
};

// 회원 상세 조회 - 계약리스트
export const getMemberDetailContractList = async (memberId, searchParams = {}) => {
  const res = await privateAxios.get(`/v1/vendor/management/members/contracts/${memberId}`, {
    params: {
      ...searchParams,
    },
  });
  return res;
};

// 회원 수정 - 기본 정보
export const updateMemberBaic = async (memberId, memberData) => {
  const res = await privateAxios.put(`/v1/vendor/management/members/${memberId}`, memberData);
  return res;
};

// 회원 삭제
export const deleteMember = async memberId => {
  const res = await privateAxios.delete(`/v1/vendor/management/members/${memberId}`);
  return res;
};

// 회원 삭제 - 회원의 진행중인 계약수
export const getContractListByMember = async memberId => {
  const res = await privateAxios.get(`/v1/vendor/management/members/contracts/${memberId}`);
  return res;
};

// 회원 삭제 - 회원의 진행중인 청구수
export const getBillingListByMember = async memberId => {
  const res = await privateAxios.get(`/v1/vendor/management/members/${memberId}/billing`);
  return res;
};

// 회원 엑셀 -> json
export const convertMember = async formData => {
  const res = await privateAxios.post('/v1/vendor/management/convert', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return res;
};

// 회원 json -> 저장
export const uploadMembers = async members => {
  const res = await privateAxios.post('/v1/vendor/management/upload', members);
  return res;
};
