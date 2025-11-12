package com.sejong.repository;

import com.sejong.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    
    /**
     * 관리자 이름으로 관리자를 찾습니다
     */
    Optional<Admin> findByAdminName(String adminName);
}
