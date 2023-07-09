package subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.ApiTest;
import subway.exception.ErrorCode;
import subway.section.dto.CreateSectionRequest;
import subway.station.StationSteps;
import subway.subwayline.dto.CreateSubwayLineRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.subwayline.SubwayLineSteps.지하철노선등록요청;
import static subway.subwayline.SubwayLineSteps.지하철노선등록요청_생성;

class SectionAcceptanceTest extends ApiTest {

    private Long 당고개역, 이수역, 사당역, 당고개역부터_이수역까지의_기존_노선_ID;
    private static final int distance = 10;

    @BeforeEach
    void before() {
        // given : 선행조건 기술
        당고개역부터_이수역까지의_기존_노선_ID = 당고개역부터_이수역까지의_기존_노선_생성();
    }

    /**
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성한다.
     * Given 기존 구간 노선은 (당고개역 - 상행 종점, 이수역- 하행 종점)으로 구성한다.
     * When 새로운 구간(이수역 - 상행 종점, 사당역 - 하행 종점)을 등록한다.
     * Then 정상적으로 등록이 되었으면 HttpStatus.OK를 반환한다.
     */
    @DisplayName("새로운 구간을 등록한다.")
    @Test
    void addSection() {
        // given : 선행조건 기술
        CreateSectionRequest request = request(이수역, 사당역, distance);

        // when : 기능 수행
        ExtractableResponse<Response> response = 새로운_노선_구간_등록(request);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성한다.
     * Given 기존 구간 노선은 (당고개역 - 상행 종점, 이수역- 하행 종점)으로 구성한다.
     * When 새로운 구간(당고개 - 상행 종점, 사당역 - 하행 종점)을 등록한다.
     * Then 새로운 구간의 상행선이 기존 노선의 하행 종점역이 아니면 예외가 발생한다.
     */
    @DisplayName("새로운 구간을 등록할 시 새로운 구간의 상행선이 기존 노선의 하행 종점역이 아니면 예외가 발생한다.")
    @Test
    void addSectionThrowExceptionIsINVALID_UP_STATION() {
        // given : 선행조건 기술
        CreateSectionRequest request = request(당고개역, 사당역, distance);

        // when : 기능 수행
        ExtractableResponse<Response> response = 새로운_노선_구간_등록(request);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_UP_STATION.getMessage());
    }

