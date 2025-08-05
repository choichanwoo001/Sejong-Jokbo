package com.sejong.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 