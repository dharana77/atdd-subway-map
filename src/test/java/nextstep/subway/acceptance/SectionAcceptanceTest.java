package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.common.CommonAcceptanceTest;
import nextstep.subway.exception.SubwayExceptionMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends CommonAcceptanceTest {

	private Long 신분당선;

	private Long 논현역;
	private Long 신논현역;
	private Long 강남역;
	private Long 양재역;

	@BeforeEach
	public void setUp() {
		논현역 = 지하철_역_생성("논현역").jsonPath().getLong("id");
		신논현역 = 지하철_역_생성("신논현역").jsonPath().getLong("id");
		강남역 = 지하철_역_생성("강남역").jsonPath().getLong("id");
		양재역 = 지하철_역_생성("양재역").jsonPath().getLong("id");

		신분당선 = 지하철_노선_생성("신분당선", "bg-red-600", 논현역, 신논현역, 10).jsonPath().getLong("id");
	}

	/**
	 * given 역과 노선을 등록하고
	 * when 해당 노선에 구간을 등록한 후 노선을 조회하면
	 * then 노선 안에 등록한 구간이 포함된 것을 확인할 수 있다.
	 */
	@DisplayName("지하철 구간 등록 - 성공")
	@Test
	void addSectionSuccess(){
		// when
		지하철_구간_등록(신분당선, 신논현역, 강남역, 10);
		List<String> stations = 지하철_노선_조회(신분당선).jsonPath().getList("stations.name");
		// then
		assertThat(stations).containsExactly("논현역", "신논현역", "강남역");
	}

	/**
	 * given 역과 노선을 등록하고
	 * when 하행 종점역과 다른 새로운 구간의 상행역을 등록하면
	 * then msg = "구간을 추가할 수 없습니다." 를 포함한 400(BAD_REQUEST) 를 반환한다.
	 */
	@DisplayName("지하철 구간 등록 - 실패(종점과 다른 상행역)")
	@Test
	void addSectionFailNotEqualUpAndDown(){
		// when
		ExtractableResponse<Response> response = 지하철_구간_등록(신분당선, 강남역, 양재역, 8);
		String message = response.jsonPath().get("msg");

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(message).isEqualTo(SubwayExceptionMessage.CANNOT_ADD_SECTION.msg());
	}

	/**
	 * given 역과 노선을 등록하고
	 * when 노선에 이미 포함된 역이 하행역인 구간을 등록하면
	 * then msg = "역이 이미 노선에 포함되어 있습니다."를 포함한 400(BAD_REQUEST) 를 반환한다.
	 */
	@DisplayName("지하철 구간 등록 - 실패(이미 노선에 포함된 역)")
	@Test
	void addSectionFailAlreadyStationInLine(){
		// when
		ExtractableResponse<Response> response = 지하철_구간_등록(신분당선, 신논현역, 논현역, 8);
		String message = response.jsonPath().get("msg");
		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(message).isEqualTo(SubwayExceptionMessage.ALREADY_EXIST_STATION.msg());
	}

	/**
	 * given 역과 노선을 등록하고 구간을 등록한 후
	 * when 해당 구간을 삭제한 다음 노선을 조회하면
	 * then 노선 안에 삭제한 구간이 없는 것을 확인할 수 있다.
	 */
	@DisplayName("지하철 구간 삭제 - 성공")
	@Test
	void deleteSectionSuccess(){
		//given
		지하철_구간_등록(신분당선, 신논현역, 강남역, 10);
		List<String> beforeDelete = 지하철_노선_조회(신분당선).jsonPath().getList("stations.name");
		// when
		지하철_구간_삭제(신분당선,강남역);
		List<String> stations = 지하철_노선_조회(신분당선).jsonPath().getList("stations.name");
		// then
		assertThat(beforeDelete).containsExactly("논현역", "신논현역", "강남역");
		assertThat(stations).containsExactly("논현역", "신논현역");
	}

	/**
	 * given 역과 노선을 등록하고 구간을 등록한 후
	 * when 마지막이 아닌 역을 삭제 시도하면
	 * then msg = "구간을 삭제할 수 없습니다."를 포함한 400(BAD_REQUEST) 를 반환한다.
	 */
	@DisplayName("지하철 구간 삭제 - 실패(마지막이 아닌 역 삭제)")
	@Test
	void deleteSectionFailWhenDeleteLastStation(){
		//given
		지하철_구간_등록(신분당선, 신논현역, 강남역, 10);
		List<String> beforeDelete = 지하철_노선_조회(신분당선).jsonPath().getList("stations.name");
		// when
		ExtractableResponse<Response> response = 지하철_구간_삭제(신분당선, 신논현역);
		String message = response.jsonPath().get("msg");
		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(message).isEqualTo(SubwayExceptionMessage.CANNOT_DELETE_SECTION.msg());
	}

	/**
	 * given 역과 노선을 등록하고 구간을 등록한 후
	 * when 구간이 1개인 경우 삭제를 시도하면
	 * then BAD_REQUEST 를 반환한다.
	 */
	@DisplayName("지하철 구간 삭제 - 실패(구간이 1개인 경우 삭제)")
	@Test
	void deleteSectionFailWhenSectionIsOnlyOne(){
		// when
		ExtractableResponse<Response> response = 지하철_구간_삭제(신분당선, 신논현역);
		String message = response.jsonPath().get("msg");
		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(message).isEqualTo(SubwayExceptionMessage.ONLY_ONE_SECTION.msg());
	}

	private ExtractableResponse<Response> 지하철_구간_삭제(Long lineId, Long stationId) {
		Map<String, Object> params = new HashMap<>();
		params.put("stationId", stationId);

		return RestAssured
				.given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().delete("/lines/{lineId}/sections", lineId)
				.then().log().all()
				.extract();
	}

	private ExtractableResponse<Response> 지하철_구간_등록(Long lineId, Long upStationId, Long downStationId, Integer distance) {
		Map<String, Object> params = new HashMap<>();
		params.put("upStationId", upStationId);
		params.put("downStationId", downStationId);
		params.put("distance", distance);

		return RestAssured
				.given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().post("/lines/{lineId}/sections", lineId)
				.then().log().all()
				.extract();
	}
}