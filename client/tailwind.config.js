/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/**/*.{html,js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        mint: '#4FD1C5', // 메인컬러
        mint_hover: '#51B1A8', // 메인컬러 호버
        background: '#F8F9FA', // 배경컬러
        text_black: '#344767', // 텍스트 메인컬러
        text_grey: '#7B809A', // 텍스트 서브컬러
        ipt_border: '#C7CCD0', // 입력창 테두리
        ipt_disa: '#F1F1F1', // 입력창 비활성화
        essential: '#FF0000', // 필수정보 별태그
      },

      screens: {
        mobile: '640px', // sm
        tablet: '768px', // md
        laptop: '1024px', // lg
        desktop: '1280px', // xl
        large_desktop: '1536px', // 2xl
      },

      spacing: {
        480: '480px',
        640: '640px', // 모바일 max-width
        2400: '2400px', // 브라우저 max-width
      },

      boxShadow: {
        'dash-board': '0px 2px 6px rgba(0, 0, 0, 0.25)', // 대시보드 그림자
        'dash-sub': '0px 1px 6px 0px #DADBDC', // 서브 대시보드 그림자
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
  plugins: [],
};