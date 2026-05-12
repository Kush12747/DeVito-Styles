package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.BarberService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@CrossOrigin
public class BarberController {
    private final BarberService service;

    public BarberController(BarberService service) {
        this.service = service;
    }
}
