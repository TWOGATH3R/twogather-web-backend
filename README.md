## 맛집 등록/검색 프로젝트 `TwoGather`

- 맛집을 등록하고 검색할 수 있습니다<br>
- 키워드를 통하여 맛집을 추천받을 수 있고, 카테고리별로 맛집을 찾아볼 수 있습니다.

### 프로젝트 배경
- 그 맛집을 표현하는 수식어를 통해 맛집을 검색하고 싶을때가 있다. TwoGather을 사용하면 카테고리별로/키워드별로 맛집을 검색하여 추천받을 수 있다.
- 리뷰어의 평균리뷰점수를 확인하는 기능을 넣어 조금 더 신뢰성있는 리뷰목록을 만들고 싶었다.
- 기존에 만들어봤던 프로젝트에서의 부족한점을 보완하여 개발하고 싶었다.
  <br>부끄럽지만, 이전 프로젝트에서는 서버쪽에서의 validation 확인의 필요성이나 로그의 필요성, test의 중요성에 대해 잘 알지 못했다.
  <br>TwoGather 프로젝트를 진행하면서 validation의 적절한 적용, 로그를 작성하고, 로그분석을 통해 시스템 장애 대응, 좋은 test코드에 대해 생각해보고, test 자동화를 하는 부분에 신경써가며 작업하려고한다.

### 로컬에서 작동시키는 법
- 프로젝트 실행과 빌드를 위해선 /src/main/resources, /src/test/resources하위에 application.properties 파일이 존재해야합니다
- 현재는 프로파일관리로 배포시엔 "prod"라는 설정을 활성화 시키거나 개발시엔 "dev"라는 설정을 활성화시키면서 개발을 하고 있습니다
- 그렇기에 로컬에서 실행시키려면 application.properties파일내용안에(없다면 만들고) 프로젝트 내에 존재하는 다른 xxx.properties파일에서 작성된 요구하는 값에 대해 copy하여 빈 부분에 대해서는 채워주셔야합니다

### 아키텍쳐 설명
![image](https://github.com/TWOGATH3R/twogather-web-backend/assets/66842566/b94260eb-0949-4e7a-8764-223907507033)
1. 코드를 작성한 뒤, Github에 push를 한다.
2. master 브랜치에 push가 발생하면, Github Actions이 실행된다.
3. Github Actions는 빌드를 하여 코드에 문제가 없는지 확인한다.
4. Github Actions는 프로젝트 파일을 압축하여 AWS S3로 전송하고, CodeDeploy에게 배포를 요청한다.
5. CodeDeploy는 S3로부터 zip 파일을 받아 배포를 진행한다.

### 한계점

### 문서

#### wiki
- https://github.com/Flre-fly/twogather-web-backend/wiki
#### 기능명세서
- https://www.notion.so/85e28c236dd344f6814d02246d59774b?v=3f357303b02143e3bfc3c6fce2f64ad6

#### API Spec
- https://www.notion.so/33ed04558f434dc5bd9f0d8f8637b409?v=e1ff8bdf3f164428b9263244a137d54c
- 가장 최신화된 문서: `src\main\resources\static\docs\index.html` 참고
- ![image](https://user-images.githubusercontent.com/66842566/236997177-46160a24-f9db-4bbc-8d0c-cb7f7f6533c3.png)
- 다운로드 버튼을 누르고 바로 html문서를 확인할 수 있습니다

#### 기획안 
- https://www.notion.so/74a37c10ea994f63b2bfd617d508f2bf

#### DB설계
- https://www.erdcloud.com/d/mNvRpSez4rT7GZBKh
![image](https://user-images.githubusercontent.com/66842566/233318029-9a21ecaf-0631-4df0-a446-404a4c42e6ad.png)

...
