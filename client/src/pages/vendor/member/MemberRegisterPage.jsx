import BasicInfoForm from '@/components/common/memberForm/BasicInfoForm';
import ProgressBar from '@/components/common/ProgressBar';
import useAlert from '@/hooks/useAlert';
import { useNavigate } from 'react-router-dom';
import close from '@/assets/close.svg';
import { useMemberBasicStore } from '@/stores/useMemberBasicStore';
import { postCreateMemberBasic } from '@/apis/member';
import { useEffect } from 'react';

const MemberRegisterPage = () => {
  const navigate = useNavigate();
  const onAlert = useAlert();

  const { basicInfo, resetBasicInfo } = useMemberBasicStore();

  // <----- 회원 등록 API ----->
  const axiosCreateMemberBasic = async () => {
    try {
      const res = await postCreateMemberBasic(basicInfo);

      // validation 체크
      // if (!validateBasicInfo()) return;

      console.log('!----회원등록 성공----!'); // 삭제예정
      await navigate('/vendor/members');
      onAlert({ msg: '회원정보가 등록되었습니다!', type: 'success' });
    } catch (err) {
      onAlert({ err });
      console.error('axiosCreateMember => ', err.response);
    }
  };

  // <----- 페이지 입장과 이탈 시 reset ----->
  useEffect(() => {
    return () => {
      resetBasicInfo();
    };
  }, []);

  return (
    <>
      <div className='up-dashboard relative flex justify-center items-center mb-4 w-full desktop:h-[18%]'>
        <ProgressBar steps={['기본정보']} />
        <img
          src={close}
          alt='back'
          className='absolute right-6 top-6 cursor-pointer w-4 h-4'
          onClick={() => navigate('/vendor/members')}
        />
      </div>
      <div className='primary-dashboard flex flex-col relative h-[1000px] large_desktop:h-[80%] '>
        <div className='flex items-center h-[50px] px-2 pb-[10px] '>
          <p className='text-text_black text-xl font-800'>기본정보</p>
        </div>
        <BasicInfoForm formType={'CREATE'} />
        <div className='absolute bottom-0 left-0 flex h-[65px] w-full justify-end px-7 pb-5 font-800 text-lg '>
          <button
            className=' px-10 py-2 border border-mint rounded-lg text-mint hover:bg-mint_hover_light'
            onClick={() => navigate('/vendor/members')}>
            취소
          </button>
          <button
            className=' px-10 py-2 bg-mint rounded-lg text-white transition-all duration-200 hover:bg-mint_hover ml-3'
            onClick={axiosCreateMemberBasic}>
            저장
          </button>
        </div>
      </div>
    </>
  );
};

export default MemberRegisterPage;
