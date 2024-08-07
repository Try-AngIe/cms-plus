import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useVendorInfoStore = create(
  persist(
    set => ({
      vendorInfo: {
        role: '',
        name: '',
        usename: '',
        vendorId: '',
      },

      setVendorInfo: data => set({ vendorInfo: data }),
    }),
    {
      name: 'vendorInfo',
      getStorage: () => localStorage,
    }
  )
);
