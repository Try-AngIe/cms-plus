const MobileBaseLayout = ({ children }) => {
  return (
    <div className='h-dvh w-full p-6 mobile:w-640 relative' style={{ border: '1px solid red' }}>
      {children}
    </div>
  );
};

export default MobileBaseLayout;
