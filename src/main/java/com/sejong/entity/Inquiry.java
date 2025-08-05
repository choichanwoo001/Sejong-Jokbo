package com.sejong.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "inquiry")
@Getter
@Setter
public class Inquiry extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Integer inquiryId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL)
    private List<Comment> comments;
} 