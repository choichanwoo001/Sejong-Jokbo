package com.sejong.controller.view;

import com.sejong.entity.Admin;
import com.sejong.repository.AdminRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminLoginController {

    private final AdminRepository adminRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String password, HttpSession session, Model model) {
        try {
            // SHA-256 해싱
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            String hashedPassword = Base64.getEncoder().encodeToString(hash);

            // DB에서 관리자 조회 (단일 관리자 가정)
            Optional<Admin> adminOpt = adminRepository.findByAdminName("admin");

            if (adminOpt.isPresent() && adminOpt.get().getPassword().equals(hashedPassword)) {
                session.setAttribute("ADMIN_USER", adminOpt.get());
                return "redirect:/admin/dashboard";
            } else {
                model.addAttribute("error", "비밀번호가 올바르지 않습니다.");
                return "admin/login";
            }

        } catch (NoSuchAlgorithmException e) {
            model.addAttribute("error", "로그인 처리 중 오류가 발생했습니다.");
            return "admin/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
