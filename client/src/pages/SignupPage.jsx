import SignupForm from '@/components/SignupForm';

const SignupPage = () => {
  return (
    <div className='p-3 relative'>
      <img className='h-[40vh] w-[100vw]' src='/src/assets/backgroundImgUp.png' alt='background' />
      <SignupForm />
    </div>
  );
};

export default SignupPage;
