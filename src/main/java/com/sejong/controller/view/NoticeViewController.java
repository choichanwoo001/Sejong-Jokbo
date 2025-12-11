package com.sejong.controller.view;

import com.sejong.entity.Notice;
import com.sejong.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class NoticeViewController {

    private final NoticeService noticeService;

    @GetMapping("/notice")
    public String noticeList(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Notice> noticePage = noticeService.getAllNotices(page);

        model.addAttribute("notices", noticePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", noticePage.getTotalPages());
        model.addAttribute("hasNext", noticePage.hasNext());
        model.addAttribute("hasPrevious", noticePage.hasPrevious());

        return "notice/list";
    }

    @GetMapping("/notice/{id}")
    public String noticeDetail(@PathVariable @org.springframework.lang.NonNull Integer id, Model model) {
        Notice notice = noticeService.getNoticeById(id);
        model.addAttribute("notice", notice);
        return "notice/detail";
    }
}
