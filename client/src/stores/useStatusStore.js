import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

export const useStatusStore = create(
  persist(
    set => ({
      status: 0,
      increment: () => set(state => ({ status: state.status + 1 })),
      decrement: () => set(state => ({ status: state.status - 1 })),
      reset: () => set({ status: 0 }),
    }),
    {
      name: 'status-storage',
      getStorage: () => localStorage,
    }
  )
);
