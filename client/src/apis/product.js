import { publicAxios } from '.';

// 상품 리스트 조회
export const getProductList = async (searchParams = {}) => {
  try {
    return await publicAxios.get('/v1/vendor/product', {
      params: {
        page: 1,
        size: 10,
        ...searchParams,
      },
    });
  } catch (err) {
    console.error('상품 목록 조회 실패 => ', err.response.data);
    throw err;
  }
};

// 상품 상세 조회
export const getProductDetail = async productId => {
  try {
    return await publicAxios.get(`/v1/vendor/product/${productId}`);
  } catch (err) {
    console.error('상품 상세 조회 실패 => ', err.response.data);
    throw err;
  }
};
