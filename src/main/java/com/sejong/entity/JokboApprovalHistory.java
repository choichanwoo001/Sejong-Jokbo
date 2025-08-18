package com.sejong.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "jokbo_approval_history")
@Getter
@Setter
public class JokboApprovalHistory extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jokbo_id", nullable = false)
    private Jokbo jokbo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private ApprovalAction action;
    
    @Column(name = "previous_status")
    @Enumerated(EnumType.STRING)
    private Jokbo.JokboStatus previousStatus;
    
    @Column(name = "new_status")
    @Enumerated(EnumType.STRING)
    private Jokbo.JokboStatus newStatus;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment; // 승인/반려 사유
    
    public enum ApprovalAction {
        승인, 반려, 승인취소, 재심사요청
    }
}
