import { create } from 'zustand';

export const useMemberPaymentStore = create(set => ({
  // <----- 결제 방식 정보 ----->
  paymentType: 'AUTO',

  // 결제 방식 설정
  setPaymentType: type =>
    set({
      paymentType: type,
    }),

  // 결제 방식 초기화
  resetPaymentType: () =>
    set({
      paymentType: 'AUTO',
    }),

  // <----- 결제 방식 - 가상계좌 ----->
  paymentTypeInfoReq_Virtual: {
    bank: '',
    accountOwner: '',
  },

  // 데이터 하나씩 set
  setPaymentTypeInfoReq_Virtual: data =>
    set(state => ({
      paymentTypeInfoReq_Virtual: {
        ...state.paymentTypeInfoReq_Virtual,
        ...data,
      },
    })),

  // 데이터 Reset
  resetPaymentTypeInfoReq_Virtual: () =>
    set({
      paymentTypeInfoReq_Virtual: {
        bank: '',
        accountOwner: '',
      },
    }),

  // <----- 결제 방식 - 납부자결제 ----->
  paymentTypeInfoReq_Buyer: {
    availableMethods: [],
  },

  // 전체 결제 수단을 설정
  setAvailableMethods: methods =>
    set(state => ({
      paymentTypeInfoReq_Buyer: {
        ...state.paymentTypeInfoReq_Buyer,
        availableMethods: methods,
      },
    })),

  // 데이터 Reset
  resetPaymentTypeInfoReq_Buyer: () =>
    set({
      paymentTypeInfoReq_Buyer: {
        availableMethods: [],
      },
    }),

  // <----- 결제 방식 - 자동결제 ----->
  paymentTypeInfoReq_Auto: {
    consentImgUrl: '',
    simpleConsentReqDateTime: '',
    consetImgName: '',
  },

  // 데이터 하나씩 set
  setPaymentTypeInfoReq_Auto: data =>
    set(state => ({
      paymentTypeInfoReq_Auto: {
        ...state.paymentTypeInfoReq_Auto,
        ...data,
      },
    })),

  // 데이터 Reset
  resetPaymentTypeInfoReq_Auto: () =>
    set({
      paymentTypeInfoReq_Auto: {
        consentImgUrl: '',
        simpleConsentReqDateTime: '',
        consetImgName: '',
      },
    }),

  // <----- 결제 수단 정보 ----->
  paymentMethod: 'CMS',

  // 결제 수단 설정
  setPaymentMethod: method =>
    set({
      paymentMethod: method,
    }),

  // 결제 수단 초기화
  resetPaymentMethod: () =>
    set({
      paymentMethod: 'CMS',
    }),

  // <----- 결제 수단 - CMS ----->
  paymentMethodInfoReq_Cms: {
    bank: '',
    accountNumber: '',
    accountOwner: '',
    accountOwnerBirth: '',
  },

  // 데이터 하나씩 set
  setPaymentMethodInfoReq_Cms: data =>
    set(state => ({
      paymentMethodInfoReq_Cms: {
        ...state.paymentMethodInfoReq_Cms,
        ...data,
      },
    })),

  // 데이터 Reset
  resetPaymentMethodInfoReq_Cms: () =>
    set({
      paymentMethodInfoReq_Cms: {
        bank: '',
        accountNumber: '',
        accountOwner: '',
        accountOwnerBirth: '',
      },
    }),

  // <----- 결제 방식 - 카드 ----->
  paymentMethodInfoReq_Card: {
    cardNumber: '',
    cardMonth: '',
    cardYear: '',
    cardOwner: '',
    cardOwnerBirth: '',
  },

  // 데이터 하나씩 set
  setPaymentMethodInfoReq_Card: data =>
    set(state => ({
      paymentMethodInfoReq_Card: {
        ...state.paymentMethodInfoReq_Card,
        ...data,
      },
    })),

  // 데이터 Reset
  resetPaymentMethodInfoReq_Card: () =>
    set({
      paymentMethodInfoReq_Card: {
        cardNumber: '',
        cardMonth: '',
        cardYear: '',
        cardOwner: '',
        cardOwnerBirth: '',
      },
    }),

  // <----- 간편성명동의 체크여부 ----->
  isSimpConsentCheck: 'true',

  // boolean 값 변경
  setIsSimpConsentCheck: data =>
    set({
      isSimpConsentCheck: !data,
    }),
}));
