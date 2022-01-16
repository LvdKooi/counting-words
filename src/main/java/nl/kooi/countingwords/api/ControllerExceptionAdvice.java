package nl.kooi.countingwords.api;

import lombok.extern.slf4j.Slf4j;
import nl.kooi.countingwords.api.dto.ErrorResponseDto;
import nl.kooi.countingwords.exception.WordProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler({WordProcessingException.class})
    public ResponseEntity<ErrorResponseDto> handleWordProcessingException(WordProcessingException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDto().reason(exception.getMessage()).reference(UUID.randomUUID()));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponseDto().reason(exception.getMessage()).reference(UUID.randomUUID()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage());

        var message = "";

        if (exception.hasErrors()) {
            message = exception.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        }
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDto().reason("The following fields were invalid: " + message).reference(UUID.randomUUID()));
    }
}