package net.andrecarbajal.sysped.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.service.CaptchaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final CaptchaService captchaService;

    @GetMapping
    public String login(HttpSession session, @RequestParam(value = "error", required = false) String error, Model model) {
        String captchaCode = captchaService.generateCaptchaCode();
        session.setAttribute("captchaCode", captchaCode);
        if (error != null) {
            if ("captcha".equals(error)) {
                model.addAttribute("error", "Captcha incorrecto");
            } else {
                model.addAttribute("error", "Usuario o contraseña incorrectos");
            }
        }
        return "login";
    }
}