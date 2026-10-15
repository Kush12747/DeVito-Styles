package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @ParameterizedTest
    @CsvSource({
            "NOT_FOUND, NOT_FOUND",
            "CONFLICT, CONFLICT",
            "UNAUTHORIZED, UNAUTHORIZED",
            "FORBIDDEN, FORBIDDEN",
            "INVALID, BAD_REQUEST"
    })
    void build_mapsResultTypeToHttpStatus_andBodyIsMessages(ResultType type, HttpStatus expected) {
        Result<Object> result = new Result<>();
        result.addErrorMessage("first", type);
        result.addErrorMessage("second", type);

        ResponseEntity<Object> response = ErrorResponse.build(result);

        assertThat(response.getStatusCode()).isEqualTo(expected);
        assertThat(response.getBody()).isEqualTo(List.of("first", "second"));
    }

    @Test
    void build_whenResultNull_returns500WithNoBody() {
        ResponseEntity<Object> response = ErrorResponse.build(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
    }
}
