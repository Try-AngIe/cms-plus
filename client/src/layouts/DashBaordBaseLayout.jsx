import Header from './Header';
import SideBar from './SideBar';

const DashBaordBaseLayout = ({ children }) => {
  return (
    <div className='flex w-full max-w-2400 max-h-1500 bg-background relative '>
      <SideBar />
      <div className='flex h-full flex-col w-full overflow-auto scrollbar-custom'>
        <Header />
        <main className='flex flex-col px-8 pt-4 pb-7 h-full large_desktop:h-[89vh] extra_desktop:h-[92vh] '>
          {children}
        </main>
      </div>
    </div>
  );
};

export default DashBaordBaseLayout;
