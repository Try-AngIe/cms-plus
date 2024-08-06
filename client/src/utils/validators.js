// 유효성 검사를 진행하는 함수

import { regex } from './regex';

// 생년월일 입력시, "2125-21-31"과 같은 유효하지 않은 날짜를 걸러내기 위함
export const isValidDate = dateString => {
  const [year, month, day] = dateString.split('-').map(Number);
  const date = new Date(year, month - 1, day);
  return (
    date && date.getFullYear() === year && date.getMonth() === month - 1 && date.getDate() === day
  );
};

// 카드 만료일 검사시, 현재 날짜 이후의 유효한 만료일인지 확인
export const isValidExpiryDate = expiryString => {
  const [month, year] = expiryString.split('/').map(Number);
  const expiryDate = new Date(2000 + year, month - 1);
  const today = new Date();
  return expiryDate > today;
};

// 카드 만료일 년도 검사
const isCardYearValid = cardYear => {
  console.log(cardYear);
  const currentYearLastTwoDigits = String(new Date().getFullYear()).substring(2);
  console.log(parseInt(cardYear, 10) >= parseInt(currentYearLastTwoDigits, 10));
  return parseInt(cardYear, 10) >= parseInt(currentYearLastTwoDigits, 10);
};

export const validateField = (id, value) => {
  switch (id) {
    case 'name': // 1.회원명 정규식
      return regex.name.test(value);

    case 'username': // 2.아이디 정규식
      return regex.username.test(value);

    case 'password': // 3.비밀번호 정규식
      return regex.password.test(value);

    case 'email': // 4.이메일 정규식
      return regex.email.test(value);

    case 'phone': // 5.휴대전화 정규식
      return regex.phone.test(value);

    case 'homePhone': // 6.유선전화 정규식
      return regex.homePhone.test(value);

    case 'productPrice': // 7. 금액 정규식
      return regex.productPrice.test(value);

    case 'contractNumber': // 8. 계약수
      return regex.contractNumber.test(value);

    case 'contractName': // 9. 계약명
      return regex.contractName.test(value);

    case 'accountNumber': // 10. 계좌번호
      return regex.accountNumber.test(value);

    case 'cardNumber': // 11. 카드번호
      return regex.cardNumber.test(value);

    case 'birth': // 12. 생년월일
      return regex.birth.test(value) && isValidDate(value);

    case 'cardMonth': // 13. 카드 유효기간 (월)
      return regex.cardMonth.test(value);

    case 'cardYear': // 14. 카드 유효기간 (년도)
      return regex.cardYear.test(value) && isCardYearValid(value);

    case 'expiryDate': // 15. 카드 만료일
      return regex.expiryDate.test(value) && isValidExpiryDate(value);

    default: // 기타. 빈값을 확인
      return value.trim() !== '';
  }
};
