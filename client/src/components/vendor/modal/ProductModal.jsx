import { useEffect, useState } from 'react';
import BaseModal from '@/components/common/BaseModal';
import { createProduct, deleteProduct, updateProduct } from '@/apis/product';
import InputWeb from '@/components/common/inputs/InputWeb';
import useAlert from '@/hooks/useAlert';
import useConfirm from '@/hooks/useConfirm';
import TextArea from '@/components/common/inputs/TextArea';

const ProductModal = ({
  icon,
  isShowModal,
  setIsShowModal,
  modalTitle,
  productDetailData,
  refreshProductList,
}) => {
  const [productName, setProductName] = useState('');
  const [productPrice, setProductPrice] = useState('');
  const [productMemo, setProductMemo] = useState('');
  const [contractNumber, setContractNumber] = useState('');
  const onAlert = useAlert();
  const onConfirm = useConfirm();

  // 모달이 열릴 때마다 초기 상태 설정
  useEffect(() => {
    if (modalTitle === '상품 등록') {
      setProductName(''); // 빈값
      setProductPrice('');
      setProductMemo('');
    } else if (modalTitle === '상품 상세 정보' && productDetailData) {
      setProductName(productDetailData.productName); // 기존 데이터값
      setProductPrice(productDetailData.productPrice);
      setProductMemo(productDetailData.productMemo);
    }
  }, [isShowModal, modalTitle, productDetailData]);

  // 상품 등록 이벤트핸들러
  const handleCreateProduct = async () => {
    const productData = {
      productName,
      productPrice,
      productMemo,
    };

    try {
      await createProduct(productData);
      onAlert({
        msg: `"${productName}"상품을 성공적으로 등록했습니다!`,
        type: 'success',
        title: '등록 성공',
      });
      setIsShowModal(false); // 상품 등록 후 모달 닫기
      refreshProductList(); // 등록 후 상품 목록 리렌더링
    } catch (err) {
      onAlert({
        msg: '상품 등록에 실패했습니다.',
        type: 'error',
        title: '등록 실패',
      });
      console.error('axiosProductCreate => ', err.response);
    }
  };

  // 상품 수정 이벤트핸들러
  const handleUpdateProduct = async productId => {
    const productData = {
      productPrice,
      productMemo,
    };

    try {
      await updateProduct(productDetailData.productId, productData);
      onAlert({
        msg: '상품을 수정했습니다.',
        type: 'success',
        title: '수정 성공',
      });
      setIsShowModal(false);
      refreshProductList();
    } catch (err) {
      onAlert({
        msg: '상품 수정에 실패했습니다.',
        type: 'error',
        title: '수정 실패',
      });
      console.error('axiosProductUpdate => ', err.response);
    }
  };

  const handleDeleteProduct = async () => {
    const isConfirmed = await onConfirm({
      msg: `"${productDetailData.productName}" 상품을 삭제 하시겠습니까?`,
      type: 'warning',
      title: '삭제 확인',
    });

    if (isConfirmed) {
      try {
        await deleteProduct(productDetailData.productId);
        onAlert({
          msg: `"${productDetailData.productName}" 상품을 삭제했습니다.`,
          type: 'success',
          title: '삭제 성공',
        });
        setIsShowModal(false);
        refreshProductList();
      } catch (err) {
        onAlert({
          msg: '상품 삭제에 실패했습니다.',
          type: 'error',
          title: '삭제 실패',
          err: err,
        });
        console.error('axiosProductDelete => ', err.response);
      }
    }
  };

  // 모달이 열리면서 동시에 api 요청하는데, 모달이 열릴때까지 api 응답이 안 도착하는 경우가 있음
  // api 응답 와야 모달 열리게 보장
  if (modalTitle === '상품 상세 정보' && !productDetailData) {
    return <div />;
  }

  return (
    <BaseModal
      isShowModal={isShowModal}
      setIsShowModal={setIsShowModal}
      modalTitle={modalTitle}
      icon={icon}
      height='h-640'
      width='w-640'
      close={true}>
      <div className='relative flex flex-col h-full  gap-1.5'>
        <div className='mb-4 flex gap-4'>
          <div className='w-1/2'>
            <InputWeb
              label='상품명'
              id='productName'
              type='text'
              maxLength={15}
              placeholder='상품명을 입력해주세요.'
              value={productName}
              readOnly={modalTitle === '상품 상세 정보'}
              tabIndex={modalTitle === '상품 상세 정보' ? '-1' : '0'}
              onChange={e => setProductName(e.target.value)}
              classInput={
                modalTitle === '상품 상세 정보'
                  ? 'mt-1 w-full rounded-md border border-slate-300 px-3 py-3 text-sm placeholder-slate-400 shadow-sm placeholder:text-sm bg-ipt_disa cursor-default'
                  : 'mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-3 text-sm placeholder-slate-400 shadow-sm placeholder:text-sm focus:border-mint focus:outline-none focus:ring-1 focus:ring-mint sm:text-sm'
              }
              onMouseDown={modalTitle === '상품 상세 정보' ? e => e.preventDefault() : undefined}
            />
          </div>
          <div className='w-1/2' />
        </div>
        <div className='mb-4 flex gap-4'>
          <InputWeb
            label='금액(원)'
            id='productPrice'
            type='text'
            placeholder='금액을 입력해주세요.'
            maxLength={6}
            value={productPrice}
            onChange={e => setProductPrice(e.target.value.replace(/\D/g, ''))}
            classContainer='w-1/2'
            classInput='mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-3 text-sm placeholder-slate-400 shadow-sm placeholder:text-sm focus:border-mint focus:outline-none focus:ring-1 focus:ring-mint sm:text-sm'
          />
          <div className='w-1/2'>
            <InputWeb
              label='계약수(건)'
              id='contractNumber'
              type='text'
              placeholder={modalTitle === '상품 상세 정보' ? productDetailData.contractNumber : ''}
              value={contractNumber}
              readOnly
              tabIndex='-1'
              classInput='mt-1 w-full rounded-md border border-slate-300 px-3 py-3 text-sm placeholder-slate-400 shadow-sm placeholder:text-sm bg-ipt_disa cursor-default'
              onMouseDown={e => e.preventDefault()}
            />
          </div>
        </div>
        <div className='mb-4'>
          <TextArea
            label='비고'
            placeholder={
              modalTitle === '상품 상세 정보'
                ? productDetailData.productMemo
                : '메모를 남겨 주세요.'
            }
            value={productMemo}
            onChange={e => setProductMemo(e.target.value)}
            className='mt-1 w-full h-60 rounded-md border border-slate-300 bg-white px-3 py-2 text-sm placeholder-slate-400 shadow-sm textarea:text-sm focus:border-mint focus:outline-none focus:ring-1 focus:ring-mint sm:text-sm resize-none'
          />
        </div>
        <div className='absolute bottom-0 right-0 flex justify-end gap-2 h-10 font-700'>
          {modalTitle === '상품 등록' ? (
            <>
              <button
                className='bg-white text-mint border px-7 py-2 rounded-md hover:bg-mint_hover_light  h-10 w-50 border-mint transition-all duration-200'
                onClick={() => setIsShowModal(false)}>
                <span>취소</span>
              </button>
              <button
                className='bg-mint text-white px-10 py-2 rounded-md hover:bg-mint_hover flex flex-row items-center w-36 justify-center transition-all duration-200 border border-white'
                onClick={handleCreateProduct}>
                <span>등록</span>
              </button>
            </>
          ) : (
            <>
              <button
                className='bg-white text-mint border border-mint px-7 py-2 rounded-md hover:bg-mint_hover h-10 w-50'
                onClick={() => setIsShowModal(false)}>
                <span>취소</span>
              </button>
              <button
                className='bg-mint text-white px-7 py-2 rounded-md hover:bg-mint_hover h-10 w-28'
                onClick={handleDeleteProduct}>
                <span>삭제</span>
              </button>
              <button
                className='bg-mint text-white px-10 py-2 rounded-md hover:bg-mint_hover flex flex-row items-center w-36 justify-center'
                onClick={handleUpdateProduct}>
                <span>수정</span>
              </button>
            </>
          )}
        </div>
      </div>
    </BaseModal>
  );
};

export default ProductModal;
