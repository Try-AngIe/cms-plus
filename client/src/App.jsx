import root from '@/routes/root';
import { RouterProvider } from 'react-router-dom';

const App = () => {
  return (
    <div className='flex h-full w-full justify-center'>
      <RouterProvider router={root} />
    </div>
  );
};

export default App;
