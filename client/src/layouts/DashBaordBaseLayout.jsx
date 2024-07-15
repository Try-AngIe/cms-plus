import Header from './Header';
import SideBar from './SideBar';

const DashBaordBaseLayout = ({ children }) => {
  return (
    <div className='flex h-full w-full max-w-2400 max-h-1500 bg-background'>
      <SideBar />
      <div className='flex h-full w-full flex-col'>
        <Header />
        <main className='flex h-full flex-col overflow-auto p-5 pl-5 pr-5 desktop:pl-5'>
          {children}
        </main>
      </div>
    </div>
  );
};

export default DashBaordBaseLayout;
