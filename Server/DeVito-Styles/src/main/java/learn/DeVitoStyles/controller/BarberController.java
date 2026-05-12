package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.BarberService;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.models.Barber;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/barber")
@CrossOrigin
public class BarberController {
    private final BarberService service;

    public BarberController(BarberService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Object> findAll() throws DataAccessException {
        Result<List<Barber>> result = service.findAll();
        return ResponseEntity.ok(result.getpayload());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable int id) throws DataAccessException {
        Result<Barber> result = service.findById(id);

        if (!result.isSuccess()) {
            if (result.getResultType() == ResultType.NOT_FOUND) {
                return new ResponseEntity<>(result.getMessages(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(result.getpayload());
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Barber barber) throws DataAccessException {
        Result<Barber> result = service.create(barber);

        
    }
}

//Method	Endpoint	        Purpose
//GET	    /api/barber	        Get all barbers
//GET	    /api/barber/{id}	Get barber by ID
//POST	    /api/barber	        Create barber (admin)
//PUT	    /api/barber/{id}	Update barber (admin)
//DELETE	/api/barber/{id}	Delete barber (admin)
