import BaseModal from '@/components/common/BaseModal';

const MemberChooseModal = ({ isShowModal, setIsShowModal, modalTitle }) => {
  return (
    <BaseModal
      isShowModal={isShowModal}
      setIsShowModal={setIsShowModal}
      modalTitle={modalTitle}
      height={'h-640'}
      width={'w-640'}>
      이부분에 내용 채우기
    </BaseModal>
  );
};

export default MemberChooseModal;
