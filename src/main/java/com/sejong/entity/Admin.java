package com.sejong.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "admin")
@Getter
@Setter
public class Admin extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Integer adminId;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "admin_name", nullable = false, length = 100)
    private String adminName;
} 