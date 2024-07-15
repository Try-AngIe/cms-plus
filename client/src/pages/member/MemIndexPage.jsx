import { Outlet } from 'react-router-dom';
import BaseLayout from '@/layouts/MobileBaseLayout';

const MemIndexPage = () => {
  return (
    <BaseLayout>
      <Outlet />
    </BaseLayout>
  );
};

export default MemIndexPage;
