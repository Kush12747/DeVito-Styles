package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service")
@CrossOrigin
public class ServiceController {
    private final ShopService service;

    public ServiceController(ShopService service) {
        this.service = service;
    }


}
