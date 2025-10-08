package net.andrecarbajal.sysped.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.service.CaptchaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {
    private final CaptchaService captchaService;

    @GetMapping("/image")
    public void generateCaptchaImage(HttpSession session, HttpServletResponse response)
            throws IOException {
        String captchaCode = (String) session.getAttribute("captchaCode");
        if (captchaCode == null) {
            captchaCode = captchaService.generateCaptchaCode();
            session.setAttribute("captchaCode", captchaCode);
        }
        BufferedImage captchaImage = captchaService.generateCaptchaImage(captchaCode);
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        OutputStream out = response.getOutputStream();
        ImageIO.write(captchaImage, "png", out);
        out.flush();
        out.close();
    }

    @PostMapping("/refresh")
    @ResponseBody
    public void refreshCaptcha(HttpSession session) {
        String newCaptchaCode = captchaService.generateCaptchaCode();
        session.setAttribute("captchaCode", newCaptchaCode);
    }
}

