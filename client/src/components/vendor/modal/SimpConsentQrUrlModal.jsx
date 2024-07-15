import React, { useState, useRef, useEffect } from 'react';
import BaseModal from '@/components/common/BaseModal';
import QRCode from 'qrcode.react';

const SimpConsentQrUrlModal = ({ isShowModal, setIsShowModal, modalTitle }) => {
  const [url, setUrl] = useState('https://google.com');
  const [phoneNumber, setPhoneNumber] = useState('01033388044');
  const qrRef = useRef(null);

  // URL이 변경될 때마다 QR 코드를 업데이트
  useEffect(() => {
    if (qrRef.current) {
      const canvas = qrRef.current.querySelector('canvas');
      QRCode.toCanvas(canvas, url, { width: 200 }, error => {
        if (error) console.error('Error generating QR code', error);
      });
    }
  }, [url]);

  const handleCopyUrl = () => {
    navigator.clipboard.writeText(url).then(() => {
      alert('URL이 클립보드에 복사되었습니다.');
    });
  };

  const handleSendLink = () => {
    alert('링크가 전송되었습니다.');
  };

  const handleDownloadQR = () => {
    if (qrRef.current) {
      const canvas = qrRef.current.querySelector('canvas');
      const image = canvas.toDataURL('image/png');
      const link = document.createElement('a');
      link.href = image;
      link.download = 'qrcode.png';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  };

  return (
    <BaseModal
      isShowModal={isShowModal}
      setIsShowModal={setIsShowModal}
      modalTitle={modalTitle}
      height={'h-640'}
      width={'w-480'}>
      <div className='flex flex-col items-center p-2'>
        <div ref={qrRef} className='mb-4 rounded border border-gray-200 p-2'>
          <QRCode value={url} size={200} />
        </div>
        <button
          onClick={handleDownloadQR}
          className='mb-4 w-full rounded-lg bg-mint py-2 font-semibold text-white'>
          QR 다운로드
        </button>

        <div className='mb-4 w-full'>
          <label className='mb-2 block text-sm font-medium text-gray-700'>URL</label>
          <div className='flex flex-col sm:flex-row'>
            <input
              type='text'
              value={url}
              onChange={e => setUrl(e.target.value)}
              className='mb-2 flex-grow rounded-lg border border-gray-300 px-3 py-2 sm:mb-0 sm:mr-2'
            />
            <div className='flex-shrink-0 sm:w-36'>
              <button
                onClick={handleCopyUrl}
                className='w-full whitespace-nowrap rounded-lg bg-mint px-4 py-2 text-white'>
                URL 링크 복사
              </button>
            </div>
          </div>
        </div>

        <div className='w-full'>
          <label className='mb-2 block text-sm font-medium text-gray-700'>연락처</label>
          <div className='flex flex-col sm:flex-row'>
            <input
              type='text'
              value={phoneNumber}
              onChange={e => setPhoneNumber(e.target.value)}
              className='mb-2 flex-grow rounded-lg border border-gray-300 px-3 py-2 sm:mb-0 sm:mr-2'
            />
            <div className='flex-shrink-0 sm:w-36'>
              <button
                onClick={handleSendLink}
                className='w-full whitespace-nowrap rounded-lg bg-mint px-4 py-2 text-white'>
                링크 전송
              </button>
            </div>
          </div>
        </div>
      </div>
    </BaseModal>
  );
};

export default SimpConsentQrUrlModal;
