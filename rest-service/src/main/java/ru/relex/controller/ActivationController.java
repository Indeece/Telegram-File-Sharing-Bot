package ru.relex.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.relex.entity.AppUser;
import ru.relex.service.UserActivationService;

import static ru.relex.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;

@RestController
@RequiredArgsConstructor
public class ActivationController {

    private final UserActivationService userActivationService;

    @RequestMapping("/user/activation")
    public ResponseEntity<String> activation(@RequestParam("id") String id) {
        boolean activated = userActivationService.activation(id);
        if (activated) {
            return ResponseEntity.ok("Регистрация успешно завершена!");
        }
        return ResponseEntity.badRequest().body("Ошибка при активации пользователя.");
    }
}