package com.sejong.config;

import com.sejong.entity.Admin;
import com.sejong.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.findByAdminName("admin").isEmpty()) {
            Admin admin = new Admin();
            admin.setAdminName("admin");

            // "choi3495" SHA-256 해싱
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest("choi3495".getBytes());
            String hashedPassword = Base64.getEncoder().encodeToString(hash);

            admin.setPassword(hashedPassword);

            adminRepository.save(admin);
        }
    }
}
