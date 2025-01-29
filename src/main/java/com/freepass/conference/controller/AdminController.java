package com.freepass.conference.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freepass.conference.dto.DefaultResponse;
import com.freepass.conference.model.User;
import com.freepass.conference.service.AdminService;
import com.freepass.conference.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    @PatchMapping("/coordinator/add/{id}")
    public ResponseEntity<DefaultResponse<User>> addCoordinator(@PathVariable Integer id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok().body(DefaultResponse.success(adminService.addCoordinator(user)));
    }

    @PatchMapping("/coordinator/remove/{id}")
    public ResponseEntity<DefaultResponse<User>> removeCoordinator(@PathVariable Integer id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok().body(DefaultResponse.success(adminService.removeCoordinator(user)));
    }

    @GetMapping("/user/all")
    public ResponseEntity<DefaultResponse<Iterable<User>>> viewAllUser() {
        return ResponseEntity.ok().body(DefaultResponse.success(userService.findAllUser()));
    }
    

    @DeleteMapping("/user/{id}")
    public ResponseEntity<DefaultResponse<User>> removeUser(@PathVariable Integer id) throws Exception {
        User user = userService.findUserById(id);
        return ResponseEntity.ok().body(DefaultResponse.success(adminService.removeUser(user)));
    }
}
