package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResponseExceptionHandler {

	@ExceptionHandler(DuplicationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ExceptionResponse duplication(DuplicationException exception) {

		return new ExceptionResponse(HttpStatus.CONFLICT.value(), exception.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ExceptionResponse notFound(NotFoundException exception) {

		return new ExceptionResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
	}
}