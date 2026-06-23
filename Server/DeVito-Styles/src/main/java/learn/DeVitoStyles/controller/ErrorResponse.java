package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponse {

    private ErrorResponse() {

    }

    public static ResponseEntity<Object> build(Result<?> result) {
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpStatus status = switch (result.getResultType()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;

            case
        }
    }
}
