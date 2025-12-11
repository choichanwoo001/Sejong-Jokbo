package com.sejong.service;

import com.sejong.entity.Notice;
import com.sejong.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 모든 공지사항을 페이징하여 조회합니다 (15개씩)
     */
    @Transactional(readOnly = true)
    public Page<Notice> getAllNotices(int page) {
        Pageable pageable = PageRequest.of(page, 15);
        return noticeRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * 공지사항 ID로 조회하고 조회수를 증가시킵니다.
     */
    @Transactional
    public Notice getNoticeById(@org.springframework.lang.NonNull Integer noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
        notice.setViewCount(notice.getViewCount() + 1);
        return notice;
    }

    /**
     * 공지사항 ID로 조회합니다 (수정용, 조회수 증가 없음)
     */
    @Transactional(readOnly = true)
    public Notice getNoticeByIdForEdit(@org.springframework.lang.NonNull Integer noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
    }

    /**
     * 공지사항을 생성합니다.
     */
    @Transactional
    public Notice createNotice(String title, String content) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setViewCount(0);
        return noticeRepository.save(notice);
    }

    /**
     * 공지사항을 수정합니다.
     */
    @Transactional
    public Notice updateNotice(@org.springframework.lang.NonNull Integer noticeId, String title, String content) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
        notice.setTitle(title);
        notice.setContent(content);
        return notice;
    }

    /**
     * 공지사항을 삭제합니다.
     */
    @Transactional
    public void deleteNotice(@org.springframework.lang.NonNull Integer noticeId) {
        noticeRepository.deleteById(noticeId);
    }

    /**
     * 전체 공지사항 수를 반환합니다.
     */
    @Transactional(readOnly = true)
    public long countAllNotices() {
        return noticeRepository.count();
    }
}
