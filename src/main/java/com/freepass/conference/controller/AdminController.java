package com.freepass.conference.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freepass.conference.model.User;
import com.freepass.conference.service.AdminService;
import com.freepass.conference.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    @PatchMapping("/coordinator/add/{id}")
    public ResponseEntity<User> addCoordinator(@PathVariable Integer id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok().body(adminService.addCoordinator(user));
    }

    @PatchMapping("/coordinator/remove/{id}")
    public ResponseEntity<User> removeCoordinator(@PathVariable Integer id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok().body(adminService.removeCoordinator(user));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<User> removeUser(@PathVariable Integer id) throws Exception {
        User user = userService.findUserById(id);
        return ResponseEntity.ok().body(adminService.removeUser(user));
    }
}
