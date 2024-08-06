import { getBanks } from '@/apis/payment';

let bankCode = {};
let bankOptions = [{ value: '', label: '-- 선택 --' }];

// <---- 은행정보 요청 API ---->
const axiosBanks = async () => {
  try {
    const res = await getBanks();
    console.log('!----은행정보 요청 성공----!'); // 삭제예정
    const bankData = res.data;

    // bankCode 생성
    bankData.forEach(bank => {
      bankCode[bank.code] = bank.name;
    });

    // bankOptions 생성
    bankData.forEach(bank => {
      bankOptions.push({ value: bank.name, label: bank.title });
    });
  } catch (err) {
    console.error('axiosBanks:', err.response);
  }
};

// 은행 데이터를 불러오는 함수 호출
axiosBanks();

export { bankCode, bankOptions };
