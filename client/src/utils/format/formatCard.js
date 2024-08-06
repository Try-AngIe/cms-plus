// <----- 카드 정보 관련 포멧팅 함수 ----->

// 카드 년도 포맷팅 함수 (저장용)
export const formatCardYearForStorage = year => {
  year = String(year);
  if (!year || year.length !== 2) return '';
  return `20${year}`;
};

// 카드 년도 포맷팅 함수 (표시용)
export const formatCardYearForDisplay = year => {
  year = String(year);
  console.log(year);
  if (!year || year.length !== 4) return year;
  return String(year).substring(2);
};

// 카드 월 포맷팅 함수 (표시용)
export const formatCardMonthForDisplay = month => {
  month = String(month);
  if (!month || month.length !== 3) return month;
  return String(month).substring(2);
};

// 카드 유효기간 포멧팅 함수
export const formatExpiryDate = value => {
  const cleaned = value.replace(/\D/g, '');
  let formatted = cleaned;

  if (cleaned.length >= 2) {
    formatted = `${cleaned.slice(0, 2)}/${cleaned.slice(2)}`;
  }

  return formatted.slice(0, 5);
};

// 카드번호 포멧팅 함수 (표시용)
export const formatCardNumber = value => {
  const cleaned = value.replace(/\D/g, '');
  const chunks = [];

  for (let i = 0; i < cleaned.length; i += 4) {
    chunks.push(cleaned.slice(i, i + 4));
  }

  return chunks.join('-').slice(0, 19);
};

// 카드번호 포멧 제거 함수 (저장용)
export const unformatCardNumber = formattedNumber => {
  return formattedNumber.replace(/-/g, '');
};
