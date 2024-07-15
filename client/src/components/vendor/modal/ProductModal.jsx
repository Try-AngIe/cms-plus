import BaseModal from '@/components/common/BaseModal';

const ProductModal = ({ isShowModal, setIsShowModal, modalTitle, productDetailData }) => {
  /*
  모달이 열리기 전에 데이터가 안들어오면 모달이 열리고
  아무 데이터도 안 됨 (데이터 조회 시간과 렌더링 시점의 차이)
  따라서 데이터 조회를 보장받고 이후에 렌더링 되게 함
  또한 위의 로직을 붙힐 시 상품 등록 모달은 최초에 렌더링이 안되므로
  상품 상세 정보만 위의 로직을 타게 만듬
  */
  if (modalTitle === '상품 상세 정보' && !productDetailData) {
    return <div />;
  }

  return (
    <BaseModal
      isShowModal={isShowModal}
      setIsShowModal={setIsShowModal}
      modalTitle={modalTitle}
      height='h-640'
      width='w-640'>
      <div className='flex flex-col justify-between gap-1.5'>
        <div className='mb-4 flex gap-4'>
          <div className='w-1/2'>
            <label className='block mb-2 font-semibold text-text_grey'>상품명</label>
            <input
              type='text'
              placeholder='상품명을 입력해주세요.'
              defaultValue={modalTitle === '상품 상세 정보' ? productDetailData.productName : ''}
              className='mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm placeholder-slate-400 shadow-sm placeholder:text-sm focus:border-mint focus:outline-none focus:ring-1 focus:ring-mint sm:text-sm'
            />
          </div>
          <div className='w-1/2' />
        </div>
        <div className='mb-4 flex gap-4'>
          <div className='w-1/2'>
            <label className='block mb-2 font-semibold text-text_grey'>금액(원)</label>
            <input
              type='text'
              placeholder='금액을 입력해주세요.'
              defaultValue={modalTitle === '상품 상세 정보' ? productDetailData.productPrice : ''}
              className='mt-1 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm placeholder-slate-400 shadow-sm placeholder:text-sm focus:border-mint focus:outline-none focus:ring-1 focus:ring-mint sm:text-sm'
            />
          </div>
          <div className='w-1/2'>
            <label className='block mb-2 font-semibold text-text_grey'>계약수(건)</label>
            <input
              type='text'
              placeholder={modalTitle === '상품 상세 정보' ? productDetailData.contractNum : ''} // 상품 상세 정보 페이지에선 계약수에 데이터 뿌려줘야 함
              readOnly // 고객은 계약수를 수정 할 수 없음
              className='mt-1 w-full rounded-md border border-slate-300  px-3 py-2 text-sm placeholder-slate-400 shadow-sm placeholder:text-sm bg-ipt_disa cursor-default'
              onMouseDown={e => e.preventDefault()} // 마우스 이벤트 방지
            />
          </div>
        </div>
        <div className='mb-4'>
          <label className='block mb-2 font-semibold text-text_grey'>비고</label>
          <textarea
            placeholder='메모를 남겨 주세요.'
            defaultValue={modalTitle === '상품 상세 정보' ? productDetailData.productMemo : ''}
            className='mt-1 w-full h-60 rounded-md border border-slate-300 bg-white px-3 py-2 text-sm placeholder-slate-400 shadow-sm textarea:text-sm focus:border-mint focus:outline-none focus:ring-1 focus:ring-mint sm:text-sm resize-none'
          />
        </div>
        <div className='flex justify-end gap-2 h-10'>
          {modalTitle === '상품 등록' ? (
            <>
              <button
                className='bg-white text-mint border border-mint px-7 py-2 rounded-md hover:bg-mint_hover hover:text-white h-10 w-50'
                onClick={() => setIsShowModal(false)}>
                <span>취소</span>
              </button>
              <button className='bg-mint text-white px-10 py-2 rounded-md hover:bg-mint_hover  flex flex-row items-center w-36 justify-center'>
                <span className='text-3xl mr-1 mb-1.5'>+</span>
                <span className='mr-1.5'>등록</span>
              </button>
            </>
          ) : (
            <>
              <button
                className='bg-white text-mint border border-mint px-7 py-2 rounded-md hover:bg-mint_hover hover:text-white h-10 w-50'
                onClick={() => setIsShowModal(false)}>
                <span>취소</span>
              </button>
              <button className='bg-mint text-white  px-7 py-2 rounded-md hover:bg-mint_hover h-10 w-32'>
                <span>삭제</span>
              </button>
              <button className='bg-mint text-white px-10 py-2 rounded-md hover:bg-mint_hover  flex flex-row items-center w-36 justify-center'>
                <span>저장</span>
              </button>
            </>
          )}
        </div>
      </div>
    </BaseModal>
  );
};

export default ProductModal;
