package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.AppointmentService;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.models.Appointment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointment")
@CrossOrigin
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping("/availability")
    public ResponseEntity<Object> getAvailability(@RequestParam int barberId, @RequestParam LocalDate date) {

        Result<List<Appointment>> result =
                service.getByBarberAndDate(barberId, date);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return ResponseEntity.ok(result.getpayload());
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> findAll() {
        Result<List<Appointment>> result = service.getAll();
        return ResponseEntity.ok(result.getpayload());
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<Object> findById(@PathVariable int appointmentId) {
        Result<Appointment> result = service.getById(appointmentId);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        return ResponseEntity.ok(result.getpayload());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> findByUserId(@PathVariable int userId) {
        Result<List<Appointment>> result = service.getByUserId(userId);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        return ResponseEntity.ok(result.getpayload());
    }

    @GetMapping("/barber/{barberId}")
    public ResponseEntity<Object> findByBarberAndDate(@PathVariable int barberId, @RequestParam LocalDate date) {
        Result<List<Appointment>> result = service.getByBarberAndDate(barberId, date);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        return ResponseEntity.ok(result.getpayload());
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody Appointment appointment) throws Exception {
        Result<Appointment> result = service.add(appointment);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getpayload(), HttpStatus.CREATED);
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<Object> update(@PathVariable int appointmentId, @RequestBody Appointment appointment) {

        if (appointmentId != appointment.getAppointmentId()) {
            return new ResponseEntity<>(List.of("Path ID and appointment id don't match"), HttpStatus.CONFLICT);
        }

        Result<Appointment> result = service.update(appointment);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/cancel/{appointmentId}")
    public ResponseEntity<Object> cancel(@PathVariable int appointmentId) throws IOException {
        Result<Void> result = service.cancel(appointmentId);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}