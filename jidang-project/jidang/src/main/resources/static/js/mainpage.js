console.log("스크립트 적용 테스트");

document.addEventListener('DOMContentLoaded', function () {

  // 10초로 자동 슬라이드 간격 설정
  const AUTO_SLIDE_INTERVAL = 8000;
  let intervalId; // setInterval ID를 저장하여 타이머를 제어할 변수

  const bannerContainer = document.querySelector('.banner-container');
  const prev = document.querySelector(".prev");
  const next = document.querySelector(".next");

  const banners = [
  // 일단은 손수 업뎃, 유튜브 및 공식 페이지 링크때문에 완전 자동화는 다소 어려움
    //{ src: '/images/banner/플린스배너.webp', link: 'https://www.youtube.com/watch?v=kKGVSGuaQbk' },
    //{ src: '/images/banner/시협회파우웅.jpeg', link: 'https://www.youtube.com/watch?v=HC2weIpKDq8' },
    //{ src: '/images/banner/Pilgrimage-of-Compassion.jpeg', link: 'https://www.youtube.com/watch?v=O3s8Ejek1r4' },
    { src: '/images/banner/reverseBeryl.jpeg', link: 'https://www.youtube.com/watch?v=XGFFpbFNUfo' },
    { src: '/images/banner/limbus9season.jpeg', link: 'https://x.com/LimbusCompany_B/status/1989318804606701845' },
    { src: '/images/banner/블아공식팝업.jpeg', link: 'https://x.com/KR_BlueArchive/status/1990344169240530951' },
    { src: '/images/banner/원신네페르.jpeg', link: 'https://www.youtube.com/watch?v=S8KLZY1FZUs' },
    { src: '/images/banner/필두쌈닭.jpeg', link: 'https://www.youtube.com/watch?v=FB-NpxepZJk' }
  ];

  // 배너 슬라이드 생성 (기존 로직 유지)
  banners.forEach((banner, index) => {
    const slide = document.createElement('div');
    slide.classList.add('banner-slide');
    if(index === 0) slide.classList.add('active');

    const a = document.createElement('a');
    a.href = banner.link;
    a.target = "_blank";

    const img = document.createElement('img');
    img.src = banner.src;
    img.alt = `광고${index+1}`;

    a.appendChild(img);
    slide.appendChild(a);
    bannerContainer.appendChild(slide);
  });

  let currentIndex = 0;
  // DOM 생성 후 slides를 다시 가져옵니다.
  const slides = document.querySelectorAll('.banner-slide');

  function showSlide(index) {
    slides.forEach((slide, i) => {
      slide.classList.toggle('active', i === index);
    });
  }

  // --- 핵심 수정: 자동 슬라이드 제어 및 리셋 함수 ---

  // 자동 슬라이드 타이머를 시작/리셋하는 함수
  function startAutoSlide() {
    clearInterval(intervalId); // 기존 타이머를 먼저 멈춥니다.
    // 새로운 10초 타이머를 시작하고 ID를 저장합니다.
    intervalId = setInterval(nextSlideAuto, AUTO_SLIDE_INTERVAL);
  }

  // 자동 슬라이드 전환 로직 (타이머 리셋 기능 없음)
  function nextSlideAuto() {
    currentIndex = (currentIndex + 1) % slides.length;
    showSlide(currentIndex);
  }

  // 수동(버튼 클릭) 슬라이드 전환 로직 (타이머 리셋 포함)
  function nextSlideClick() {
    currentIndex = (currentIndex + 1) % slides.length;
    showSlide(currentIndex);
    startAutoSlide(); // 클릭 후 타이머를 10초로 리셋
  }

  function prevSlideClick() {
    currentIndex = (currentIndex - 1 + slides.length) % slides.length;
    showSlide(currentIndex);
    startAutoSlide(); // 클릭 후 타이머를 10초로 리셋
  }

  // --- 이벤트 리스너 연결 및 자동 슬라이드 시작 ---

  // 버튼 클릭 이벤트에 타이머 리셋 기능이 포함된 함수 연결
  if(next) next.addEventListener('click', nextSlideClick);
  if(prev) prev.addEventListener('click', prevSlideClick);

  // 페이지 로드 직후 자동 슬라이드 시작
  startAutoSlide();

  // 마우스 오버 시 일시 정지/재개 로직
  if(bannerContainer) {
    bannerContainer.addEventListener('mouseenter', () => {
      // 마우스 오버 시 타이머 정지
      clearInterval(intervalId);
    });
    bannerContainer.addEventListener('mouseleave', () => {
      // 마우스가 떠나면 타이머 재시작 (10초 리셋)
      startAutoSlide();
    });
  }
});