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

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true; // 기본값은 공개

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL)
    @org.hibernate.annotations.BatchSize(size = 100)
    private List<Comment> comments;
}