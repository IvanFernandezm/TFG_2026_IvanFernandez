package org.example.tribunalsbackend.Api;

import org.example.tribunalsbackend.Controller.UserController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    //REST CONTROLLER PER A LA GESTIÓ GENERAL D'INICI DE SESSIÓ

    private final UserController userService;

    public UserRestController(UserController userService) {
        this.userService = userService;
    }

    @GetMapping("/signIn")
    public ResponseEntity<String> signIn(@RequestParam String mail) throws Exception {
        String userType = userService.signIn(mail);
        return ResponseEntity.ok(userType);
    }

    @PostMapping("/register")
    public void register(@RequestBody Map<String, String> body, @RequestParam String userType) {
        userService.register(body.get("mail"), body.get("name"), userType);

    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam String mail, @RequestParam String userType) {
        userService.deleteUser(mail, userType);
    }

}
