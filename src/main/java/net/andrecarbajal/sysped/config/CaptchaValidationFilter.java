package net.andrecarbajal.sysped.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.service.CaptchaService;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class CaptchaValidationFilter extends OncePerRequestFilter {
    private static final String CAPTCHA_SESSION_KEY = "captchaCode";

    private final CaptchaService captchaService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if ("/login".equals(request.getServletPath()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String captchaInput = request.getParameter("captcha");
            HttpSession session = request.getSession(false);
            String captcha = session != null ? (String) session.getAttribute(CAPTCHA_SESSION_KEY) : null;
            if (captcha == null || !captcha.equals(captchaInput)) {
                if (session != null) {
                    String newCaptcha = captchaService.generateCaptchaCode();
                    session.setAttribute(CAPTCHA_SESSION_KEY, newCaptcha);
                }
                response.sendRedirect("/login?error=captcha");
                return;
            }
            session.removeAttribute(CAPTCHA_SESSION_KEY);
        }
        filterChain.doFilter(request, response);
    }
}
