// 유효성 검사에 사용되는 정규표현식

export const regex = {
  // <----- 고객, 회원 정규표현식 ----->

  // 1. 회원명
  name: /^[가-힣a-zA-Z0-9]{1,40}$/,

  // 2. 아이디
  username: /^[a-z0-9]{5,20}$/,

  // 3. 비밀번호
  password: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()\-_=+<>?]).{8,16}$/,

  // 4. 이메일
  email: /.+@.+\..+/,

  // 5. 휴대전화번호
  phone: /^01(?:0|1|[6-9])(?:\d{3}|\d{4})\d{4}$/,

  // 6. 유선전화번호
  homePhone: /^(02|0[3-6]{1}[1-5]{1})([0-9]{3,4})[0-9]{4}$/,

  // <----- 상품 목록 조회 search 정규표현식 ----->

  // 7. 금액
  productPrice: /^[0-9]+$/,

  // 8. 계약수
  contractNumber: /^[0-9]+$/,

  // <---------- 간편동의등록 모바일 계약정보에서 사용되는 것 ---------->

  // 9. 계약명
  contractName: /^.{1,32}$/,

  // <---------- 간편동의등록 모바일 결제정보에서 사용되는 것 ---------->

  // 10. 계좌번호
  accountNumber: /^\d{10,14}$/,

  // 11. 카드번호
  cardNumber: /^\d{16}$/,

  // 12. 생년월일
  birth: /^(\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/,

  // 13. 카드 유효기간 (월)
  cardMonth: /^(0?[1-9]|1[012])$/,

  // 14. 카드 유효기간 (년도)
  cardYear: /^\d{2}$/,

  // 15. 카드 만료일
  expiryDate: /^(0[1-9]|1[0-2])\/([0-9]{2})$/,
};
