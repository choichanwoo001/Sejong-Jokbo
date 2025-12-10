package com.sejong.controller.view;

import com.sejong.entity.Notice;
import com.sejong.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Notice> noticePage = noticeService.getAllNotices(page);

        model.addAttribute("notices", noticePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", noticePage.getTotalPages());
        model.addAttribute("hasNext", noticePage.hasNext());
        model.addAttribute("hasPrevious", noticePage.hasPrevious());

        return "admin/notice/list";
    }

    @GetMapping("/create")
    public String createForm() {
        return "admin/notice/form";
    }

    @PostMapping("/create")
    public String create(@RequestParam String title, @RequestParam String content,
            RedirectAttributes redirectAttributes) {
        noticeService.createNotice(title, content);
        redirectAttributes.addFlashAttribute("message", "공지사항이 등록되었습니다.");
        return "redirect:/admin/notice";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable @org.springframework.lang.NonNull Integer id, Model model) {
        Notice notice = noticeService.getNoticeByIdForEdit(id);
        model.addAttribute("notice", notice);
        return "admin/notice/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable @org.springframework.lang.NonNull Integer id, @RequestParam String title,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {
        noticeService.updateNotice(id, title, content);
        redirectAttributes.addFlashAttribute("message", "공지사항이 수정되었습니다.");
        return "redirect:/admin/notice";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable @org.springframework.lang.NonNull Integer id,
            RedirectAttributes redirectAttributes) {
        noticeService.deleteNotice(id);
        redirectAttributes.addFlashAttribute("message", "공지사항이 삭제되었습니다.");
        return "redirect:/admin/notice";
    }
}
