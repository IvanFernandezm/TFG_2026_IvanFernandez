package org.example.tribunalsbackend.Api;

import org.example.tribunalsbackend.Controller.UserController;
import org.example.tribunalsbackend.Domain.Abstracts.AppUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserController userService;

    public UserRestController(UserController userService) {
        this.userService = userService;
    }

    @GetMapping("/signIn")
    public String signIn(@RequestParam String mail) throws Exception {
        return userService.signIn(mail);
    }

    @PostMapping("/register")
    public void register(@RequestBody AppUser appUser, String userType) {
        userService.register(appUser,userType);

    }
}
