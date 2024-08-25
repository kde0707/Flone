package org.hermez.admin.controller;

import org.hermez.admin.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("flone/admin")
public class AdminMainController {

    private final AdminService adminService;

    public AdminMainController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("main.hm")
    public String getAdminMainPage(Model model) {
        model.addAttribute("adminMainResponse", adminService.getAdminMain());
        model.addAttribute("monthlySignupChartData", adminService.getMonthlySignupStatistics());
        model.addAttribute("monthlyPaymentChartData", adminService.getMonthlyPaymentStatistics());
        model.addAttribute("courseCountBySubjectChartData", adminService.getCourseCountBySubjectStatistics());
        model.addAttribute("classroomCreationData", adminService.getMonthlyClassroomCreationStatistics());
        model.addAttribute("monthlyCourseCountChartData", adminService.getMonthlyCourseCountStatistics());
        return "flone/admin-main";
    }
}