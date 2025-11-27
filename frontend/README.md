# MES System - Frontend

Manufacturing Execution System 프론트엔드 애플리케이션

## 기술 스택

- **React 18** - UI 라이브러리
- **Vite** - 빌드 도구
- **Material-UI (MUI)** - UI 컴포넌트 라이브러리
- **Chart.js** - 차트 라이브러리
- **React Router** - 클라이언트 사이드 라우팅
- **Axios** - HTTP 클라이언트

## 주요 기능

### 1. Dashboard (대시보드)
- 실시간 생산 현황 모니터링
- 오늘 생산 수량 / 불량 수량 / 가동률 표시
- 공정 진행률 Progress Bar
- 설비 상태 모니터링 (RUN/IDLE/ALARM)
- 불량률 차트
- SSE를 통한 실시간 데이터 수신

### 2. Work Orders (작업 지시 관리)
- 작업 지시 CRUD 기능
- 작업 지시 생성 / 수정 / 삭제
- 작업 지시 시작 기능
- 공정 라우팅 조회

### 3. Products (제품 관리)
- 제품 목록 조회
- 제품 등록 / 수정 / 삭제

### 4. Processes (공정 관리)
- 공정 목록 조회
- 공정 등록 / 수정 / 삭제

### 5. Equipment Monitor (설비 모니터링)
- 설비별 상태 모니터링
- 실시간 온도 / 진동 / 압력 그래프
- SSE를 통한 실시간 데이터 수신

### 6. Defects (불량 관리)
- 불량 코드 관리
- 불량 유형별 통계 그래프

## 시작하기

### 사전 요구사항

- Node.js 18.x 이상
- npm 8.x 이상

### 설치

```bash
npm install
```

### 개발 서버 실행

```bash
npm run dev
```

애플리케이션이 http://localhost:3000 에서 실행됩니다.

### 빌드

```bash
npm run build
```

빌드된 파일은 `dist` 폴더에 생성됩니다.

### 프리뷰

```bash
npm run preview
```

## 프로젝트 구조

```
frontend/
├── src/
│   ├── components/      # 공통 컴포넌트
│   │   └── Layout.jsx   # 메인 레이아웃 (사이드바, 헤더)
│   ├── pages/           # 페이지 컴포넌트
│   │   ├── Dashboard.jsx
│   │   ├── WorkOrders.jsx
│   │   ├── Products.jsx
│   │   ├── Processes.jsx
│   │   ├── EquipmentMonitor.jsx
│   │   └── Defects.jsx
│   ├── services/        # API 서비스
│   │   └── api.js       # Axios 인스턴스 및 API 함수
│   ├── hooks/           # Custom hooks
│   │   └── useSSE.js    # SSE 처리를 위한 hook
│   ├── App.jsx          # 메인 앱 (라우팅)
│   └── main.jsx         # 엔트리 포인트
├── public/              # 정적 파일
├── package.json
└── vite.config.js       # Vite 설정
```

## API 연동

백엔드 API는 `http://localhost:8080/api`에서 실행되어야 합니다.

Vite 프록시 설정으로 CORS 문제를 해결합니다:
- 프론트엔드: http://localhost:3000
- 백엔드: http://localhost:8080

## 주요 의존성

- `@mui/material` - Material-UI 컴포넌트
- `@emotion/react` - MUI를 위한 CSS-in-JS
- `react-router-dom` - 라우팅
- `axios` - HTTP 클라이언트
- `chart.js` & `react-chartjs-2` - 차트
- `date-fns` - 날짜 포맷팅

## 개발 가이드

### SSE (Server-Sent Events) 사용

`useSSE` hook을 사용하여 실시간 데이터를 받습니다:

```javascript
import useSSE from '../hooks/useSSE';

function MyComponent() {
  const { data, error, isConnected } = useSSE('http://localhost:8080/api/stream');

  useEffect(() => {
    if (data) {
      // 데이터 처리
    }
  }, [data]);
}
```

### API 호출

`services/api.js`에 정의된 API 함수를 사용합니다:

```javascript
import { workOrdersApi } from '../services/api';

const loadData = async () => {
  try {
    const response = await workOrdersApi.getAll();
    setData(response.data);
  } catch (error) {
    console.error('Failed to load data:', error);
  }
};
```

## 면접용 데모 포인트

1. **실시간 모니터링** - SSE를 활용한 실시간 데이터 업데이트
2. **Material-UI** - 모던하고 일관성 있는 UI 디자인
3. **Chart.js** - 데이터 시각화
4. **React Router** - SPA 라우팅
5. **컴포넌트 구조** - 재사용 가능한 컴포넌트 설계
6. **API 통합** - RESTful API와의 통합

## 라이센스

MIT