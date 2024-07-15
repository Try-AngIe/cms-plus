import React, { useState, useRef } from 'react';
import axios from 'axios';
import InfoRow from '@/components/common/InfoRow';
import SignatureCanvas from 'react-signature-canvas';
import { useUserDataStore } from '@/stores/useUserDataStore';

const Signature = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadStatus, setUploadStatus] = useState('');
  const signatureRef = useRef();
  const { userData, setUserData } = useUserDataStore();

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  const isSignatureEmpty = () => {
    return signatureRef.current && signatureRef.current.isEmpty();
  };

  const saveSignature = () => {
    if (signatureRef.current) {
      if (isSignatureEmpty()) {
        alert('서명을 먼저 생성해주세요.');
        return;
      }

      const canvas = signatureRef.current.getCanvas();
      canvas.toBlob(blob => {
        const url = URL.createObjectURL(blob);
        setUserData({ ...userData, signatureUrl: url, signatureBlob: blob });
        closeModal();
      }, 'image/png');
    }
  };

  const uploadSignature = async blob => {
    setIsUploading(true);
    setUploadStatus('업로드 중...');
    try {
      const formData = new FormData();
      formData.append('file', blob, 'signature.png');

      const response = await axios.post('http://localhost:8080/api/v1/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      setUploadStatus('업로드 성공!');
      return response.data.fileUrl; // 서버에서 반환된 파일 URL
    } catch (error) {
      console.error('Error uploading signature:', error);
      setUploadStatus('업로드 실패. 다시 시도해주세요.');
      return null;
    } finally {
      setIsUploading(false);
    }
  };

  const saveSignatureAsPNG = async () => {
    if (userData.signatureBlob) {
      try {
        const fileUrl = await uploadSignature(userData.signatureBlob);
        if (fileUrl) {
          const link = document.createElement('a');
          link.href = fileUrl;
          link.download = 'signature.png';
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          setUploadStatus('다운로드 완료!');
        }
      } catch (error) {
        console.error('Error saving signature:', error);
        setUploadStatus('서명 저장 중 오류가 발생했습니다.');
      }
    } else {
      setUploadStatus('서명을 먼저 생성해주세요.');
    }
  };

  const clearSignature = () => {
    if (signatureRef.current) {
      signatureRef.current.clear();
    }
  };

  const productNames =
    userData.items.length > 1
      ? `${userData.items[0].name} 외 ${userData.items.length - 1}`
      : userData.items[0].name;

  const formatDate = dateString => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  };

  const bankNameMap = {
    shinhan: '신한은행',
    kb: '국민은행',
    woori: '우리은행',
    ibk: '기업은행',
    suhyup: '수협은행',
    nh: 'NH농협은행',
    busan: '부산은행',
    hana: '하나은행',
    gwangju: '광주은행',
    post: '우체국',
    im: 'iM뱅크',
    knb: '경남은행',
  };

  const paymentInfo =
    userData.paymentMethod === 'card'
      ? `카드 ${userData.cardNumber.slice(-4).padStart(16, '*')}`
      : `${bankNameMap[userData.bank] || userData.bank} ${userData.accountNumber.slice(-4).padStart(userData.accountNumber.length, '*')}`;

  return (
    <div className='relative bg-white p-1'>
      <div className='w-full text-left'>
        <h3 className='mb-8 text-base font-semibold text-gray-700'>
          회원님의
          <br />
          정보를 확인하고 서명해주세요.
        </h3>
      </div>
      <div className='mb-4 space-y-2 border-b border-t py-3'>
        <InfoRow label='상품' value={productNames} />
        <InfoRow label='합계금액' value={`${userData.totalPrice.toLocaleString()}원`} />
        <InfoRow
          label='기간'
          value={`${formatDate(userData.startDate)}~${formatDate(userData.endDate)}`}
        />
        <InfoRow label='약정일' value={`${userData.paymentDay}일`} />
        <InfoRow label='결제수단' value={paymentInfo} />
      </div>
      <div className='mb-4 mt-8'>
        <label className='mb-1 block text-sm font-medium text-gray-700'>서명</label>
        <button
          onClick={openModal}
          className='flex h-32 w-full items-center justify-center rounded-md border border-mint text-gray-400'>
          {userData.signatureUrl ? (
            <img src={userData.signatureUrl} alt='서명' className='h-full w-full object-contain' />
          ) : (
            '서명하기'
          )}
        </button>
      </div>

      {isModalOpen && (
        <div className='fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50'>
          <div className='rounded-lg bg-white p-4'>
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
                className='rounded-lg bg-mint px-4 py-2 text-sm text-white'>
                저장하기
              </button>
              <button onClick={closeModal} className='rounded-lg bg-gray-200 px-4 py-2 text-sm'>
                취소
              </button>
            </div>
          </div>
        </div>
      )}

      {userData.signatureUrl && (
        <div>
          <button
            onClick={saveSignatureAsPNG}
            disabled={isUploading}
            className={`mt-4 w-full rounded-lg ${isUploading ? 'bg-gray-400' : 'bg-mint'} px-4 py-2 text-sm text-white`}>
            {isUploading ? '업로드 중...' : '서명 이미지 다운로드'}
          </button>
          {uploadStatus && (
            <p
              className={`mt-2 text-sm ${uploadStatus.includes('실패') ? 'text-red-500' : 'text-green-500'}`}>
              {uploadStatus}
            </p>
          )}
        </div>
      )}
    </div>
  );
};

export default Signature;
