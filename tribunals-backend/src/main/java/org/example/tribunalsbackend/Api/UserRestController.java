package org.example.tribunalsbackend.Api;

import org.example.tribunalsbackend.Controller.UserController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public void register(@RequestBody Map<String, String> body, @RequestParam String userType) {
        userService.register(body.get("mail"), body.get("name"), userType);

    }
}
