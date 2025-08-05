package com.sejong.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "jokbo")
@Getter
@Setter
public class Jokbo extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jokbo_id")
    private Integer jokboId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(name = "uploader_name", nullable = false, length = 100)
    private String uploaderName;
    
    @Column(name = "content_url", nullable = false, length = 500)
    private String contentUrl;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private JokboStatus status = JokboStatus.대기;
    
    public enum JokboStatus {
        대기, 승인, 반려
    }
} 