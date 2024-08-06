import daisyui from 'daisyui';

/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/**/*.{html,js,jsx,ts,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        'GeekbleMalang': ['GeekbleMalang'],
      },

      colors: {
        mint: '#4FD1C5', // 메인컬러
        mint_hover: '#51B1A8', // 메인컬러 호버
        mint_hover_light: '#C1F3EF', // 하얀버튼 호버
        background: '#F8F9FA', // 배경컬러
        text_black: '#344767', // 텍스트 메인컬러
        text_grey: '#7B809A', // 텍스트 서브컬러
        ipt_border: '#C7CCD0', // 입력창 테두리
        ipt_disa: '#F1F1F1', // 입력창 비활성화
        essential: '#FF0000', // 필수정보 별태그
        btn_disa: '#C4C4C4', // 버튼 비활성화
        negative: '#F44334', // 취소, 실패 버튼
        negative_hover: '#DD3324', // 취소, 실패 버튼 호버
        warning: '#FFD33C', // 경고 노란색
        warning_hover: '#EABF29', // 경고 노란색 호버
        table_col: '#f4f5f6', // 테이블 컬럼색
      },

      screens: {
        mobile: '640px', // sm
        tablet: '768px', // md
        laptop: '1024px', // lg
        desktop: '1280px', // xl
        large_desktop: '1536px', // 2xl
        extra_desktop: '2000px', // 2xl
        tmp: '1800px', // 2xl
      },

      fontSize: {
        15: ['15px', '20px'],
      },

      spacing: {
        46: '46px',
        54: '54px',
        380: '380px',
        400: '400px',
        420: '420px',
        480: '480px',
        510: '510px',
        540: '540px',
        580: '580px',
        640: '640px', // 모바일 max-width
        680: '680px',
        720: '720px',
        730: '730px',
        1200: '1200px',
        1500: '1500px', // 브라우저 max-height
        2400: '2400px', // 브라우저 max-width
      },

      boxShadow: {
        'dash-board': '0px 2px 6px rgba(0, 0, 0, 0.25)', // 대시보드 그림자
        'dash-sub': '0px 1px 6px 0px #DADBDC', // 서브 대시보드 그림자
        sidebars: '0 1px 3.5px 0 rgba(218, 219, 220, 1)', // 사이드바 아이콘 그림자
        dash_up: '0 0 3px 0 rgba(0, 0, 0, 0.25)',
        modal: '0 3px 10px rgba(209, 209, 209, 1)',
        dialog: '0 0 15px rgba(0, 0, 0, 0.1)', // alert, confirm
        column: '0 1px 2px rgba(0, 0, 0, 0.19)', // table column 그림자
      },

      fontWeight: {
        300: '300',
        400: '400',
        700: '700',
        800: '800',
        900: '900',
      },

      keyframes: {
        slideIn: {
          '0%': { transform: 'translateX(-100%)', opacity: 0 },
          '100%': { transform: 'translateX(0)', opacity: 1 },
        },
        slideOut: {
          '0%': { transform: 'translateX(0)', opacity: 1 },
          '100%': { transform: 'translateX(-100%)', opacity: 0 },
        },
      },

      animation: {
        slideIn: 'slideIn 0.4s ease-out forwards',
        slideOut: 'slideOut 0.4s ease-out forwards',
      },
    },
  },
  plugins: [daisyui],
};
