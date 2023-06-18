package account.controllers;

import account.dto.request.RequestChangeUserAccessDto;
import account.dto.request.RequestRoleDto;
import account.service.interfaces.IAdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/admin/")
public class AdminController {
    IAdminService adminService;

    @Autowired
    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("user/role")
    ResponseEntity<?> setUserRole(@RequestBody RequestRoleDto requestRoleDto, Principal principal) {
        return adminService.setUserRole(requestRoleDto, principal);
    }

    @DeleteMapping({"user/{email}"})
    ResponseEntity<?> deleteUser(@PathVariable String email, Principal principal) {
        return adminService.deleteUser(email, principal);
    }

    @GetMapping("user/")
    ResponseEntity<?> getAllUsersInfo(Principal principal) {
        return adminService.getAllUsersInfo(principal);
    }

    @PutMapping("user/access")
    ResponseEntity<?> changeUserAccess(@RequestBody RequestChangeUserAccessDto requestChangeUserAccessDto, Principal principal) {
        return adminService.changeUserAccess(requestChangeUserAccessDto, principal.getName());
    }
}
