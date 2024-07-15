import { Navigate } from 'react-router-dom';
import { lazy } from 'react';
import LazyComponentWrapper from '@/pages/utils/LazyComponentWrapper';
import User from '@/assets/User';
import Home from '@/assets/Home';
import File from '@/assets/File';
import Card from '@/assets/Card';
import Item from '@/assets/Item';
import Setting from '@/assets/Setting';
import ProductListPage from '@/pages/vendor/product/ProductListPage';

const DashBoard = lazy(() => import('@/pages/vendor/DashBoardPage')); // 대시보드

const MemberIndex = lazy(() => import('@/pages/vendor/member/MemberIndex')); // 회원
const MemberList = lazy(() => import('@/pages/vendor/member/MemberListPage')); // 회원목록
const MemberDetail = lazy(() => import('@/pages/vendor/member/MemberDetailPage')); // 회원상세
const MemberRegister = lazy(() => import('@/pages/vendor/member/MemberRegisterPage')); // 회원등록
const MemberIngoUpdate = lazy(() => import('@/pages/vendor/member/MemberInfoUpdatePage')); // 회원수정

const ContractIndex = lazy(() => import('@/pages/vendor/contract/ContractIndex')); // 계약
const ContractList = lazy(() => import('@/pages/vendor/contract/ContractListPage')); // 계약목록
const ContractDetail = lazy(() => import('@/pages/vendor/contract/ContractDetailPage')); // 계약상세
const ProductInfoUpdate = lazy(() => import('@/pages/vendor/contract/ProductInfoUpdatePage')); // 상품수정
const BillingInfoUpdate = lazy(() => import('@/pages/vendor/contract/BillingInfoUpdatePage')); // 청구수정
const PaymentInfoUpdate = lazy(() => import('@/pages/vendor/contract/PaymentInfoUpdatePage')); // 결제수정

const BillingtIndex = lazy(() => import('@/pages/vendor/billing/BillingIndex')); // 청구
const BillingList = lazy(() => import('@/pages/vendor/billing/BillingListPage')); // 청구목록
const BillingDetail = lazy(() => import('@/pages/vendor/billing/BillingDetailPage')); // 청구상세

const ProductIndex = lazy(() => import('@/pages/vendor/product/ProductIndex')); // 상품
const ProductList = lazy(() => import('@/pages/vendor/product/ProductListPage')); // 상품목록

const SettingIndex = lazy(() => import('@/pages/vendor/setting/SettingIndex')); // 간편서명동의설정
const SettingSimpConsent = lazy(() => import('@/pages/vendor/setting/SettingSimpConsentPage')); // 간편서명동의설정

const vendorRoute = () => {
  return [
    {
      path: '',
      element: <Navigate replace to='dashboard' />,
    },
    {
      path: 'dashboard',
      name: '대시보드',
      icon: <Home />,
      element: <LazyComponentWrapper component={DashBoard} />,
    },
    {
      path: 'members',
      name: '회원',
      icon: <User />,
      element: <LazyComponentWrapper component={MemberIndex} />,
      children: [
        {
          path: '',
          name: '회원 목록',
          menu: true,
          element: <LazyComponentWrapper component={MemberList} />,
        },
        {
          path: 'detail/:id',
          name: '회원 상세 정보',
          menu: false,
          element: <LazyComponentWrapper component={MemberDetail} />,
        },
        {
          path: 'register',
          name: '회원 등록',
          menu: false,
          element: <LazyComponentWrapper component={MemberRegister} />,
        },
        {
          path: 'update/:id',
          name: '회원 정보 수정',
          menu: false,
          element: <LazyComponentWrapper component={MemberIngoUpdate} />,
        },
      ],
    },
    {
      path: 'contracts',
      name: '계약',
      icon: <File />,
      element: <LazyComponentWrapper component={ContractIndex} />,
      children: [
        {
          path: '',
          name: '계약 목록',
          menu: true,
          element: <LazyComponentWrapper component={ContractList} />,
        },
        {
          path: 'detail/:id',
          name: '계약 상세 정보',
          menu: false,
          element: <LazyComponentWrapper component={ContractDetail} />,
        },
        {
          path: 'Product/update/:id',
          name: '상품 정보 수정',
          menu: false,
          element: <LazyComponentWrapper component={ProductInfoUpdate} />,
        },
        {
          path: 'payment/update/:id',
          name: '결제 정보 수정',
          menu: false,
          element: <LazyComponentWrapper component={PaymentInfoUpdate} />,
        },
        {
          path: 'billings/update/:id',
          name: '청구 정보 수저',
          menu: false,
          element: <LazyComponentWrapper component={BillingInfoUpdate} />,
        },
      ],
    },
    {
      path: 'billings',
      name: '청구',
      icon: <Card />,
      element: <LazyComponentWrapper component={BillingtIndex} />,
      children: [
        {
          path: '',
          name: '청구 목록',
          menu: true,
          element: <LazyComponentWrapper component={BillingList} />,
        },
        {
          path: 'detail/:id',
          name: '청구 상세 정보',
          menu: false,
          element: <LazyComponentWrapper component={BillingDetail} />,
        },
      ],
    },
    {
      path: 'products',
      name: '상품',
      icon: <Item />,
      element: <LazyComponentWrapper component={ProductIndex} />,
      children: [
        {
          path: '',
          name: '상품 목록',
          menu: true,
          element: <LazyComponentWrapper component={ProductList} />,
        },
      ],
    },
    {
      path: 'setting',
      name: '설정',
      icon: <Setting />,
      element: <LazyComponentWrapper component={SettingIndex} />,
      children: [
        {
          path: '',
          name: '',
          menu: false,
          element: <Navigate to='simpconsent' replace />,
        },
        {
          path: 'simpconsent',
          name: '간편서명동의 설정',
          menu: true,
          element: <LazyComponentWrapper component={SettingSimpConsent} />,
        },
      ],
    },
  ];
};

export default vendorRoute;
