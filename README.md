# MES (Manufacturing Execution System)

> 제조 실행 시스템 - Spring Boot + React 기반 생산 관리 플랫폼

## 📋 프로젝트 소개

실무 수준의 제조 실행 시스템(MES)으로, 생산 공정 관리부터 품질 검사까지 제조업의 핵심 프로세스를 지원

### 주요 기능

- ** LOT 추적 시스템**: 전체 생산 이력 추적 및 공정별 투입/산출/불량 관리
- ** 품질 검사 관리**: 검사 기준 설정, 측정값 입력, 자동 판정 시스템
- **️ 공정 처리**: 실시간 공정 실행 및 설비 할당
- ** 통계 대시보드**: OEE 계산, 불량률 분석, 생산 현황 모니터링
- ** 실시간 모니터링**: WebSocket 기반 실시간 데이터 업데이트


## 🛠 기술 스택

### Backend
- **Framework**: Spring Boot 3.5.7
- **Language**: Java 21
- **ORM**: Hibernate 6.6.33, Spring Data JPA
- **Database**: MySQL 8.0
- **Build Tool**: Maven
- **Real-time**: WebSocket (STOMP)

### Frontend
- **Framework**: React 18.3.1
- **Build Tool**: Vite 7.2.4
- **UI Library**: Material-UI (MUI) 6.3.1
- **HTTP Client**: Axios
- **Routing**: React Router DOM 7.1.1

## 🏗 시스템 아키텍처
```
┌─────────────────┐         ┌──────────────────┐         ┌─────────────┐
│   React SPA     │ ──────> │  Spring Boot API │ ──────> │    MySQL    │
│  (Frontend)     │  HTTP   │    (Backend)     │  JDBC   │  (Database) │
└─────────────────┘         └──────────────────┘         └─────────────┘
        │                            │
        │         WebSocket          │
        └────────────────────────────┘
              (Real-time Updates)
```

## 📐 데이터베이스 설계

### 핵심 엔티티
### LOT (생산 로트)
```
- lotNumber: 고유 LOT 번호 (LOT-YYYYMMDD-XXXX)
- product: 제품 정보
- workOrder: 작업 오더
- status: CREATED / IN_PROGRESS / COMPLETED / ON_HOLD / REJECTED / SHIPPED
- quantity: 수량
```

###  LotHistory (공정 이력)
```
- lot: 연관 LOT
- process: 공정 정보
- equipment: 사용 설비
- inputQuantity: 투입 수량
- outputQuantity: 산출 수량
- defectQuantity: 불량 수량
- result: PASS / FAIL / REWORK / PENDING
- operator: 작업자
```

### QualityInspection (품질 검사)
```
- inspectionNumber: 검사 번호 (INS-YYYYMMDD-XXXX)
- lot: 검사 대상 LOT
- type: INCOMING / IN_PROCESS / FINAL / OUTGOING
- status: PENDING / IN_PROGRESS / COMPLETED / CANCELLED
- result: PASS / FAIL / CONDITIONAL_PASS
- items: 검사 항목 리스트
```

### InspectionStandard (검사 기준)
```
- code: 기준 코드
- name: 기준 명
- standardValue: 기준값
- upperLimit / lowerLimit: 허용 범위
- unit: 단위
- applicableType: 적용 검사 유형
```

### ERD 주요 관계
```
  Product ─┬─< ProcessRouting >─── ProcessEntity
  └─< WorkOrder ─< LOT ─┬─< LotHistory >─── Equipment
  └─< QualityInspection ─< InspectionItem >─── InspectionStandard
```

## 🎯 주요 기능 상세

### 1. LOT 추적 시스템
- LOT 번호 자동 생성 (LOT-YYYYMMDD-XXXX)
- 공정별 처리 이력 기록
- Timeline 형태의 이력 시각화
- 실시간 상태 업데이트

