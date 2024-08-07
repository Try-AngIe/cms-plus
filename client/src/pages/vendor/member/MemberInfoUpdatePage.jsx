import { updateMemberBaic } from '@/apis/member';
import BasicInfoForm from '@/components/common/memberForm/BasicInfoForm';
import ProgressBar from '@/components/common/ProgressBar';
import useAlert from '@/hooks/useAlert';
import { useMemberBasicStore } from '@/stores/useMemberBasicStore';
import { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

const MemberInfoUpdatePage = () => {
  const { basicInfo, resetBasicInfo } = useMemberBasicStore();
  const navigate = useNavigate();
  const memberId = useParams();
  const onAlert = useAlert();

  // <-----기본 정보 수정 API ----->
  const axiosUpdateMemberBasic = async () => {
    try {
      await updateMemberBaic(memberId.id, basicInfo);
      console.log('!----기본정보 수정 성공----!'); // 삭제예정
      navigate(`/vendor/members/detail/${memberId.id}`);
      onAlert({
        msg: '회원정보가 수정되었습니다!',
        type: 'success',
        title: '회원 수정 성공',
      });
    } catch (err) {
      console.error('axiosUpdateMemberBasic => ', err.response);
      onAlert({
        msg: '회원 수정에 실패했습니다.',
        type: 'error',
        title: '수정 실패',
        err: err,
      });
    }
  };

  // <----- 회원정보 zustand reset ----->
  useEffect(() => {
    return () => {
      resetBasicInfo();
    };
  }, []);

  return (
    <>
      <div className='up-dashboard relative flex justify-center items-center mb-4 w-full desktop:h-[18%]'>
        <ProgressBar steps={['기본정보']} />
      </div>
      <div className='primary-dashboard flex flex-col relative h-[1000px] large_desktop:h-[80%] '>
        <div className='flex items-center h-[50px] px-2 pb-[10px] '>
          <p className='text-text_black text-xl font-800'>기본정보</p>
        </div>
        <BasicInfoForm formType='UPDATE' />
        <div className='absolute bottom-0 left-0 flex h-[65px] w-full justify-end px-7 pb-5 font-800 text-lg '>
          <button
            className=' px-10 py-2 border border-mint rounded-lg text-mint'
            onClick={() => navigate(-1)}>
            취소
          </button>
          <button
            className=' px-10 py-2 bg-mint rounded-lg text-white transition-all duration-200 hover:bg-mint_hover ml-3'
            onClick={axiosUpdateMemberBasic}>
            저장
          </button>
        </div>
      </div>
    </>
  );
};

export default MemberInfoUpdatePage;
