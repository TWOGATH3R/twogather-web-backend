
<p align="center">
  <img src="https://github.com/TWOGATH3R/.github/assets/66842566/39335476-6b15-4ff3-be52-be5b47dbbd10">
</p>

## 맛집 등록/검색 웹 프로젝트 `TwoGather`
- 맛집을 등록하고 키워드를 통해 검색할 수 있습니다<br>
- 필터링을 통하여 맛집을 추천받을 수 있고, 카테고리별로 맛집을 찾아볼 수 있습니다.

### 프로젝트 배경
- 가고 싶은 맛집을 수식어를 통해 검색하고 싶을때가 있었습니다. TwoGather을 사용하여 카테고리별로/키워드별로 맛집을 검색하여 추천받을 수 있습니다.
- 리뷰어의 평균리뷰점수를 확인하는 기능을 넣어 신뢰성있는 리뷰를 제공받을 수 있습니다.
- 기존에 만들어봤던 프로젝트에서의 부족한점을 보완하여 개발하고 싶었습니다.
  <br>부끄럽지만, 이전 프로젝트에서는 서버쪽에서의 validation 확인의 필요성이나 로깅의 필요성, test의 중요성에 대해 잘 알지 못했습니다
  <br>TwoGather 프로젝트를 진행하면서 validation의 적절한 적용, 로그를 작성하고, 로그분석을 통한 시스템 장애 대응, 좋은 test코드에 대해 생각해보고, test 자동화를 하는 부분에 신경써가며 작업하려고합니다

### 로컬에서 작동시키는 법
- 프로젝트 실행과 빌드를 위해선 /src/main/resources, /src/test/resources하위에 application.properties 파일이 존재해야합니다
- 현재는 프로파일관리로 배포시엔 "prod"라는 설정을 활성화 시키거나 개발시엔 "dev"라는 설정을 활성화시키면서 개발을 하고 있습니다
- 그렇기에 로컬에서 실행시키려면 application.properties파일내용안에(없다면 만들어야합니다) 프로젝트 내에 존재하는 다른 xxx.properties파일에서 작성된 요구하는 값에 대해 copy하여 빈 부분에 대해서 채워주셔야합니다

### CI / CD
![image](https://github.com/TWOGATH3R/twogather-web-backend/assets/66842566/d96d47be-3da6-48df-9b2b-4260815f4f16)

### Teck stack
![image](https://github.com/TWOGATH3R/twogather-web-backend/assets/66842566/1c217223-40a6-4e14-afc4-a1be216041fa)

### 한계점
- oAuth 적용 목표
### 문서
[위키](https://github.com/TWOGATH3R/twogather-web-backend/wiki)

# 팀원
| Backend 🌟 | Backend 🌟 | Frontend 🌟 | Frontend 🌟 |
| :-----: | :-----: | :-----: | :------: |
| <img src="https://github.com/TWOGATH3R/.github/assets/66842566/5c881f2e-c0a8-43dd-a301-51865d24deac" width=400px height=200px  alt="민지"/> | <img src="https://github.com/TWOGATH3R/.github/assets/66842566/174fbbed-dbba-4cfc-8c71-12fe15008521" width=400px height=200px alt="지호"/> | <img src="https://github.com/TWOGATH3R/.github/assets/66842566/f85e58c9-126d-4710-9253-269bc77e0bf8" width=400px height=200px alt="태욱"/> | <img src="https://github.com/TWOGATH3R/.github/assets/66842566/5c881f2e-c0a8-43dd-a301-51865d24deac" width=400px height=200px  alt="예정"> |
|                       [민지](https://github.com/Flre-fly)                        |                            [지호](https://github.com/J-I-H-O)                            |                            [태욱](https://github.com/taewok)                            |                          [예정](https://github.com/bananana0118)                 |
