import { create } from 'zustand';
import { privateAxios } from '@/apis';

const useSimpleConsentStore = create(set => ({
  selectedProducts: [],
  checkedItems: {
    '실시간 CMS': true,
    카드: true,
  },
  setSelectedProducts: products => set({ selectedProducts: products }),
  setCheckedItems: items => set({ checkedItems: items }),
  saveSimpleConsent: async () => {
    const state = useSimpleConsentStore.getState();
    const paymentMethods = [];
    if (state.checkedItems['카드']) {
      paymentMethods.push({
        canCancel: true,
        canPayRealtime: true,
        title: '카드',
        code: 'CARD',
      });
    }
    if (state.checkedItems['실시간 CMS']) {
      paymentMethods.push({
        canCancel: false,
        canPayRealtime: true,
        title: '실시간CMS',
        code: 'CMS',
      });
    }
    const productIds = state.selectedProducts.map(product => product.productId);

    try {
      const response = await privateAxios.put('/v1/vendor/simple-consent', {
        id: 1,
        vendorUsername: 'vendor1',
        paymentMethods,
        productIds,
      });
      console.log('Simple consent saved:', response.data);
      return response.data;
    } catch (error) {
      console.error('Failed to save simple consent:', error);
      throw error;
    }
  },
}));

export default useSimpleConsentStore;