    /**
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성한다.
     * Given 기존 구간 노선은 (당고개역 - 상행 종점, 이수역- 하행 종점)으로 구성한다.
     * When 새로운 구간(이수역 - 상행 종점, 당고개 - 하행 종점)을 등록한다.
     * Then 새로운 구간의 상행선이 기존 노선의 하행 종점역이 아니면 예외가 발생한다.
     */
    @DisplayName("새로운 구간을 등록할 시 새로운 구간의 하행역이 기존 노선에 등록되어 있으면 예외가 발생한다.")
    @Test
    void addSectionThrowExceptionIsINVALID_DOWN_STATION() {
        // given : 선행조건 기술
        CreateSectionRequest request = request(이수역, 당고개역, distance);

        // when : 기능 수행
        ExtractableResponse<Response> response = 새로운_노선_구간_등록(request);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_DOWN_STATION.getMessage());
    }

    /**
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성하고 구간을 두개 등록한다.
     * Given 기존 구간 노선은 (1.당고개역 - 상행 종점, 이수역- 하행 종점, 2. 이수역 - 상행 종점, 사당역 - 하행 종점)으로 구성한다.
     * When 기존 구간(2. 이수역 - 상행 종점, 사당역 - 하행 종점)에서 사당역만 제거한다.
     * Then 구간을 제거하고 정상적인 요청이면 HttpStatus.OK를 반환한다.
     */
    @DisplayName("구간을 제거한다.")
    @Test
    void removeSection() {
        // given : 선행조건 기술
        이수역부터_사당역까지의_신규노선_생성(당고개역부터_이수역까지의_기존_노선_ID);

        // when : 기능 수행
        ExtractableResponse<Response> response = 지하철_노선_구간_제거(당고개역부터_이수역까지의_기존_노선_ID, 사당역);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성하고 구간을 두개 등록한다.
     * Given 기존 구간 노선은 (1.당고개역 - 상행 종점, 이수역- 하행 종점, 2. 이수역 - 상행 종점, 사당역 - 하행 종점)으로 구성한다.
     * When 기존 구간(2. 이수역 - 상행 종점, 사당역 - 하행 종점)에서 이수역을 제거한다.
     * Then 하행종점역이 아니기 때문에 예외를 반환한다.
     */
    @DisplayName("구간을 제거할때 제거하려는 구간의 역이 하행 종점이 아니면 예외가 발생한다.")
    @Test
    void removeSectionThrowsExceptionIsNOT_DOWN_STATION() {
        // given : 선행조건 기술
        이수역부터_사당역까지의_신규노선_생성(당고개역부터_이수역까지의_기존_노선_ID);

        // when : 기능 수행
        ExtractableResponse<Response> response = 지하철_노선_구간_제거(당고개역부터_이수역까지의_기존_노선_ID, 이수역);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo(ErrorCode.NOT_DOWN_STATION.getMessage());
    }

    /**
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성하고 구간을 두개 등록한다.
     * Given 기존 구간 노선은 (1.당고개역 - 상행 종점, 이수역- 하행 종점)으로 구성한다.
     * When 기존 구간(1.당고개역 - 상행 종점, 이수역- 하행 종점)에서 이수역을 제거한다.
     * Then 구간이 하나만 있기 때문에 예외를 반환한다.
     */
    @DisplayName("구간을 제거할때 제거하려는 노선이 하나만 있으면 예외를 반환한다.")
    @Test
    void removeSectionThrowsExceptionIsSECTION_IS_ONE() {
        // when : 기능 수행
        ExtractableResponse<Response> response = 지하철_노선_구간_제거(당고개역부터_이수역까지의_기존_노선_ID, 이수역);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo(ErrorCode.SECTION_IS_ONE.getMessage());
    }

    private Long 당고개역부터_이수역까지의_기존_노선_생성() {
        String stationName1 = "당고개역";
        String stationName2 = "이수역";
        String stationName3 = "사당역";
        String name = "4호선";
        String color = "bg-blue-500";
        당고개역 = 역_생성(stationName1);
        이수역 = 역_생성(stationName2);
        사당역 = 역_생성(stationName3);
        CreateSubwayLineRequest 당고개역부터_이수역까지의_기존_노선 = 지하철노선등록요청_생성(name, color, 당고개역, 이수역, distance);
        return 지하철노선등록요청(당고개역부터_이수역까지의_기존_노선).jsonPath().getLong("id");
    }

    private ExtractableResponse<Response> 새로운_노선_구간_등록(CreateSectionRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", 1L)
                .then().log().all()
                .extract();
    }

    private void 이수역부터_사당역까지의_신규노선_생성(Long 당고개역부터_이수역까지의_기존_노선_ID) {
        ExtractableResponse<Response> 이수역부터_사당역까지의_신규구간_생성 = RestAssured.given().log().all()
                .body(request(이수역, 사당역, 10))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", 당고개역부터_이수역까지의_기존_노선_ID)
                .then().log().all()
                .extract();

        assertThat(당고개역부터_이수역까지의_기존_노선_ID).isEqualTo(1L);
        assertThat(이수역부터_사당역까지의_신규구간_생성.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static ExtractableResponse<Response> 지하철_노선_구간_제거(Long 당고개역부터_이수역까지의_기존_노선_ID, Long 사당역) {
        return RestAssured.given().log().all()
                .param("stationId", 사당역)
                .when().delete("/lines/{lineId}/sections", 당고개역부터_이수역까지의_기존_노선_ID)
                .then().log().all()
                .extract();
    }

    private CreateSectionRequest request(Long newUpStationId, Long newDownStationId, int distance) {
        return new CreateSectionRequest(newUpStationId, newDownStationId, distance);
    }

    private static Long 역_생성(String stationName1) {
        return StationSteps.지하철생성요청(StationSteps.지하철생성요청_생성(stationName1)).jsonPath().getLong("id");
    }
}