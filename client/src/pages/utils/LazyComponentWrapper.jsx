import { Suspense } from 'react';
import Loading from './Loading';

const LazyComponentWrapper = ({ component: Component }) => (
  <Suspense fallback={<Loading />}>
    <Component />
  </Suspense>
);

export default LazyComponentWrapper;
