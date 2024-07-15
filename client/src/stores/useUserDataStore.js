import { create } from 'zustand';

export const useUserDataStore = create((set, get) => ({
  userData: {
    // 기본 정보
    name: '',
    mobile: '',
    phone: '',
    email: '',
    zipcode: '',
    address: '',
    addressDetail: '',
    // 계약 정보
    selectedProduct: '상품명1(3,000원)',
    items: [{ name: '상품명1(3,000원)', quantity: 1, price: 3000 }],
    startDate: '2024-06-31',
    endDate: '2024-07-01',
    paymentDay: 1,
    totalPrice: 3000,

    // 결제 정보
    paymentMethod: 'card',
    cardNumber: '',
    expiryDate: '',
    cardHolder: '',
    cardBirthDate: '',
    bank: 'shinhan',
    accountHolder: '',
    accountBirthDate: '',
    accountNumber: '',

    // 서명 정보 추가
    signatureUrl: '',
  },
  setUserData: data =>
    set(state => {
      const newUserData = { ...state.userData, ...data };
      if (data.items) {
        newUserData.totalPrice = newUserData.items.reduce(
          (sum, item) => sum + item.quantity * item.price,
          0
        );
      }
      return { userData: newUserData };
    }),
  resetUserData: () =>
    set(state => ({
      userData: {
        ...state.userData,
        name: '',
        mobile: '',
        phone: '',
        email: '',
        zipcode: '',
        address: '',
        addressDetail: '',
        selectedProduct: '상품명1(3,000원)',
        items: [{ name: '상품명1(3,000원)', quantity: 1, price: 3000 }],
        startDate: '2024-06-31',
        endDate: '2024-07-01',
        paymentDay: 1,
        totalPrice: 3000,
        paymentMethod: 'card',
        cardNumber: '',
        expiryDate: '',
        cardHolder: '',
        cardBirthDate: '',
        bank: '',
        accountHolder: '',
        accountBirthDate: '',
        accountNumber: '',
        signatureUrl: '',
      },
    })),
  clearPaymentInfo: () =>
    set(state => ({
      userData: {
        ...state.userData,
        cardNumber: '',
        expiryDate: '',
        cardHolder: '',
        cardBirthDate: '',
        bank: '',
        accountHolder: '',
        accountBirthDate: '',
        accountNumber: '',
      },
    })),
}));
