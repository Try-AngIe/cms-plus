import BaseModal from '@/components/common/BaseModal';

const BillingRegisterModal = ({ isShowModal, setIsShowModal, modalTitle }) => {
  return (
    <BaseModal
      isShowModal={isShowModal}
      setIsShowModal={setIsShowModal}
      modalTitle={modalTitle}
      height={'h-5/6'}
      width={'w-5/6'}>
      이부분에 내용 채우기
    </BaseModal>
  );
};

export default BillingRegisterModal;
