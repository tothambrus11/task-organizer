package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/auth")
    public ResponseEntity<Boolean> auth(@RequestBody String password) {
        return new ResponseEntity<>(adminService.auth(password), HttpStatus.OK);
    }

    @GetMapping("/reset")
    public ResponseEntity reset() {
        adminService.reset();
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
