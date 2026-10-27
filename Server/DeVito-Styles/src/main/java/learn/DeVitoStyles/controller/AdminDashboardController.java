package learn.DeVitoStyles.controller;

import learn.DeVitoStyles.domain.AdminDashboardService;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.dto.AdminDashboardResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Admin only: SecurityConfig restricts /api/admin/** to the ADMIN role
@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin
public class AdminDashboardController {
    private final AdminDashboardService service;

    public AdminDashboardController(AdminDashboardService service) {
        this.service = service;
    }

    // GET /api/admin/dashboard?range=day|week|month (defaults to week)
    @GetMapping
    public ResponseEntity<Object> getDashboard(@RequestParam(required = false) String range) throws DataAccessException {
        Result<AdminDashboardResponse> result = service.getDashboard(range);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        return ResponseEntity.ok(result.getpayload());
    }
}
