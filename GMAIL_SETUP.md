# Gmail SMTP 설정 가이드

## 1. Gmail 2단계 인증 활성화

1. [Google 계정 설정](https://myaccount.google.com/)에 접속
2. "보안" 탭 클릭
3. "2단계 인증" 활성화

## 2. 앱 비밀번호 생성

1. [Google 계정 설정](https://myaccount.google.com/) → "보안"
2. "2단계 인증" 클릭
3. "앱 비밀번호" 클릭
4. "앱 선택" → "기타(맞춤 이름)"
5. 앱 이름 입력 (예: "세종족보")
6. "생성" 클릭
7. 생성된 16자리 앱 비밀번호 복사

## 3. application.yml 설정

`src/main/resources/application.yml` 파일에서 다음 부분을 수정:

```yaml
spring:
  mail:
    username: your-email@gmail.com  # 실제 Gmail 주소로 변경
    password: your-app-password     # 위에서 생성한 16자리 앱 비밀번호로 변경
```

## 4. 보안 주의사항

- 앱 비밀번호는 절대 공개 저장소에 커밋하지 마세요
- 실제 운영 환경에서는 환경변수나 별도 설정 파일을 사용하세요
- 앱 비밀번호는 정기적으로 재생성하는 것을 권장합니다

## 5. 테스트

설정 완료 후 애플리케이션을 실행하고 족보 등록 페이지에서 이메일 인증을 테스트해보세요. 