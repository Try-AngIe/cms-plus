import LoginForm from '@/components/LoginForm';

const LoginPage = () => {
  return (
    <>
      <LoginForm />
      <img
        className=' absolute right-0 top-0 h-[88vh] w-[44vw] hidden mobile:block'
        src='/src/assets/backgroundImgSide.png'
        alt='background'
      />
    </>
  );
};

export default LoginPage;
