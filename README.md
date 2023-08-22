
<p align="center">
  <img src="https://github.com/TWOGATH3R/.github/assets/66842566/39335476-6b15-4ff3-be52-be5b47dbbd10">
</p>

# 맛집 등록/검색 웹 프로젝트
- 맛집을 등록하고 키워드를 통해 검색할 수 있습니다<br>
- 필터링을 통하여 맛집을 추천받을 수 있고, 카테고리별로 맛집을 찾아볼 수 있습니다.

## 프로젝트 목표
1. **유연한 설계와 확장성 높은 코드로 유지보수성 높이기**
    - 개발을 하다보면 중복된 코드를 사용할 때가 있습니다. 중복된 코드를 하나의 메서드로 묶어 중복을 최소화하도록 진행하였습니다.
    - 결합을 느슨하게 하기 위해 자율적인 객체를 만들려고 노력하였습니다.
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
      
## 기술이슈와 해결방법
- 자세한 내용은 [위키](https://github.com/TWOGATH3R/twogather-web-backend/wiki)에 나와있습니다.

**1. 동시성 문제**
   - 동일한 하나의 데이터에 2 이상의 스레드에서 데이터를 동시에 제어할 때 나타는 문제로,
   - 하나의 스레드가 데이터를 수정 중일때, 다른 스레드에서 수정 전의 데이터를 조회해 로직을 처리함으로써 데이터의 정합성이 깨지는 문제를 말합니다.
   - 자바는 멀티스레드를 지원하기 때문에 이런 문제에 대해 생각을 해보는 과정이 필요하다고 생각했습니다
   - 현재 프로젝트에서는 다음로직에 대해 동시성 문제가 생길 수 있습니다.
   ```
    1. 가게 이름 중복에 대한 유효성 검사
    2. 가게 업데이트
   ```
   - 트랜잭션A가 가게 업데이트를 수행하는데 트랜잭션 B가 똑같은 이름으로 가게 유효성 검사를 수행하면 유효성 검사를 통과하게 되면서 데이터의 정합성이 깨질 수 있습니다
   - 하지만 락을 거는 방법보다 간단한 방법인 디비에 제약조건을 걸어서 해결하는 방식으로 문제를 해결하였습니다.
   - [문서](https://github.com/TWOGATH3R/twogather-web-backend/wiki/%EB%8F%99%EC%8B%9C%EC%84%B1-%EB%AC%B8%EC%A0%9C)
     
**2. 스레드 로컬을 사용해서 Exception이 Throw됐을 때만 로그 출력하기**
   - 필터에서 모든 경우에 대해 로그를 출력할 수도 있지만 주로 문제상황을 해결하기 위해 로그를 확인했었기 때문에 문제 상황에 대한 로그만 추출하고 싶었습니다.
   - 이때, http request는 입력스트림을 사용해서 데이터를 읽습니다. 한번읽으면 다시 읽을 수가 없죠.
   - Exception이 Throw됐을때 다시 한번 데이터를 읽어서 로그를 출력하기 위해 스레드 로컬을 사용하였습니다
   - 스레드 로컬이란, 스레드 당 로컬변수를 할당할 수 있게 도와주는 클래스이빈다.
   - 각 요청의 정보를 스레드 로컬에 저장하고 만약 Exception이 Throw되는 경우 저장하고 있던 정보를 로그로 출력하도록 문제를 해결하였습니다.
   - [문서](https://github.com/TWOGATH3R/twogather-web-backend/wiki/%EB%A1%9C%EA%B7%B8-%EB%B6%84%EC%84%9D)

      
**3. 인수테스트에서 테스트 격리하기**
   - 인수테스트를 할땐 보통 랜덤포트를 쓰게 됩니다. 이 경우 서버측과 클라이언트 측이 분리됩니다
   - 결국 HTTP 경계를 넘어 요청을 처리하는 서버 측 코드로 트랜잭션이 확장되지 않습니다.
   - 무언가가 처리된 부분은 서버측 스레드이기때문에 그쪽이 롤백되어야 데이터가 롤백되어 테스트 격리를 달성할 수 있습니다.
   - 그런데 롤백이 http클라이언트쪽에만 처리가 되니 제대로 롤백이 되지 않는 문제가 생깁니다.
   - 문제 해결을 위해 롤백대신 실제 sql을 날려서 데이터베이스를 비워주는 클래스를 작성하였습니다
   - [문서](https://github.com/TWOGATH3R/twogather-web-backend/wiki/%EC%9D%B8%EC%88%98%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%97%90%EC%84%9C-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EA%B2%A9%EB%A6%AC%ED%95%98%EA%B8%B0)

**4. 테스트 커버리지**
   - 다양한 상황을 대비하기 위해 테스트 코드를 작성하고 커버리지를 확인하는 도구를 활용하여 커버리지를 측정하였습니다
   - **테스트 커버리지 81%**
   - [문서](https://github.com/TWOGATH3R/twogather-web-backend/wiki/Jacoco-%EB%8F%84%EC%9E%85%ED%95%98%EC%97%AC-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BB%A4%EB%B2%84%EB%A6%AC%EC%A7%80-%EC%B8%A1%EC%A0%95)
  
**5. 쿼리 튜닝**
   - 가장 많이 사용될 것으로 예상되는 쿼리를 선정하고 그 쿼리에 대해 튜닝 작업을 진행하였습니다.
   - 필요없는 조인을 제거하고 인덱스를 효과적으로 도입하여 **8배** 정도 빨라짐을 확인하였습니다
   - [문서](https://github.com/TWOGATH3R/twogather-web-backend/wiki/%EC%B5%9C%EC%A0%81%ED%99%94)

## 프로젝트 한계
- 자바8 문법 일부가 적용이 되지 않은 점
- 코드 리뷰가 빈약한 점
- 캐시 적용시 적절한 만료시간을 찾아 설정하지 못한 점


## CI / CD
![image](https://github.com/TWOGATH3R/twogather-web-backend/assets/66842566/d96d47be-3da6-48df-9b2b-4260815f4f16)

## Teck stack
![image](https://github.com/TWOGATH3R/twogather-web-backend/assets/66842566/1c217223-40a6-4e14-afc4-a1be216041fa)

## ERD
![image](https://github.com/TWOGATH3R/twogather-web-backend/assets/66842566/20874d74-f976-40af-9b27-1472106ba7f5)

## 팀원
| Backend 🌟 | Backend 🌟 | 
| :-----: | :-----: | 
| <img src="https://github.com/TWOGATH3R/.github/assets/66842566/5c881f2e-c0a8-43dd-a301-51865d24deac" width=400px height=300px  alt="민지"/> | <img src="https://github.com/TWOGATH3R/.github/assets/66842566/174fbbed-dbba-4cfc-8c71-12fe15008521" width=400px height=300px alt="지호"/> | <img src="https://github.com/TWOGATH3R/.github/assets/66842566/f85e58c9-126d-4710-9253-269bc77e0bf8" width=400px height=300px alt="태욱"/> | 
|                       [민지](https://github.com/Flre-fly)                        |                            [지호](https://github.com/J-I-H-O)                            |                            
