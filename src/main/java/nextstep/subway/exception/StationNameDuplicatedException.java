package nextstep.subway.exception;

public class StationNameDuplicatedException extends NameDuplicatedException {
    public StationNameDuplicatedException(String name) {
        super(name);
    }
}