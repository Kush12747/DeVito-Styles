package learn.DeVitoStyles.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.domain.UserService;
import learn.DeVitoStyles.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user) throws DataAccessException, JsonProcessingException {
        Result<User> result = service.authenticate(user);

        if (result.getResultType() == ResultType.NOT_FOUND) {
            return new ResponseEntity<>(result.getMessages(), HttpStatus.NOT_FOUND);
        } else if (result.getResultType() == ResultType.INVALID) {
            return new ResponseEntity<>(result.getMessages(), HttpStatus.UNAUTHORIZED);
        }

        // convert user to json
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(result.getpayload());

        // add secret string
        String userJsonWithSecret =
                userJson + "backend-secret-string";

        // hash it
        int hashTotal = Objects.hash(userJsonWithSecret);

        // final output
        String outputString = userJson + "|" + hashTotal;

        Map<String, String> outputMap =
                Map.of("user", outputString);

        return new ResponseEntity<>(outputMap, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> create(@RequestBody User user) throws DataAccessException {
        Result<User> result = service.create(user);

        if (!result.isSuccess()) {
            return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result.getpayload(), HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable int id) throws DataAccessException {
        Result<User> result = service.findById(id);

        if (!result.isSuccess()) {
            if (result.getResultType() == ResultType.NOT_FOUND) {
                return new ResponseEntity<>(result.getMessages(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(result.getpayload());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id, @RequestBody User user) throws DataAccessException {

        if (id != user.getUserId()) {
            return new ResponseEntity<>(List.of("Path ID and User ID must match."), HttpStatus.CONFLICT);
        }

        Result<User> result = service.update(user);

        if (!result.isSuccess()) {
            if (result.getResultType() == ResultType.NOT_FOUND) {
                return new ResponseEntity<>(result.getMessages(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result.getpayload(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        Result<List<User>> result = service.findAll();
        return ResponseEntity.ok(result.getpayload());
    }
}

//Endpoint	Method
//POST /api/user/login
//POST /api/user/register
//GET /api/user/{id}
//PUT /api/user/{id}
//GET /api/user