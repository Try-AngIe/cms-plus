import { useState, useRef } from 'react';
import InfoRow from '@/components/common/InfoRow';
import SignatureCanvas from 'react-signature-canvas';
import { uploadSignature } from '@/apis/signature';

const Signature = ({ userData, setUserData, name }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadStatus, setUploadStatus] = useState('');
  const signatureRef = useRef();

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  const isSignatureEmpty = () => {
    return signatureRef.current && signatureRef.current.isEmpty();
  };

  const handleUploadSignature = async blob => {
    try {
      const formData = new FormData();
      formData.append('file', blob, 'signature.png');
      const fileUrl = await uploadSignature(formData);
      return fileUrl;
    } catch (error) {
      console.error('Error uploading signature:', error);
      throw error;
    }
  };

  const saveSignature = async () => {
    if (signatureRef.current) {
      if (isSignatureEmpty()) {
        alert('서명을 먼저 생성해주세요.');
        return;
      }

      const canvas = signatureRef.current.getCanvas();
      canvas.toBlob(async blob => {
        const url = URL.createObjectURL(blob);
        setUserData({
          ...userData,
          contractDTO: {
            ...userData.contractDTO,
            signatureUrl: url,
            signatureBlob: blob,
          },
        });

        try {
          setIsUploading(true);
          setUploadStatus('업로드 중...');
          const fileUrl = await handleUploadSignature(blob);
          if (fileUrl) {
            const link = document.createElement('a');
            link.href = fileUrl;
            link.download = 'signature.png';
            document.body.appendChild(link);
            document.body.removeChild(link);
            setUploadStatus('다운로드 완료!');
          }
        } catch (error) {
          console.error('Error saving signature:', error);
          setUploadStatus('서명 저장 중 오류가 발생했습니다.');
        } finally {
          setIsUploading(false);
          closeModal();
        }
      }, 'image/png');
    }
  };

  const clearSignature = () => {
    if (signatureRef.current) {
      signatureRef.current.clear();
    }
  };

  const productNames =
    userData.contractDTO.items && userData.contractDTO.items.length > 0
      ? userData.contractDTO.items.length > 1
        ? `${userData.contractDTO.items[0].productName} 외 ${userData.contractDTO.items.length - 1}`
        : userData.contractDTO.items[0].productName
      : '상품 정보 없음';

  const formatDate = dateString => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  };

  const bankNameMap = {
    SHINHAN: '신한은행',
    KB: '국민은행',
    WOORI: '우리은행',
    IBK: '기업은행',
    SUHYUP: '수협은행',
    NH: 'NH농협은행',
    BUSAN: '부산은행',
    HANA: '하나은행',
    GWANGJU: '광주은행',
    POST: '우체국',
    IM: 'iM뱅크',
    KNB: '경남은행',
  };

  const paymentInfo =
    userData.paymentDTO.paymentMethod === 'CARD'
      ? userData.paymentDTO.cardNumber
        ? `카드 ${userData.paymentDTO.cardNumber.slice(-4).padStart(16, '*')}`
        : '카드 정보 없음'
      : userData.paymentDTO.bank && userData.paymentDTO.accountNumber
        ? `${bankNameMap[userData.paymentDTO.bank] || userData.paymentDTO.bank} ${userData.paymentDTO.accountNumber
            .slice(-4)
            .padStart(userData.paymentDTO.accountNumber.length, '*')}`
        : '계좌 정보 없음';

  return (
    <div className='relative bg-white p-1'>
      <div className='w-full text-left'>
        <h3 className='mb-8 text-base font-semibold text-gray-700'>
          {name}님의
          <br />
          정보를 확인하고 서명해주세요.
        </h3>
      </div>
      <div className='mb-4 space-y-2 border-b border-t py-3'>
        <InfoRow label='상품' value={productNames} />
        <InfoRow label='합계금액' value={`${userData.contractDTO.totalPrice.toLocaleString()}원`} />
        <InfoRow
          label='기간'
          value={`${formatDate(userData.contractDTO.startDate)}~${formatDate(userData.contractDTO.endDate)}`}
        />
        <InfoRow label='약정일' value={`${userData.contractDTO.contractDay}일`} />
        <InfoRow label='결제수단' value={paymentInfo} />
      </div>
      <div className='mb-4 mt-8'>
        <label className='mb-1 block text-sm font-medium text-gray-700'>서명</label>
        <button
          onClick={openModal}
          className='flex h-32 w-full items-center justify-center rounded-md border border-mint text-gray-400'>
          {userData.contractDTO.signatureUrl ? (
            <img
              src={userData.contractDTO.signatureUrl}
              alt='서명'
              className='h-full w-full object-contain'
            />
          ) : (
            '서명하기'
          )}
        </button>
      </div>

      {isModalOpen && (
        <div className='fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50'>
          <div className='relative rounded-lg bg-white p-4'>
            <button
              onClick={closeModal}
              className='absolute right-2 top-2 text-gray-500 hover:text-gray-700'>
              ✕
            </button>
            <h3 className='mb-2 text-base font-semibold'>서명하기</h3>
            <SignatureCanvas
              ref={signatureRef}
              canvasProps={{
                width: 300,
                height: 150,
                className: 'border border-gray-300 rounded-lg',
              }}
            />
            <div className='mt-4 flex justify-end space-x-2'>
              <button onClick={clearSignature} className='rounded-lg bg-gray-200 px-4 py-2 text-sm'>
                지우기
              </button>
              <button
                onClick={saveSignature}
                disabled={isUploading}
                className={`rounded-lg ${
                  isUploading ? 'bg-gray-400' : 'bg-mint'
                } px-4 py-2 text-sm text-white`}>
                {isUploading ? '처리 중...' : '저장 및 다운로드'}
              </button>
            </div>
            {uploadStatus && (
              <p
                className={`mt-2 text-sm ${
                  uploadStatus.includes('실패') ? 'text-red-500' : 'text-green-500'
                }`}>
                {uploadStatus}
              </p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Signature;
