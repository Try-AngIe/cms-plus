import Input from '@/components/common/Input';

const CardInfo = () => {
  return (
    <div>
      <h3 className='mb-8 text-base font-semibold text-gray-700'>
        회원님의
        <br />
        결제카드 정보를 입력해주세요.
      </h3>

      <div className='flex flex-col bg-white p-1'>
        <form className='space-y-4'>
          <Input
            label='카드사'
            name='cardNumber'
            type='text'
            required
            placeholder='신한카드'
            disabled
            class='mt-1 block w-full px-3 py-2 bg-white border border-slate-300 rounded-md text-sm shadow-sm placeholder-slate-400
          focus:outline-none disabled:bg-slate-50 disabled:text-slate-500 disabled:border-slate-200 disabled:shadow-none'
          />
          <Input
            label='카드번호'
            name='cardNumber'
            type='text'
            required
            placeholder="'-' 없이 숫자만 입력해주세요"
          />
          <Input label='유효기간' name='expiryDate' type='text' required placeholder='MM/YY' />
          <Input label='명의자' name='cardHolder' type='text' required placeholder='최대 15자리' />
          <Input
            label='생년월일'
            name='cardBirthDate'
            type='text'
            required
            placeholder='생년월일 6자리'
          />
        </form>
        <button className='mt-4 w-full rounded-lg border border-teal-400 bg-white py-2 text-sm font-normal text-teal-400 transition-colors hover:bg-teal-50'>
          카드 인증하기
        </button>
      </div>
    </div>
  );
};

export default CardInfo;
