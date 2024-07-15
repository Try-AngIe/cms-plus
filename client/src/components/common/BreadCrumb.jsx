import User from '@/assets/User';

const BreadCrumb = () => {
  return (
    <div className='h-12 w-48 flex border border-red-400'>
      <div>
        <User />
      </div>
      /
      <div>회원목록</div>
    </div>
  );
};

export default BreadCrumb;
