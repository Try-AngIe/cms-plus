import { useNavigate } from 'react-router-dom';
import InputWeb from './common/InputWeb';

const SignupForm = () => {
  const navigate = useNavigate();

  const handleMoveLogin = () => {
    navigate('/login');
  };

  return (
    <div className='absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 h-full flex flex-col justify-around items-center py-16'>
      <p className='text-white font-900 text-3xl'>Hyosung CMS+</p>
      <div className='text-white font-700 text-sm flex flex-col items-center'>
        <p>안녕하세요 효성CMS+입니다.</p>
        <p>아이디 만들기는 효성 FMS의 미리 계약된 고객만</p>
        <p>진행하실 수 있습니다.</p>
      </div>
      <div className='h-580 w-680 shadow-modal bg-white rounded-xl w- p-6 flex flex-col items-center justify-around relative'>
        <p className='text-text_black text-xl  font-800'>아이디 만들기</p>
        <div className='w-full justify-between flex'>
          <InputWeb
            id='name'
            label='회원명'
            type='text'
            placeholder='회원명'
            required
            classContainer='w-1/2 mr-5'
            classLabel='text-sm'
            classInput='py-3'
          />
          <div className='w-1/2 flex items-end'>
            <InputWeb
              id='username'
              label='아이디'
              type='text'
              placeholder='아이디(영문, 숫자 조합 5~20)'
              required
              classContainer='w-full'
              classLabel='text-sm'
              classInput='py-3'
            />
            <button className='bg-btn_disa ml-3  w-32 rounded-lg text-white text-sm font-700 cursor-pointer h-46'>
              중복확인
            </button>
          </div>
        </div>
        <div className='w-full justify-between flex'>
          <InputWeb
            id='password'
            label='비밀번호'
            type='password'
            placeholder='비밀번호(숫자, 영문, 특수문자 조합 8 ~ 16)'
            required
            classContainer='w-1/2 mr-5'
            classLabel='text-sm'
            classInput='py-3'
          />
          <InputWeb
            id='password-check'
            label='비밀번호 확인'
            type='password'
            placeholder='비밀번호(숫자, 영문, 특수문자 조합 8 ~ 16)'
            required
            classContainer='w-1/2'
            classLabel='text-sm'
            classInput='py-3'
          />
        </div>
        <div className='w-full justify-between flex'>
          <InputWeb
            id='email'
            label='이메일'
            type='text'
            placeholder='ex) example@gmail.com'
            required
            classContainer='w-1/2 mr-5'
            classLabel='text-sm'
            classInput='py-3'
          />
          <InputWeb
            id='phone'
            label='휴대전화번호'
            type='number'
            placeholder='휴대전화번호'
            required
            classContainer='w-1/2'
            classLabel='text-sm'
            classInput='py-3'
          />
        </div>
        <div className='w-full justify-between flex'>
          <InputWeb
            id='department'
            label='부서명'
            type='text'
            placeholder='부서명'
            required
            classContainer='w-1/2 mr-5'
            classLabel='text-sm'
            classInput='py-3'
          />
          <InputWeb
            id='homePhone'
            label='유선전화번호'
            type='number'
            placeholder='유선전화번호'
            classContainer='w-1/2'
            classLabel='text-sm'
            classInput='py-3'
          />
        </div>

        <div className='text-sm font-700'>
          <span className='text-text_black'>이미 아이디가 있으신가요? </span>
          <span
            className='text-mint underline cursor-pointer'
            onClick={() => {
              handleMoveLogin();
            }}>
            로그인
          </span>
        </div>
        <button className='bg-btn_disa px-6 py-3 rounded-lg text-white text-sm font-700 absolute right-6 bottom-8 cursor-pointer'>
          아이디 만들기
        </button>
      </div>
    </div>
  );
};

export default SignupForm;
