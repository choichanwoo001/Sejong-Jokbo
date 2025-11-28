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

    @Column(name = "content_url", length = 500)
    private String contentUrl;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // 텍스트 족보 내용

    @Column(name = "content_type", length = 20)
    private String contentType; // "text" 또는 "file"

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private JokboStatus status = JokboStatus.대기;

    @Column(name = "download_count")
    private Long downloadCount = 0L;

    public enum JokboStatus {
        대기, 승인, 반려
    }
}