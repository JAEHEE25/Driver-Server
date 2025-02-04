# 성공적인 코딩 테스트를 위한 최적의 경로, Codrive

![코드라이브 내러티브](https://github.com/user-attachments/assets/f25f7608-e3da-4632-b3ec-00b6247b4060)


<br>

## 프로젝트 설명

**[ 서비스 URL ]** [https://www.codrive.co.kr](https://www.codrive.co.kr)

**[ 소개 ]** 꾸준한 코딩테스트 준비를 돕는 알고리즘 문제 풀이 기록 및 공유 서비스

**[ 팀 구성 ]** 디자이너 2명, 프론트엔드 개발자 3명, 백엔드 개발자 1명

**[ 기간 ]** 2024.06 ~ ing

<br><br>

## 기술 스택
#### Backend
`Java`, `Spring Boot`, `Spring Security`

`Spring Data JPA`, `QueryDSL`

`JUnit5`, `Mockito`

#### Database
`MySQL`, `Redis`

#### Infrastructure
`Docker`, `Docker-Compose`, `Amazon EC2`, `Amazon S3`

`Prometheus`, `Grafana`, `Grafana Loki`

<br><br>

## 아키텍처

![archi](https://github.com/user-attachments/assets/ff58ed00-1df8-4fdd-bdc1-b5a6ae5e04c2)

<br><br>

## 주요 기능 구현

**[ API 명세서 ]** [https://api.codrive.co.kr/swagger-ui/index.html](https://api.codrive.co.kr/swagger-ui/index.html)

#### Github 소셜 로그인 및 자동 연동 기능
- `OAuth 2.0`, `JWT`, `Spring Security`를 이용해 Github 소셜 로그인을 통한 인증 로직을 개발했습니다.
- 회원가입 단계에서 원하는 Github Repository를 설정할 수 있으며, 문제 풀이를 등록 및 수정할 시 자동으로 Github에 커밋됩니다.
- 커밋 기능에 활용하는 Github Token과 애플리케이션 인증 로직에 필요한 Refresh Token을 `Redis`에 저장해 관리합니다.

#### 팔로우 및 그룹을 통한 네트워킹 기능
- `QueryDSL`을 활용한 동적 쿼리로 일간/주간/월간 문제 풀이 현황을 제공합니다.
- 팔로우를 통해 사용자 간 문제 풀이 현황을 공유할 수 있습니다.
- 대표 이미지/사용 언어/인원 수/그룹 공개 여부 등의 커스터마이징을 통해 그룹을 생성하고 관리할 수 있습니다.
- 참여 요청 승인 및 내보내기 기능을 통해 그룹 스터디를 원활하게 관리할 수 있습니다.
    
#### 실시간 알림 기능
- `SSE(Server-Sent-Events)` 방식으로 구현하여, 팔로우 및 그룹 관련 이벤트 발생 시 실시간으로 알림을 발송합니다.

#### 모니터링
- 안정적인 서비스 운영을 위해 별도의 `Amazon EC2` 인스턴스에 `Grafana`, `Prometheus`, `Grafana Loki`를 설정하여 HTTP 요청 및 응답, 애플리케이션 로그, 서버 리소스 등을 모니터링하고 있습니다.


<br><br>

## ERD

![erd](https://github.com/user-attachments/assets/f9b38845-d4c2-4744-8a9e-9f50ab3cb756)



