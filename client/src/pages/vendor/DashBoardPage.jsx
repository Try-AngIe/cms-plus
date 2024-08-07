import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faUsers,
  faFileContract,
  faMoneyBillWave,
  faChartLine,
} from '@fortawesome/free-solid-svg-icons';
import { getMonthBillingInfo, getStatInfo, getTopInfo } from '@/apis/dashboard';
import TopMembers from '@/components/vendor/dashboard/DashBoardTopMembers';
import RecentContracts from '@/components/vendor/dashboard/DashBoardRecentContracts';
import DashBoardCalendar, {
  convertToCalendarEvents,
} from '@/components/vendor/dashboard/DashBoardCalendar';
import BillingSummary from '@/components/vendor/dashboard/DashBoardBillingSummary';
import formatNumber from '@/utils/format/formatNumber';

export const STATUS_COLORS = {
  PAID: 'bg-green-100 text-green-800',
  WAITING_PAYMENT: 'bg-yellow-100 text-yellow-800',
  NON_PAID: 'bg-red-100 text-red-800',
  CREATED: 'bg-blue-100 text-blue-800',
};

const StatItem = ({ icon, title, value, subStat }) => (
  <div className='bg-white rounded-xl shadow-dash-sub p-6 transition duration-300 ease-in-out hover:shadow-md cursor-default'>
    <div className='flex items-center mb-4'>
      <FontAwesomeIcon icon={icon} className='text-mint text-2xl mr-4' />
      <h3 className='text-sm font-700 text-text_black'>{title}</h3>
    </div>
    <p className='text-2xl font-700 text-text_black mb-2'>{value}</p>
    <div className='text-xs font-400 text-text_grey'>{subStat}</div>
  </div>
);

const Stats = React.memo(({ statInfo }) => (
  <div className='mb-6 grid grid-cols-1 md:grid-cols-4 gap-6'>
    <StatItem
      icon={faUsers}
      title='회원 현황'
      value={`${formatNumber(statInfo.totalMemberCount)}명`}
      subStat={`신규: ${formatNumber(statInfo.newMemberCount)} | 활성: ${formatNumber(statInfo.activeMemberCount)}`}
    />
    <StatItem
      icon={faFileContract}
      title='계약 현황'
      value={`${formatNumber(statInfo.totalContractCount)}건`}
      subStat={`신규: ${formatNumber(statInfo.newContractCount)} | 만료예정: ${formatNumber(statInfo.expectedToExpiredCount)}`}
    />
    <StatItem
      icon={faMoneyBillWave}
      title='청구 현황'
      value={`₩${formatNumber(statInfo.totalBillingPrice)}`}
      subStat={`완납: ₩${formatNumber(statInfo.totalPaidPrice)} | 미납: ₩${formatNumber(statInfo.totalNotPaidPrice)}`}
    />
    <StatItem
      icon={faChartLine}
      title={`${new Date().getMonth()}월 대비 매출`}
      value={`${statInfo.billingPriceGrowth > 0 ? '+' : ''}${statInfo.billingPriceGrowth?.toFixed(1)}%`}
      subStat={`회원: ${statInfo.memberGrowth >= 0 ? '+' : ''}${statInfo.memberGrowth?.toFixed(1)}%`}
    />
  </div>
));

Stats.displayName = 'Stats';

const DashBoardPage = () => {
  const [statInfo, setStatInfo] = useState({
    totalMemberCount: 0,
    newMemberCount: 0,
    activeMemberCount: 0,
    totalContractCount: 0,
    newContractCount: 0,
    expectedToExpiredCount: 0,
    totalBillingPrice: 0,
    totalPaidPrice: 0,
    totalNotPaidPrice: 0,
    billingPriceGrowth: 0,
    memberGrowth: 0,
  });

  const [billingInfo, setBillingInfo] = useState({
    totalBillingPrice: 0,
    statusPrices: {},
    totalBillingAmount: 0,
    statusCounts: {},
    dayBillingRes: [],
  });
  const [topFive, setTopFive] = useState({});
  const [date, setDate] = useState(new Date());

  const calendarRef = useRef();
  const [events, setEvents] = useState([]);

  const fetchMonthBillingInfo = useCallback(async (year, month) => {
    try {
      const res = await getMonthBillingInfo(year, month);
      setBillingInfo(res.data);
      setEvents(convertToCalendarEvents(res.data));
    } catch (error) {
      console.error('Error fetching billing info:', error);
    }
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      const date = new Date();
      const curYear = date.getFullYear();
      const curMonth = date.getMonth() + 1;
      const [statRes, topRes] = await Promise.all([getStatInfo(curYear, curMonth), getTopInfo()]);
      setStatInfo(statRes.data);
      setTopFive(topRes.data);
    };

    fetchData();
  }, []);

  useEffect(() => {
    const curMonth = date.getMonth() + 1;
    const curYear = date.getFullYear();
    fetchMonthBillingInfo(curYear, curMonth);
  }, [date, fetchMonthBillingInfo]);

  const calendarGoNext = useCallback(() => {
    const calendarApi = calendarRef.current.getApi();
    calendarApi.next();
    setDate(calendarApi.getDate());
  }, []);

  const calendarGoPrev = useCallback(() => {
    const calendarApi = calendarRef.current.getApi();
    calendarApi.prev();
    setDate(calendarApi.getDate());
  }, []);

  const handleCalendarEventClicked = useCallback(async info => {
    // TODO: Implement event click handling
  }, []);

  const memoizedCalendar = useMemo(
    () => (
      <DashBoardCalendar
        events={events}
        onEventClick={handleCalendarEventClicked}
        calendarRef={calendarRef}
        onNext={calendarGoNext}
        onPrev={calendarGoPrev}
      />
    ),
    [events, handleCalendarEventClicked, calendarGoNext, calendarGoPrev]
  );

  return (
    <div className='pb-10'>
      <div className='container mx-auto'>
        {statInfo && <Stats statInfo={statInfo} />}
        <div className='grid grid-cols-1 lg:grid-cols-4 gap-6 mb-8'>
          <div className='lg:col-span-3'>
            <div className='bg-white rounded-xl shadow-dash-sub p-4 transition duration-300 ease-in-out hover:shadow-md '>
              {memoizedCalendar}
            </div>
          </div>
          <div className='bg-white rounded-xl shadow-dash-sub transition duration-300 ease-in-out hover:shadow-md'>
            <BillingSummary billingInfo={billingInfo} month={date.getMonth() + 1} />
          </div>
        </div>
        <div className='grid grid-cols-1 lg:grid-cols-2 gap-6'>
          <div className='bg-white rounded-xl shadow-dash-sub transition duration-300 ease-in-out hover:shadow-md'>
            <TopMembers topFive={topFive.topFiveMemberRes} />
          </div>
          <div className='bg-white rounded-xl shadow-dash-sub transition duration-300 ease-in-out hover:shadow-md'>
            <RecentContracts contracts={topFive.recentFiveContracts} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashBoardPage;
