import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

export const useAddressStore = create(
  persist(set => ({
    zipcode: '',
    address: '',
    addressDetail: '',
    setZipcode: zipcode => {
      set({ zipcode });
    },
    setAddress: address => {
      set({ address });
    },
    setAddressDetail: addressDetail => {
      set({ addressDetail });
    },
    reset: () => set({ zipcode: '', address: '', addressDetail: '' }),
  }))
);
