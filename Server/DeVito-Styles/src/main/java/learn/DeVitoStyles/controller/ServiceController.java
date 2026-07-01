package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.domain.ShopService;
import learn.DeVitoStyles.models.Service;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service")
@CrossOrigin
public class ServiceController {
    private final ShopService service;

    public ServiceController(ShopService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Object> findAll() throws DataAccessException {
        Result<List<Service>> result = service.findAll();
        return ResponseEntity.ok(result.getpayload());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable int id) throws DataAccessException {
        Result<Service> result = service.findById(id);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        return ResponseEntity.ok(result.getpayload());
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Service serviceObj) throws DataAccessException {
        Result<Service> result = service.create(serviceObj);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getpayload(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id, @RequestBody Service serviceObj) throws DataAccessException {

        if (id != serviceObj.getServiceId()) {
            return new ResponseEntity<>(List.of("Path ID and body ID must match"), HttpStatus.CONFLICT);
        }

        Result<Service> result = service.update(serviceObj);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getpayload(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id) throws DataAccessException {
        Result<Void> result = service.deleteById(id);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}