# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

---

### 기능 요구사항
- 지하철역 인수 테스트를 완성하세요.
  - [X] 지하철역 목록 조회 인수 테스트 작성하기
  - [X] 지하철역 삭제 인수 테스트 작성하기
  - [ ] `요구사항 설명` 에서 제공되는 인수 조건을 기반으로 지하철 노선 관리 기능 구현하기
    - [X] 지하철 노선 생성 기능
    - [X] 지하철 노선 목록 조회 기능
    - [X] 지하철 노선 조회 기능
    - [ ] 지하철 노선 수정 기능
    - [ ] 지하철 노선 삭제 기능
  - [ ] 인수 조건을 검증하는 인수 테스트를 작성하기
    - [X] 지하철 노선 생성
    - [X] 지하철 노선 목록 조회
    - [X] 지하철 노선 조회
    - [ ] 지하철 노선 수정
    - [ ] 지하철 노선 삭제
  - [X] 인수 테스트 격리

### 프로그래밍 요구사항
- 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수테스트를 리팩토링 하세요.
- 아래 순서로 기능을 구현하세요.
  - 인수 조건을 검증하는 인수 테스트 작성
  - 인수 테스트를 충족하는 기능 구현
- 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리시키세요.

### 요구사항 설명
- 인수조건
  - 지하철노선 생성
    > `Given` 지하철 노선을 생성하면   
    `When` 지하철 노선 목록을 조회 시 생성한 노선을 찾을 수 있다.  
  - 지하철노선 목록 조회
    > `Given` 2개의 지하철 노선을 생성하고   
    `When` 지하철 노선 목록을 조회하면   
    `Then` 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
  - 지하철 노선 조회
    > `Given` 지하철 노선을 생성하고   
    `When` 생성한 지하철 노선을 조회하면   
    `Then` 생성한 지하철 노선의 정보를 응답받을 수 있다.
  - 지하철 노선 수정
    > `Given` 지하철 노선을 생성하고   
    `When` 생성한 지하철 노선을 수정하면   
    `Then` 해당 지하철 노선 정보는 수정된다.
  - 지하철 노선 삭제
    > `Given` 지하철 노선을 생성하고   
    `When` 생성한 지하철 노선을 삭제하면   
    `Then` 해당 지하철 노선 정보는 삭제된다.