import { privateAxios } from '.';
import { notebookAxios } from './notebooks';

export const getStatisticList = async (searchParams = {}) => {
  const res = await privateAxios.get('/v1/statistics/member-contracts', {
    params: {
      ...searchParams,
    },
  });
  return res;
};

export const getRenewalProbability = async memberData => {
  try {
    const res = await privateAxios.post('/v1/statistics/notebook/member.ipynb', memberData);
    return res.data;
  } catch (err) {
    console.error('Error fetching renewal probability:', err);
    throw err;
  }
};
