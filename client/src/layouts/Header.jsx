import BreadCrumb from '@/components/common/BreadCrumb';
import vendorRoute from '@/routes/vendorRoute';

const Header = () => {
  const tmp = () => {
    for (const roots of vendorRoute()) {
      console.log(roots);
    }
  };

  return (
    <div className='flex h-24 items-end pl-8'>
      <BreadCrumb />
    </div>
  );
};

export default Header;
