import { useNavigate } from 'react-router-dom';
import InputWeb from './common/inputs/InputWeb';
import { getCheckUsername, postLogin, postRequestAuthenticationNumber } from '@/apis/auth';
import { useEffect, useState } from 'react';
import FindVendoPasswordModal from '@/components/vendor/modal/FIndVendorPasswordModal';
import FindVendorIdModal from '@/components/vendor/modal/FindVendorIdModal';
import ResetPasswordModal from '@/components/vendor/modal/ResetPasswordModal';
import SuccessFindIdModal from '@/components/vendor/modal/SuccessFindIdModal';
import user from '@/assets/user.svg';
import password from '@/assets/password.svg';
import { useVendorInfoStore } from '@/stores/useVendorInfoStore';
import useAlert from '@/hooks/useAlert';
import Spinner from './common/loadings/Spinner';

const LoginForm = () => {
  const navigate = useNavigate();
  const [isShowIdModal, setIsShowIdModal] = useState(false);
  const [isShowPasswordModal, setIsShowPasswordModal] = useState(false);
  const [isShowResetPasswordModal, setIsShowResetPasswordModal] = useState(false);
  const [isShowSuccessFindIdModal, setIsShowSuccessFindIdModal] = useState(false);
  const [findedId, setFindedId] = useState('');
  const { setVendorInfo } = useVendorInfoStore();
  const [vendorFormData, setVendorFormData] = useState({
    username: '',
    password: '',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [time, setTime] = useState(0);
  const onAlert = useAlert();

  // 사용자 입력값
  const handleChangeValue = e => {
    const { id, value } = e.target;
    setVendorFormData(prev => ({ ...prev, [id]: value == '' ? '' : value }));
  };

  // 공백입력 막기
  const handleKeyDown = e => {
    e.key === ' ' && e.preventDefault();
  };

  // 폼 제출 핸들러
  const handleSubmit = e => {
    e.preventDefault();
    axiosLogin(vendorFormData);
  };

  // 로그인 API
  const axiosLogin = async data => {
    try {
      // 아이디 입력 여부

      if (!data.username) {
        onAlert({ msg: '아이디를 입력해주세요!', type: 'error', title: '로그인 오류' });
        return;
      }

      // 비밀번호 입력 여부
      if (!data.password) {
        onAlert({ msg: '비밀번호를 입력해주세요!', type: 'error', title: '로그인 오류' });
        return;
      }

      setIsLoading(true);
      const resLogin = await postLogin(data);
      console.log('!----로그인 성공----!'); // 삭제예정
      const { accessToken, ...vendorInfo } = resLogin.data;
      localStorage.setItem('access_token', accessToken);
      localStorage.setItem('time', JSON.stringify(3600));
      setVendorInfo(vendorInfo);
      navigate('/vendor/dashboard');
      setIsLoading(false);
    } catch (err) {
      // 아이디 존재 여부
      const resCheckUsername = await getCheckUsername(data.username);
      const isChecked = resCheckUsername.data;
      if (isChecked) {
        await onAlert({
          msg: '비밀번호가 올바르지 않습니다.',
          type: 'error',
          title: '로그인 오류',
        });
        setIsLoading(false);
        return;
      }
      await onAlert({ msg: '존재하지 않는 아이디입니다.', type: 'error', title: '로그인 오류' });
      setIsLoading(false);
      console.error('axiosJoin => ', err.response);
    }
  };

  // <---- 인증번호 요청 API ---->
  const axiosRequestAuthenticationNumber = async formData => {
    try {
      const data = {
        userInfo: formData.name ? formData.name : formData.username,
        methodInfo: formData.method === 'SMS' ? formData.phone : formData.email,
        method: formData.method,
      };
      const res = await postRequestAuthenticationNumber(data);
      console.log('!----인증번호 요청 성공----!'); // 삭제예정
      setTime(300);
    } catch (err) {
      console.error('axiosRequestAuthenticationNumber => ', err.response);
    }
  };

  useEffect(() => {
    setTime(0);
  }, [isShowIdModal, isShowPasswordModal]);

  useEffect(() => {
    localStorage.clear();
  }, []);

  return (
    <div className='absolute left-0 top-0 h-[100vh] mobile:h-full w-full mobile:w-[56vw] flex justify-center items-center'>
      <form onSubmit={handleSubmit} className=' w-480 h-640 flex flex-col justify-around p-16'>
        <p className='text-mint font-900 text-3xl'>Hyosung CMS#</p>
        <p className='text-text_grey font-800 mb-3'>수납 및 자금 관리 통합 비즈니스 솔루션</p>
        <div>
          <InputWeb
            id='username'
            label='아이디'
            type='text'
            placeholder='아이디를 입력해 주세요.'
            classInput='mb-4'
            value={vendorFormData.username}
            onChange={handleChangeValue}
            onKeyDown={handleKeyDown}
            maxLength={20}
          />
          <InputWeb
            id='password'
            label='비밀번호'
            type='password'
            placeholder='비밀번호를 입력해 주세요.'
            value={vendorFormData.password}
            onChange={handleChangeValue}
            onKeyDown={handleKeyDown}
            classInput='relative'
            maxLength={16}
          />
        </div>
        <div className='text-text_grey text-sm  w-full flex justify-end cursor-pointer'>
          <span onClick={() => setIsShowIdModal(true)}>아이디 찾기</span>
          <span className='mx-1'>/</span>
          <span onClick={() => setIsShowPasswordModal(true)}>비밀번호 찾기</span>
        </div>
        <button
          tabIndex='-1'
          className={`font-700 px-4 py-3 text-white rounded-lg transition-all duration-200
                    bg-mint hover:bg-mint_hover`}>
          {isLoading ? (
            <div className='flex items-center justify-center'>
              <Spinner />
              <p>로그인 중...</p>
            </div>
          ) : (
            <p>로그인</p>
          )}
        </button>
        <div className='border border-ipt_disa w-full' />
        <div className='w-full flex justify-center text-xs'>
          <span>아직 회원이 아니신가요?</span>
          <span
            className='ml-2 font-800 text-mint cursor-pointer underline'
            onClick={() => {
              navigate('/signup');
            }}>
            아이디 만들기
          </span>
        </div>
      </form>
      <FindVendorIdModal
        isShowModal={isShowIdModal}
        setIsShowModal={setIsShowIdModal}
        setFindedId={setFindedId}
        setIsShowSuccessFindIdModal={setIsShowSuccessFindIdModal}
        axiosRequestAuthenticationNumber={axiosRequestAuthenticationNumber}
        time={time}
        setTime={setTime}
        icon={user}
        modalTitle={'아이디 찾기'}
      />
      <FindVendoPasswordModal
        isShowModal={isShowPasswordModal}
        setIsShowModal={setIsShowPasswordModal}
        setIsShowResetPasswordModal={setIsShowResetPasswordModal}
        axiosRequestAuthenticationNumber={axiosRequestAuthenticationNumber}
        setFindedId={setFindedId}
        time={time}
        setTime={setTime}
        icon={password}
        modalTitle={'비밀번호 찾기'}
      />
      <ResetPasswordModal
        isShowModal={isShowResetPasswordModal}
        setIsShowModal={setIsShowResetPasswordModal}
        findedId={findedId}
        icon={password}
        modalTitle={'비밀번호 재설정'}
      />
      <SuccessFindIdModal
        isShowModal={isShowSuccessFindIdModal}
        setIsShowModal={setIsShowSuccessFindIdModal}
        setVendorFormData={setVendorFormData}
        findedId={findedId}
        icon={user}
        modalTitle={'아이디확인'}
      />
    </div>
  );
};

export default LoginForm;
