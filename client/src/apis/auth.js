import { publicAxios } from '.';

// 회원가입
export const postJoin = async info => {
  try {
    return await publicAxios.post('/v1/vendor/auth/join', info);
  } catch (err) {
    console.error('회원가입 실패 =>', err.response.data);
    throw err;
  }
};
