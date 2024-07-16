package subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import subway.StationRequest;
import subway.line.dto.LineCreateRequest;
import subway.line.dto.LineModifyRequest;
import subway.line.dto.LineSectionAppendRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@Sql(scripts = "classpath:truncate-tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {

  @DisplayName("지하철의 새로운 노선을 추가한다.")
  @Test
  public void testCreateLine(){
    //도메인 제약 테스트를 추가하기 위해 노선의 조건에서 색상이 중복되지 않고 거리가 100이하여야 한다고 가정한다

    //given 역을 생성하고
    ExtractableResponse<Response> createStationResponse = 지하철_역을_생성("종합운동장");
    ExtractableResponse<Response> createStationResponse2 = 지하철_역을_생성("잠실새내");

    //when 두개의 역에 대한 지하철 노선을 추가한다.
    ExtractableResponse<Response> createLineResponse =
      지하철_노선을_생성("2호선", "green", 1L, 2L, 10);

    //then
    assertThat(createStationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(createStationResponse2.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(createLineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
  }

  @DisplayName("지하철 노선 목록을 조회한다.")
  @Test
  public void testShowLines() {
    //given
    지하철_역을_생성("종합운동장");
    지하철_역을_생성("잠실새내");
    지하철_역을_생성("잠실");
    지하철_역을_생성("선릉");
    지하철_노선을_생성("2호선", "green", 1L, 2L, 10);
    지하철_노선을_생성("2호선", "green", 2L, 3L, 50);
    지하철_노선을_생성("2호선", "green", 3L, 4L, 10);

    //when
    ExtractableResponse<Response> showResponse = RestAssured.given().log().all()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().get("/lines")
      .then().log().all()
      .extract();

    //then
    assertThat(showResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    assertAll(
      "지하철 노선 목록 조회 아이디 값 확인",
      () -> assertThat(showResponse.body().jsonPath().getList("stations").size()).isEqualTo(3),
      () -> assertThat(showResponse.body().jsonPath().getLong("stations[0].id[0]")).isEqualTo(1L),
      () -> assertThat(showResponse.body().jsonPath().getLong("stations[0].id[1]")).isEqualTo(2L),
      () -> assertThat(showResponse.body().jsonPath().getLong("stations[1].id[0]")).isEqualTo(2L),
      () -> assertThat(showResponse.body().jsonPath().getLong("stations[1].id[1]")).isEqualTo(3L),
      () -> assertThat(showResponse.body().jsonPath().getLong("stations[2].id[0]")).isEqualTo(3L),
      () -> assertThat(showResponse.body().jsonPath().getLong("stations[2].id[1]")).isEqualTo(4L)
    );
    assertThat(showResponse.body().jsonPath().getList("name", String.class)).size().isEqualTo(3);

    assertAll(
      "지하철 노선 목록 조회 이름 값 확인",
      () -> assertThat(showResponse.body().jsonPath().getString("stations[0].name[0]")).isEqualTo("종합운동장"),
      () -> assertThat(showResponse.body().jsonPath().getString("stations[0].name[1]")).isEqualTo("잠실새내"),
      () -> assertThat(showResponse.body().jsonPath().getString("stations[1].name[0]")).isEqualTo("잠실새내"),
      () -> assertThat(showResponse.body().jsonPath().getString("stations[1].name[1]")).isEqualTo("잠실"),
      () -> assertThat(showResponse.body().jsonPath().getString("stations[2].name[0]")).isEqualTo("잠실"),
      () -> assertThat(showResponse.body().jsonPath().getString("stations[2].name[1]")).isEqualTo("선릉")
    );
  }

  @DisplayName("지하철 특정 노선의 정보를 조회한다.")
  @Test
  public void testShowLineInfo() {

    //given
    지하철_역을_생성("종합운동장");
    지하철_역을_생성("잠실새내");
    지하철_노선을_생성("2호선", "green", 1L, 2L, 10);

    //when
    ExtractableResponse lineOneInfo = RestAssured.given().log().all()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().get("/lines/1")
      .then().log().all()
      .extract();

    //then
    assertThat(lineOneInfo.statusCode()).isEqualTo(HttpStatus.OK.value());
    assertAll(
      "지하철 노선 정보 확인",
      () -> assertThat(lineOneInfo.body().jsonPath().getString("name")).isEqualTo("2호선"),
      () -> assertThat(lineOneInfo.body().jsonPath().getString("color")).isEqualTo("green"),
      () -> assertThat(lineOneInfo.body().jsonPath().getList("stations")).size().isEqualTo(2),
      () -> assertThat(lineOneInfo.body().jsonPath().getLong("stations[0].id")).isEqualTo(1L),
      () -> assertThat(lineOneInfo.body().jsonPath().getLong("stations[1].id")).isEqualTo(2L),
      () -> assertThat(lineOneInfo.body().jsonPath().getString("stations[0].name")).isEqualTo("종합운동장"),
      () -> assertThat(lineOneInfo.body().jsonPath().getString("stations[1].name")).isEqualTo("잠실새내")
    );
  }

  @DisplayName("지하철 노선을 수정한다.")
  @Test
  public void testModifyLine() {
    //given
    ExtractableResponse<Response> createStation1Response = 지하철_역을_생성("종합운동장");
    ExtractableResponse<Response> createStation2Response = 지하철_역을_생성("잠실새내");
    ExtractableResponse<Response> createLineResponse =
      지하철_노선을_생성("2호선", "green", 1L, 2L, 10);

    //when
    ExtractableResponse lineModifyResponse = RestAssured.given().log().all()
      .body(new LineModifyRequest("새로운 2호선", "blue"))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().put("/lines/1")
      .then().log().all()
      .extract();

    //then
    assertThat(lineModifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

    assertAll(
      "지하철 및 노선 생성",
      () -> assertThat(createStation1Response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
      () -> assertThat(createStation2Response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
      () -> assertThat(createLineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value())
    );

    assertAll(
      "지하철 노선 수정",
      () -> assertThat(lineModifyResponse.body().jsonPath().getString("name")).isEqualTo("새로운 2호선"),
      () -> assertThat(lineModifyResponse.body().jsonPath().getString("color")).isEqualTo("blue")
    );
  }

  @DisplayName("지하철 노선을 삭제한다.")
  @Test
  public void testDeleteLine() {
      //given
    ExtractableResponse<Response> createStation1Response = 지하철_역을_생성("종합운동장");
    ExtractableResponse<Response> createStation2Response = 지하철_역을_생성("잠실새내");
    ExtractableResponse<Response> createLineResponse =
      지하철_노선을_생성("2호선", "green", 1L, 2L, 10);


    //when
    ExtractableResponse lineDeleteResponse = RestAssured.given().log().all()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().delete("/lines/1")
      .then().log().all()
      .extract();

    //then
    assertAll(
      "지하철 및 노선 생성",
      () -> assertThat(createStation1Response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
      () -> assertThat(createStation2Response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
      () -> assertThat(createLineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value())
    );

    assertThat(lineDeleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }

  private ExtractableResponse<Response> 지하철_역을_생성(String name) {
    return RestAssured.given().log().all()
      .body(new StationRequest(name))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/stations")
      .then().log().all()
      .extract();
  }

  private ExtractableResponse<Response> 지하철_노선을_생성(String name, String color, Long upStationId, Long downStationId, int distance) {
    return RestAssured.given().log().all()
      .body(new LineCreateRequest(name, color, upStationId, downStationId, distance))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines")
      .then().log().all()
      .extract();
  }

  private ExtractableResponse<Response> 지하철_노선_구간을_등록(Long lineId, Long upStationId, Long downStationId, int distance) {
    return RestAssured.given().log().all()
      .body(new LineSectionAppendRequest(upStationId, downStationId, distance))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines/%d/sections".formatted(lineId))
      .then().log().all()
      .extract();
  }

  /**
   Given 지하철 노선을 등록 한다.
   When 지하철 노선 구간을 등록 한다.
   Then 새로운 요청의 상행 역은 마지막 구간의 하행 역 이어야 한다. -> 조건 불 충분시 오류가 발생 해야 한다.
        새로운 요청의 하행 역은 이미 해당 구간 역에 등록 되어 있는 역이 아니 어야 한다. -> 불 충분시 오류가 발생 해야 한다.
        등록된 지하철 노선 구간 결과에 요청이 들어가 있는지 확인 한다.
   */
  @DisplayName("지하철 노선 구간을 등록한다.")
  @Test
  public void testAppendSubwayLineSection(){
    //given
    지하철_역을_생성("종합운동장");
    지하철_역을_생성("잠실새내");
    지하철_노선을_생성("2호선", "green", 1L, 2L, 10);

    //when
    ExtractableResponse lineSectionAppendResponse = RestAssured.given().log().all()
      .pathParam("id", 1L)
      .body(new LineSectionAppendRequest(1L, 2L, 10))
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines/{id}/sections")
      .then().log().all()
      .extract();

    //then
    assertThat(lineSectionAppendResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
  }

  /**
   Given 지하철 노선 및 지하철 노선 구간을 등록 한다.
   When 지하철 노선 구간을 삭제 한다.
   Then 마지막 하행 종점 역이 아닌 경우 삭제할 수 없다. -> 조건 불 충분시 오류가 발생 해야 한다.
        노선 구간이 1개인 경우 삭제할 수 없다. -> 조건 불 충분시 오류가 발생 해야 한다.
        삭제된 지하철 노선 구간 결과 에서 요청 결과가 삭제 되 었는지 확인 한다.
   */
  @DisplayName("지하철 노선 구간을 삭제한다.")
  @Test
  public void testRemoveSubwayLineSection(){
    //given
    지하철_역을_생성("종합운동장");
    지하철_역을_생성("잠실새내");
    지하철_역을_생성("잠실");
    지하철_노선을_생성("2호선", "green", 1L, 2L, 10);
    지하철_노선_구간을_등록(1L, 1L, 2L, 10);
    지하철_노선_구간을_등록(1L, 2L, 3L, 10);

    //when
    ExtractableResponse falseLineSectionRemoveResponse = RestAssured.given().log().all()
      .pathParam("stationId", 2L)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().delete("/lines/1/sections")
      .then().log().all()
      .extract();

    ExtractableResponse trueLineSectionRemoveResponse = RestAssured.given().log().all()
      .pathParam("stationId", 3L)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().delete("/lines/1/sections")
      .then().log().all()
      .extract();

    //then
    assertThat(falseLineSectionRemoveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(trueLineSectionRemoveResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }
}
