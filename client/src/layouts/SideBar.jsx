import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import vendorRoute from '@/routes/vendorRoute';
import React, { useEffect, useState } from 'react';
import { useSideBarActiveStore } from '@/stores/useSideBarActiveStore';
import cmslogo from '@/assets/cmslogo.svg';

const SideBar = () => {
  const [refresh, setRefresh] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const { sideBarMenus, toggle } = useSideBarActiveStore();

  const move = () => {
    navigate('/vendor/dashboard');
  };

  return (
    <div className='hidden h-full w-72  py-6 pl-4 mr-4 sticky desktop:block desktop:animate-slideIn '>
      <div
        className='mb-7 ml-3 mt-3 flex items-center cursor-pointer'
        onClick={() => navigate('/vendor/dashboard')}>
        <img src={cmslogo} alt='logo' className='mx-2 h-8 w-8' />
        <h1 className='font-GeekbleMalang text-xl text-text_black mb-1'>CMS.S3</h1>
      </div>
      <div className='border-gradient mb-5 border-b-2' />
      {vendorRoute().map((route, idx) => {
        return (
          route.path && (
            <div key={idx} onClick={() => toggle(route.path)}>
              <NavLink
                to={route.path}
                className={({ isActive }) =>
                  ` ${isActive && ' bg-white shadow-sidebars'} flex rounded-xl cursor-pointer mb-2 px-3 py-2`
                }>
                {({ isActive }) => (
                  <div className='flex h-full w-full items-center justify-between'>
                    <div className='flex items-center'>
                      <div
                        className={`mr-4 flex h-9 w-9 items-center justify-center rounded-xl ${isActive ? 'bg-mint' : 'bg-white shadow-sidebars'}`}>
                        {React.cloneElement(route.icon, {
                          className: 'h-6 w-4',
                          fill:
                            route.path === 'products'
                              ? isActive
                                ? '#4FD1C5'
                                : '#ffffff'
                              : isActive
                                ? '#ffffff'
                                : '#4FD1C5',

                          stroke:
                            route.path === 'products' ? (isActive ? '#ffffff' : '#4FD1C5') : ' ',
                        })}
                      </div>
                      <p
                        className={`text-15 font-800 ${isActive ? ' text-text_black' : ' text-text_grey'}`}>
                        {route.name}
                      </p>
                    </div>
                    {/* {route.children && (
                      <Arrow className='mr-1 h-2 w-4' rotation={isActive ? 0 : 180} />
                    )} */}
                  </div>
                )}
              </NavLink>
              {/* {route.children &&
                route.children.map((child, idx) => {
                  return (
                    child.menu && (
                      <div key={idx} className='flex items-center px-7 mb-4'>
                        <div className='rounded-full w-2 h-2 bg-mint mr-7' />
                        <p className='text-sm text-text_grey'>{child.name}</p>
                      </div>
                    )
                  );
                })} */}
            </div>
          )
        );
      })}
    </div>
  );
};

export default SideBar;
