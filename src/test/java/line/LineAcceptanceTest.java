package line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import subway.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {

  @DisplayName("지하철 노선을 생성하여 새로운 노선을 추가한다.")
  @Test
  public void testCreateLine(){
    //도메인 제약 테스트를 추가하기 위해 노선의 조건에서 색상이 중복되지 않고 거리가 100이하여야 한다고 가정한다

    //given 역을 생성하고
    ExtractableResponse<Response> createStationResponse = RestAssured.given().log().all()
      .body(new StationRequest("종합운동장"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract();

    ExtractableResponse<Response> createStationResponse2 = RestAssured.given().log().all()
      .body(new StationRequest("잠실새내"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract();

    //when 두개의 역에 대한 지하철 노선을 추가한다.
    ExtractableResponse<Response> createLineResponse = RestAssured.given().log().all()
      .body(new LineCreateRequest("2호선", "green", 1L, 2L, 10))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines")
      .then().log().all()
      .extract();

    //then
    // 이미 노선이 있는지 확인한다.
    ExtractableResponse<Response> showResponse = RestAssured.given().log().all()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().get("/lines")
      .then().log().all()
      .extract();

    assertThat(showResponse.body().jsonPath().getList("name", String.class)).size().isEqualTo(1);

    // 노선이 있지 않다면 두개의 역에 대한 지하철 노선이 추가되었는지 확인한다.
    assertThat(createStationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(createLineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(createLineResponse.body().jsonPath().getList("stations", Long.class)).size().isEqualTo(2);
    assertThat(createLineResponse.body().jsonPath().getList("stations", Long.class).get(0)).isEqualTo(1L);
    assertThat(createLineResponse.body().jsonPath().getList("stations", Long.class).get(1)).isEqualTo(2L);
    //첫번째 이름 종합운동장인지, 두번째 이름 잠실새내역인지 확인
    assertThat(createLineResponse.body().jsonPath().getString("stations[0].name")).isEqualTo("종합운동장");
    assertThat(createLineResponse.body().jsonPath().getString("stations[1].name")).isEqualTo("잠실새내");
    assertThat(createLineResponse.body().jsonPath().getString("name")).isEqualTo("2호선");
    assertThat(createLineResponse.body().jsonPath().getString("color")).isEqualTo("green");
    assertThat(createLineResponse.body().jsonPath().getLong("distance")).isEqualTo(10);

  }

  @DisplayName("지하철 노선을 생성하여 목록을 조회한다.")
  @Test
  public void testShowLines(){
    //given
    ExtractableResponse<Response> createStationResponse = RestAssured.given().log().all()
      .body(new StationRequest("종합운동장"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract();

    ExtractableResponse<Response> createStationResponse2 = RestAssured.given().log().all()
      .body(new StationRequest("잠실새내"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract();

    ExtractableResponse<Response> createStationResponse3 = RestAssured.given().log().all()
      .body(new StationRequest("잠실"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract();

    ExtractableResponse<Response> createStationResponse4 = RestAssured.given().log().all()
      .body(new StationRequest("선릉"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract();

    ExtractableResponse<Response> createLineResponse = RestAssured.given().log().all()
      .body(new LineCreateRequest("2호선", "green", 1L, 2L, 10))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines")
      .then().log().all()
      .extract();

    ExtractableResponse<Response> createLineResponse2 = RestAssured.given().log().all()
      .body(new LineCreateRequest("2호선", "green", 2L, 3L, 50))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines")
      .then().log().all()
      .extract();

    ExtractableResponse<Response> createLineResponse3 = RestAssured.given().log().all()
      .body(new LineCreateRequest("2호선", "green", 3L, 4L, 10))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines")
      .then().log().all()
      .extract();


    //when
    ExtractableResponse<Response> showResponse = RestAssured.given().log().all()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().get("/lines")
      .then().log().all()
      .extract();

    //then
    assertThat(showResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(showResponse.body().jsonPath().getList("name", String.class)).size().isEqualTo(1);
    assertThat(showResponse.body().jsonPath().getString("name")).isEqualTo("2호선");
    assertThat(showResponse.body().jsonPath().getString("color")).isEqualTo("green");
    assertThat(showResponse.body().jsonPath().getList("stations", Long.class)).size().isEqualTo(4);
    assertThat(showResponse.body().jsonPath().getList("stations", Long.class).get(0).getId()).isEqualTo(1L);
    assertThat(showResponse.body().jsonPath().getList("stations", Long.class).get(1).getId()).isEqualTo(2L);
    assertThat(showResponse.body().jsonPath().getList("stations", Long.class).get(2).getId()).isEqualTo(3L);
    assertThat(showResponse.body().jsonPath().getList("stations", Long.class).get(3).getId()).isEqualTo(4L);

    assertThat(showResponse.body().jsonPath().getString("stations[0].name")).isEqualTo("종합운동장");
    assertThat(showResponse.body().jsonPath().getString("stations[1].name")).isEqualTo("잠실새내");
  }


  @DisplayName("지하철 특정 노선의 정보를 조회한다.")
  @Test
  public void testShowLineInfo(){

    //given
    ExtractableResponse<Response> createStationResponse = RestAssured.given().log().all()
      .body(new StationRequest("종합운동장"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract();

    ExtractableResponse<Response> createStationResponse2 = RestAssured.given().log().all()
      .body(new StationRequest("잠실새내"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract();

    ExtractableResponse<Response> createLineResponse = RestAssured.given().log().all()
      .body(new LineCreateRequest("2호선", "green", 1L, 2L, 10))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines")
      .then().log().all()
      .extract();

    //when
    ExtractableResponse lineOneInfo = RestAssured.given().log().all()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().get("/lines/1")
      .then().log().all()
      .extract();

    //then
    assertThat(lineOneInfo.statusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(lineOneInfo.body().jsonPath().getString("name")).isEqualTo("2호선");
    assertThat(lineOneInfo.body().jsonPath().getString("color")).isEqualTo("green");
    assertThat(lineOneInfo.body().jsonPath().getLong("distance")).isEqualTo(10);
    assertThat(lineOneInfo.body().jsonPath().getList("stations", Long.class)).size().isEqualTo(2);
    assertThat(lineOneInfo.body().jsonPath().getList("stations", Long.class).get(0)).isEqualTo(1L);
    assertThat(lineOneInfo.body().jsonPath().getList("stations", Long.class).get(1)).isEqualTo(2L);
    assertThat(lineOneInfo.body().jsonPath().getString("stations[0].name")).isEqualTo("종합운동장");
    assertThat(lineOneInfo.body().jsonPath().getString("stations[1].name")).isEqualTo("잠실새내");
  }


  @DisplayName("지하철 노선을 수정한다.")
  @Test
  public void testModifyLine(){
    //given
    ExtractableResponse<Response> createStation1Response = RestAssured.given().log().all()
      .body(new StationRequest("종합운동장"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract();

    ExtractableResponse<Response> createStation2Response = RestAssured.given().log().all()
      .body(new StationRequest("잠실새내"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract();

    ExtractableResponse<Response> createLineResponse = RestAssured.given().log().all()
      .body(new LineCreateRequest("2호선", "green", 1L, 2L, 10))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines")
      .then().log().all()
      .extract();

    //when
    ExtractableResponse lineModifyResponse = RestAssured.given().log().all()
      .body(new LineModifyRequest("새로운 2호선", "blue"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().put("/lines/1")
      .then().log().all()
      .extract();

    //then
    assertThat(lineModifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

    assertThat(createStation1Response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(createStation2Response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(createLineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
  }
}
