import { useState, useEffect, useCallback } from 'react';
import { getStatisticList, getRenewalProbability } from '@/apis/statistic';
import StatisticList from '@/components/common/statistics/StatisticList';
import useAlert from '@/hooks/useAlert';
import DountChart from '@/components/common/charts/DountChart';
import styled, { keyframes } from 'styled-components';
import InfoRow from '@/components/common/InfoRow';

const Statistics = () => {
  const [statisticList, setStatisticList] = useState([]);
  const [filteredList, setFilteredList] = useState([]);
  const [selectedContract, setSelectedContract] = useState(null);
  const [renewalProbability, setRenewalProbability] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const onAlert = useAlert();

  const fetchStatisticList = useCallback(async () => {
    console.log('Fetching statistic list...', new Date().toISOString());
    try {
      const res = await getStatisticList();
      console.log('Fetched statistic list', res.data);
      setStatisticList(res.data);
      setFilteredList(res.data);
    } catch (err) {
      console.error('Failed to fetch statistic list', err);
      onAlert('회원 목록을 불러오는데 실패했습니다.', 'error');
    }
  }, [onAlert]);

  const handleSelectContract = useCallback(async (contract) => {
    setSelectedContract(contract);
    setIsLoading(true);
    const memberData = {
      enroll_year: contract.enrollYear,
      contract_duration: contract.contractDuration,
      total_contract_amount: contract.totalContractAmount,
      payment_type: contract.paymentType,
    };

    try {
      console.log('request', memberData)

      const result = await getRenewalProbability(memberData);
      console.log('Full server response:', result);
      const probability = parseFloat(result.new_member_renewal_probability.replace('%', '')) / 100;
      setRenewalProbability(probability);
    } catch (err) {
      console.error('갱신 확률 조회 실패', err);
      console.error('Error response:', err.response?.data);
      onAlert('갱신 확률을 불러오는데 실패했습니다.', 'error');
    } finally {
      setIsLoading(false);
    }
  }, [onAlert]);

  useEffect(() => {
    fetchStatisticList();
  }, []); // 빈 의존성 배열로 변경

  return (
      <div className='flex flex-1 overflow-hidden'>
        <StatisticList
          statisticList={statisticList}
          filteredList={filteredList}
          setFilteredList={setFilteredList}
          handleSelectContract={handleSelectContract}
        />

        <div className='w-px bg-ipt_border' />

        <div className='w-3/5 p-6 flex flex-col h-full overflow-hidden'>
          <h2 className='text-2xl font-semibold mb-4 text-text_black'>특정 계약의 갱신 예측</h2>
          {selectedContract ? (
            <div className='flex-1 overflow-hidden flex flex-col'>
              <div className='mb-4 space-y-2 border-b border-t py-3'>
                <InfoRow label='계약ID' value={selectedContract.contractId} />
                <InfoRow label='가입연도' value={selectedContract.enrollYear} />
                <InfoRow label='계약기간' value={`${selectedContract.contractDuration}일`} />
                <InfoRow label='총 계약금액' value={`${selectedContract.totalContractAmount.toLocaleString()}원`} />
              </div>
              <h3 className='text-2xl font-semibold mt-3 text-text_black'>갱신 확률</h3>
              {isLoading ? (
                <SpinnerContainer>
                  <LoadingSpinner />
                </SpinnerContainer>
              ) : (
                renewalProbability !== null && (
                    <>
                      <ChartContainer>
                        <DountChart color="#07f" percent={renewalProbability} size="150px" />
                      </ChartContainer>
                      <p className='text-center text-xl text-gray-500 mt-4'>
                        해당 계약 갱신 확률은 </p>
                        <p className='text-center text-xl text-blue-600 mt-2'>{Math.round(renewalProbability * 100)}%</p>
                    </>
                )
              )}
            </div>
          ) : (
            <div className='flex-1 flex mt-2 items-center justify-center'>
              <p className='text-center text-xl text-gray-500'>회원을 선택해주세요.</p>
            </div>
          )}
        </div>
      </div>
  );
};

const ChartContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
`;

const SpinnerContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
`;

const spin = keyframes`
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
`;

const LoadingSpinner = styled.div`
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-top: 4px solid #07f;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: ${spin} 1s linear infinite;
`;

export default Statistics;
