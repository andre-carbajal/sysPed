package net.andrecarbajal.sysped.service;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;

@Service
public class CaptchaService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CAPTCHA_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public String generateCaptchaCode() {
        StringBuilder sb = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public BufferedImage generateCaptchaImage(String code) {
        int width = 160;
        int height = 50;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 10; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.setFont(new Font("Consolas", Font.BOLD, 34));
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(code)) / 2;
        int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(code, x, y);

        g2d.dispose();
        return image;
    }
}
