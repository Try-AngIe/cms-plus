import { Outlet } from 'react-router-dom';
import BaseLayout from '@/layouts/DashBaordBaseLayout';

const VenIndexPage = () => {
  return (
    <BaseLayout>
      <Outlet />
    </BaseLayout>
  );
};

export default VenIndexPage;
