import vendorRoute from '@/routes/vendorRoute';
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

const vendorRoutePaths = () => {
  const paths = {};

  vendorRoute().forEach(route => {
    route.path && (paths[route.path] = false);
  });
  paths['dashboard'] = true;
  return paths;
};

export const useSideBarActiveStore = create(
  persist(
    set => ({
      sideBarMenus: vendorRoutePaths(),
      toggle: menu =>
        set(state => {
          state.sideBarMenus[menu] = !state.sideBarMenus[menu];
          if (menu != 'dashboard') {
            state.sideBarMenus['dashboard'] = false;
          }
          return { sideBarMenus: state.sideBarMenus };
        }),
    }),
    {
      name: 'sideBar-storage',
      getStorage: () => localStorage,
    }
  )
);
