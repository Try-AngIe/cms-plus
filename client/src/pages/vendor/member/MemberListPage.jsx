import Table from '@/components/common/Table';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
const cols = [
  'No',
  '회원이름',
  '휴대전화',
  '약정일',
  '상품',
  '계약금액',
  '계약상태',
  '간편동의여부',
];

const items = [
  {
    No: '1',
    회원이름: '18.2.0',
    휴대전화: '2013-05-29',
    약정일: '2013-05-29',
    상품: '2013-05-29',
    계약금액: '2013-05-29',
    계약상태: '2013-05-29',
    간편동의여부: '2013-05-29',
  },
  {
    No: '2',
    회원이름: '18.2.0',
    휴대전화: '2013-05-29',
    약정일: '2013-05-29',
    상품: '2013-05-29',
    계약금액: '2013-05-29',
    계약상태: '2013-05-29',
    간편동의여부: '2013-05-29',
  },
  {
    No: '3',
    회원이름: '18.2.0',
    휴대전화: '2013-05-29',
    약정일: '2013-05-29',
    상품: '2013-05-29',
    계약금액: '2013-05-29',
    계약상태: '2013-05-29',
    간편동의여부: '2013-05-29',
  },
  {
    No: '3',
    회원이름: '18.2.0',
    휴대전화: '2013-05-29',
    약정일: '2013-05-29',
    상품: '2013-05-29',
    계약금액: '2013-05-29',
    계약상태: '2013-05-29',
    간편동의여부: '2013-05-29',
  },
  {
    No: '3',
    회원이름: '18.2.0',
    휴대전화: '2013-05-29',
    약정일: '2013-05-29',
    상품: '2013-05-29',
    계약금액: '2013-05-29',
    계약상태: '2013-05-29',
    간편동의여부: '2013-05-29',
  },
  {
    No: '3',
    회원이름: '18.2.0',
    휴대전화: '2013-05-29',
    약정일: '2013-05-29',
    상품: '2013-05-29',
    계약금액: '2013-05-29',
    계약상태: '2013-05-29',
    간편동의여부: '2013-05-29',
  },
  {
    No: '3',
    회원이름: '18.2.0',
    휴대전화: '2013-05-29',
    약정일: '2013-05-29',
    상품: '2013-05-29',
    계약금액: '2013-05-29',
    계약상태: '2013-05-29',
    간편동의여부: '2013-05-29',
  },
  {
    No: '3',
    회원이름: '18.2.0',
    휴대전화: '2013-05-29',
    약정일: '2013-05-29',
    상품: '2013-05-29',
    계약금액: '2013-05-29',
    계약상태: '2013-05-29',
    간편동의여부: '2013-05-29',
  },
  {
    No: '3',
    회원이름: '18.2.0',
    휴대전화: '2013-05-29',
    약정일: '2013-05-29',
    상품: '2013-05-29',
    계약금액: '2013-05-29',
    계약상태: '2013-05-29',
    간편동의여부: '2013-05-29',
  },
  {
    No: '3',
    회원이름: '18.2.0',
    휴대전화: '2013-05-29',
    약정일: '2013-05-29',
    상품: '2013-05-29',
    계약금액: '2013-05-29',
    계약상태: '2013-05-29',
    간편동의여부: '2013-05-29',
  },
];

const initialSearch = [
  { key: 'checkbox', type: 'hidden', value: '' },
  { key: 'No', type: 'hidden', value: '' },
  { key: '회원이름', type: 'text', value: '' },
  { key: '휴대전화', type: 'text', value: '' },
  {
    key: '약정일',
    type: 'select',
    value: '',
    options: ['1일', '7일', '12일', '24일'],
  },
  { key: '상품', type: 'text', value: '' },
  { key: '계약금액', type: 'number', value: '' },
  {
    key: '계약상태',
    type: 'select',
    value: '',
    options: ['계약종료', '진행중', '대기중'],
  },
  {
    key: '간편동의여부',
    type: 'select',
    value: '',
    options: ['완료', '대기중'],
  },
];

const MemberListPage = () => {
  const [search, setSearch] = useState(initialSearch);

  const handleSearchChange = (key, value) => {
    setSearch(prev =>
      prev.map(searchItem =>
        searchItem.key === key ? { ...searchItem, value: value } : searchItem
      )
    );
  };

  // 검색 API 들어갈 함수
  const show = () => {
    console.log(search);
  };

  const navigate = useNavigate();

  // id값은 추후 변경
  const handleGoDetail = () => {
    navigate('detail/1');
  };

  const handleGoRegister = () => {
    navigate('register');
  };
  return (
    <div className='primary-dashboard h-full w-full flex flex-col overflow-auto'>
      <div>
        <button
          className='w-56 rounded-lg bg-mint p-3 font-bold text-white'
          onClick={handleGoDetail}>
          임시 회원 상세 이동
        </button>
        <button
          className='w-56 rounded-lg bg-mint p-3 font-bold text-white'
          onClick={handleGoRegister}>
          임시 회원 등록
        </button>
      </div>
      <Table
        cols={cols}
        search={search}
        items={items}
        handleSearchChange={handleSearchChange}
        show={show}
      />
    </div>
  );
};

export default MemberListPage;
