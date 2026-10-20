package com.soham.reverselogistics.controller;

import com.soham.reverselogistics.dto.DashboardStats;
import com.soham.reverselogistics.model.ReturnStatus;
import com.soham.reverselogistics.security.CurrentUser;
import com.soham.reverselogistics.service.ReturnRequestService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Displays summary metrics, action queues and recent returns. */
@Controller
public class DashboardController {

    private final ReturnRequestService service;

    public DashboardController(ReturnRequestService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        CurrentUser user = CurrentUser.from(authentication);
        DashboardStats stats = service.getDashboardStats(user);
        model.addAttribute("stats", stats);
        model.addAttribute("statuses", ReturnStatus.values());
        return "dashboard";
    }
}
