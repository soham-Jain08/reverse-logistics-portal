package com.soham.reverselogistics.controller;

import com.soham.reverselogistics.dto.ReturnRequestForm;
import com.soham.reverselogistics.model.ReturnRequest;
import com.soham.reverselogistics.service.ReturnRequestService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/returns")
public class ReturnRequestController {

    // TODO (Task 6): replace with the username of the logged-in customer
    private static final String DEMO_CUSTOMER = "customer1";

    private final ReturnRequestService service;

    public ReturnRequestController(ReturnRequestService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("returns", service.findAllNewestFirst());
        return "returns/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("form", new ReturnRequestForm());
        return "returns/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") ReturnRequestForm form,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "returns/form";
        }
        ReturnRequest saved = service.create(form, DEMO_CUSTOMER);
        redirect.addFlashAttribute("message",
                "Return request #" + saved.getId() + " created with status REQUESTED.");
        return "redirect:/returns";
    }
}
