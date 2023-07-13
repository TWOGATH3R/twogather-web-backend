
<p align="center">
  <img src="https://github.com/TWOGATH3R/.github/assets/66842566/39335476-6b15-4ff3-be52-be5b47dbbd10">
</p>

# 맛집 등록/검색 웹 프로젝트
- 맛집을 등록하고 키워드를 통해 검색할 수 있습니다<br>
- 필터링을 통하여 맛집을 추천받을 수 있고, 카테고리별로 맛집을 찾아볼 수 있습니다.
# 동작

|로그인|검색|
|:-:|:-:|
|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/303270ec-f865-4d9a-ab22-b4d44c160e72.gif>|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/832b6f00-c1e6-44f0-b9de-31ed05457062.gif>|
|회원가입|비밀번호찾기|
|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/8b83a61a-948e-41da-ae39-57d36c018fc9.gif>|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/af42a7fc-66c9-4c5a-ae42-2843751c8a03.gif>|
|아이디 찾기|ㅡㅡ|
|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/1912d42b-26cb-48bc-b1d8-9a5f3acacb6b.gif>||
|개인회원 마이페이지|가게 주인 마이페이지|
|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/fd8e5746-6fff-4994-9c00-f09f33c176da.gif>|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/b8494fa0-56b8-4a51-a4af-72fec9162156.gif>|
|관리자 마이페이지|관리자 승인/거부|
|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/a119195e-d54c-4a2c-a95c-e1073c262eae.gif>|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/ceff61cf-70b3-4e6b-ae49-2ad41556e9f7.gif>|
|가게 등록|가게주인 대댓글 남기기|
|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/bccee6fc-a390-48e0-b49d-12fb929631c4.gif>|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/4fb2ecbc-58a7-43a6-a749-5a0dc36db9b1.gif>|
|가게 상세 보기|리뷰 달기|
|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/b3efb2e6-d37b-4f49-8e87-d10e3e1f28a7.gif>|<img src=https://github.com/TWOGATH3R/.github/assets/66842566/a2231219-b886-4fa0-a39b-e3c09f5b2d8f.gif>|

## 프로젝트 배경
- 가고 싶은 맛집을 수식어를 통해 검색하고 싶을때가 있었습니다. TwoGather을 사용하여 카테고리별로/키워드별로 맛집을 검색하여 추천받을 수 있습니다.
- 리뷰어의 평균리뷰점수를 확인하는 기능을 넣어 신뢰성있는 리뷰를 제공받을 수 있습니다.
- 기존에 만들어봤던 프로젝트에서의 부족한점을 보완하여 개발하고 싶었습니다.
- 부끄럽지만, 이전 프로젝트에서는 서버쪽에서의 validation 확인의 필요성이나 로깅의 필요성, test의 중요성에 대해 잘 알지 못했습니다
- TwoGather 프로젝트를 진행하면서 validation의 적절한 적용, 로그를 작성하고, 로그분석을 통한 시스템 장애 대응, 좋은 test코드에 대해 생각해보고, test 자동화를 하는 부분에 신경써가며 작업하려고합니다

## 프로젝트 목표
1. **유연한 설계와 확장성 높은 코드로 유지보수성 높이기**
    - 개발을 하다보면 중복된 코드를 사용할 때가 있습니다. 중복된 코드를 하나의 메서드로 묶어 중복을 최소화하도록 진행하였습니다.
    - 결합을 느슨하게 하기 위해 자율적인 객체를 만들려고 노력하였습니다.
    - `자율적인 객체`: 상태와 행위를 가지고 자기 자신을 스스로 책임지는 객체. 외부에 의해 상태를 조절받는 것이 아닌 자신이 직접 상태를 조절하는 것.
2. **테스트 코드를 통해 개발한 코드의 신뢰성 높이기**
    - 잘 작성한 테스트 코드들은 실제 안정적인 수행을 보장해줍니다.
    - 기능이 수정/삭제/추가 되더라도 다른 코드에 영향이 가지 않는지 확인하고 안정적인 코드를 제공하도록 도와줍니다
3. **문서화**
    - 개발 과정에서 겪은 문제들을 문서화 합니다.
    - 협업하는 과정에서 다른 인원이 추가로 들어온다고 하더라도 잘 정리된 문서를 보고 보다 쉽게 협업할 수 있습니다.
    - 협업하는 과정에서 필요한 API spec등을 RestDocs로 정의하여 프론트엔드와 협업합니다.
    - [위키](https://github.com/TWOGATH3R/twogather-web-backend/wiki)
4. **배포 자동화**
    - 자동화 된 배포는 새로운 기능 및 수정사항을 적용하는데 걸리는 시간을 크게 줄일 수 있습니다.
    - 배포가 자동화 되기때문에 개발자는 수동 배포 작업을 처리하는 대신 코드 작성에 더 집중할 수 있습니다
5. **동시성 문제**
    - 동시성 문제는 예측할 수 없는 동작, 데이터 불일치 또는 시스템 오류로 이어질 수 있으므로 확인하는 것이 중요합니다.
    - 동시성 문제에 대해 고민해보고 이러한 문제가 생길 수 있는지 여부를 검토해봅니다

   
## 로컬에서 작동시키는 법
- 프로젝트 실행과 빌드를 위해선 /src/main/resources, /src/test/resources하위에 application.properties 파일이 존재해야합니다
- 현재는 프로파일관리로 배포시엔 "prod"라는 설정을 활성화 시키거나 개발시엔 "dev"라는 설정을 활성화시키면서 개발을 하고 있습니다
- 그렇기에 로컬에서 실행시키려면 application.properties파일내용안에(없다면 만들어야합니다) 프로젝트 내에 존재하는 다른 xxx.properties파일에서 작성된 요구하는 값에 대해 copy하여 빈 부분에 대해서 채워주셔야합니다

## CI / CD
![image](https://github.com/TWOGATH3R/twogather-web-backend/assets/66842566/d96d47be-3da6-48df-9b2b-4260815f4f16)

## Teck stack
![image](https://github.com/TWOGATH3R/twogather-web-backend/assets/66842566/1c217223-40a6-4e14-afc4-a1be216041fa)

## ERD
![image](https://github.com/TWOGATH3R/twogather-web-backend/assets/66842566/20874d74-f976-40af-9b27-1472106ba7f5)


## 한계점
- 인덱스를 활용한 쿼리 튜닝
- 성능 테스트

## 팀원
| Backend 🌟 | Backend 🌟 | 
| :-----: | :-----: | 
| <img src="https://github.com/TWOGATH3R/.github/assets/66842566/5c881f2e-c0a8-43dd-a301-51865d24deac" width=400px height=300px  alt="민지"/> | <img src="https://github.com/TWOGATH3R/.github/assets/66842566/174fbbed-dbba-4cfc-8c71-12fe15008521" width=400px height=300px alt="지호"/> | <img src="https://github.com/TWOGATH3R/.github/assets/66842566/f85e58c9-126d-4710-9253-269bc77e0bf8" width=400px height=300px alt="태욱"/> | 
|                       [민지](https://github.com/Flre-fly)                        |                            [지호](https://github.com/J-I-H-O)                            |                            