### 2. 품질 검사 관리
- 검사 유형별 기준 관리 (수입/공정/최종/출하)
- 측정값 입력 및 자동 판정
- 범위 기반 PASS/FAIL 자동 계산
- 검사 이력 관리
- 합격률 통계

### 3. 공정 처리
- 활성 LOT 목록 표시
- 공정 및 설비 선택
- 투입/산출/불량 수량 입력 및 검증
- 실시간 상태 업데이트
- 작업자 기록

### 워크플로우:
```
1. LOT 선택
   ↓
2. 공정 및 설비 선택
   ↓
3. 투입/산출/불량 수량 입력
   ↓
4. 작업자 정보 입력
   ↓
5. 공정 이력 저장 + LOT 상태 업데이트
```

### 4. 통계 대시보드 & OEE 
목적: 생산 효율성 분석 및 개선점 도출

```
OEE = Availability × Performance × Quality

- Availability (가동률): 계획 대비 실제 가동 시간
- Performance (성능률): 표준 대비 실제 사이클 타임
- Quality (품질률): 양품률 (총 생산 - 불량) / 총 생산

World Class: 85% 이상
Good: 65-84%
Fair: 40-64%
Poor: 40% 미만
```
## 📱 화면 구성

1. Dashboard

실시간 생산 현황
설비 상태 모니터링
작업 진행률
WebSocket 실시간 업데이트

2. LOT Tracking

LOT 목록 및 검색
LOT 생성
공정 이력 Timeline
상태별 필터링

3. Process Execution

활성 LOT 목록
공정 실행 다이얼로그
투입/산출/불량 수량 입력
실시간 처리 내역

4. Quality Inspection

검사 목록 (Inspections 탭)
검사 기준 관리 (Standards 탭)
측정값 입력 인터페이스
자동 판정 시스템
검사 결과 완료 처리

5. Statistics

LOT 통계 (Total/Completed/In Progress)
생산량 및 불량률
품질 검사 합격률
OEE 시각화 (프로그레스 바)
30초 자동 갱신

## 🚀 설치 및 실행

### 사전 요구사항
- Java 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### Backend 설정

1. 데이터베이스 생성
```sql
CREATE DATABASE mes;
```

2. application.yml 설정
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mes
    username: root
    password: your_password
```

3. 실행
```bash
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

### 샘플 데이터

앱 시작 시 자동 생성:
- 6개 설비 (Cutting, Assembly, Packaging, Inspection)
- 9개 검사 기준 (치수, 외관, 성능, 수입 검사)
- 3개 LOT (완료 2개, 진행 중 1개)
- 2개 제품 및 4개 공정
- 2개 작업 오더
- 11개 공정 이력
- 4개 품질 검사

## 📊 시스템 흐름도
```
제품 등록 → 작업 오더 생성 → LOT 생성
                           ↓
                        공정 처리
                  (Process Execution)
                           ↓
                  공정 이력 기록 ←─→ 설비 할당
                 (LotHistory)    (Equipment)
                           ↓
                        품질 검사
                  (Quality Inspection)
                           ↓
                  검사 항목 측정 → 자동 판정
                           ↓
                        LOT 완료
                           ↓
                      통계 분석 (OEE)

```


## 🎓 기술적 도전

### 1. LOT 번호 중복 방지
동시 생성 시 시퀀스 중복 문제 → DB 조회 기반 카운팅으로 해결

### 2. Lazy Loading 에러
WebSocket에서 트랜잭션 없이 엔티티 접근 → @Transactional 추가

### 3. CORS 설정
allowCredentials=true 시 와일드카드 불가 → allowedOriginPatterns 사용

### 4. 데이터 검증
비숫자 값 파싱 에러 → try-catch로 예외 처리

## 📈 향후 개선

- [ ] Spring Security 인증/인가
- [ ] 생산 계획/스케줄링
- [ ] 재고 관리 연동
- [ ] Excel/PDF 리포트
- [ ] 모바일 최적화

