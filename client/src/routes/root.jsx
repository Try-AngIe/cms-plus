import { lazy } from 'react';
import { Navigate, createBrowserRouter } from 'react-router-dom';
import LazyComponentWrapper from '@/pages/utils/LazyComponentWrapper';
import vendorRoute from './vendorRoute';
import memberRoute from './memberRoute';

const Login = lazy(() => import('@/pages/LoginPage')); // 로그인
const Signup = lazy(() => import('@/pages/SignupPage')); // 회원가입
const VendorIndex = lazy(() => import('@/pages/vendor/VenIndexPage')); // 고객 중첩 라우팅
const MemberIndex = lazy(() => import('@/pages/member/MemIndexPage')); // 회원 중첩 라우팅

const root = createBrowserRouter([
  {
    path: '/',
    element: <Navigate replace to='login' />,
  },
  {
    path: 'login',
    element: <LazyComponentWrapper component={Login} />,
  },
  {
    path: 'signup',
    element: <LazyComponentWrapper component={Signup} />,
  },
  {
    path: 'vendor',
    element: <LazyComponentWrapper component={VendorIndex} />,
    children: vendorRoute(),
  },
  {
    path: 'member',
    element: <LazyComponentWrapper component={MemberIndex} />,
    children: memberRoute(),
  },
]);

export default root;
