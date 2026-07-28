# Agent DB Sync (구조 예시용 공개 저장소)

두 개의 데이터베이스(원본 DB / 대상 DB) 사이에서 사용자·조직 정보를 주기적으로
비교하여 동기화하는 Spring Boot 배치 애플리케이션의 **아키텍처 골격**입니다.

> 이 저장소는 사내 실제 코드에서 접속정보, 암호화 키, 실쿼리, 테이블/컬럼명 등
> 민감정보를 모두 제거하고 구조만 남긴 버전입니다. 실행 가능한 완전한 구현체가
> 아닙니다.

## 기술 스택
- Spring Boot 2.7 / JDK 11
- MyBatis + Spring JDBC(JdbcTemplate)
- Jasypt (설정파일 암호화)
- 지원 DB: MSSQL / Oracle / PostgreSQL / MariaDB / MySQL

## 구조

```
src/main/java/com/app/daol
├── DaolsbApplication.java     # 부트 진입점, 스케줄러 활성화
├── config/
│   ├── JasyptConfig.java              # 설정파일 암복호화 빈 등록
│   ├── SqlSessionConfigOriginal.java  # 원본 DB 커넥션/세션 설정
│   └── SqlSessionConfigTarget.java    # 대상 DB 커넥션/세션 설정
├── main/
│   └── MainSchedule.java      # cron 기반 동기화 배치 (사용자/조직)
├── service/
│   ├── OriginalDatabaseService.java   # 원본 DB 조회
│   └── TargetDatabaseService.java     # 대상 DB 조회/등록/수정
├── mapper/
│   ├── original/Original.java # MyBatis 매퍼 (원본)
│   └── target/Target.java     # MyBatis 매퍼 (대상)
├── dto/ColumnDto.java          # 조회 결과 매핑 DTO
└── util/Aes.java                # 설정값 복호화용 AES 유틸(키는 환경변수 주입)
```

## 동작 개요
1. 스케줄러가 cron 주기로 실행되어 원본 DB에서 사용자/조직 목록을 조회한다.
2. 대상 DB의 목록과 비교하여 신규는 INSERT, 변경분은 UPDATE, 원본에 없는 대상은
   비활성화(soft delete) 처리한다.
3. DB 접속정보 및 세부 쿼리는 `application.properties`(환경변수로 치환)와
   MyBatis 매퍼 XML로 분리되어 있다.

## 접속정보/키 관리
- DB 계정, 비밀번호, jasypt 마스터 키, AES 키는 **코드/설정파일에 하드코딩하지
  않고** 배포 환경의 환경변수 또는 별도 시크릿 매니저에서 주입한다.
- `application.properties`는 예시일 뿐이며 실제 값은 `.env`/환경변수로 채운다
  (`.gitignore`에서 제외).

## 미포함 항목 (보안상 비공개)
- 실제 서버 IP/포트, 계정, 비밀번호
- Jasypt 마스터 키, AES 대칭키
- 실제 테이블/컬럼명이 포함된 조회·등록·수정 쿼리
- 사내 조직/사용자 스키마 세부사항
