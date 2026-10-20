package com.soham.reverselogistics.controller;

import com.soham.reverselogistics.dto.ActionForm;
import com.soham.reverselogistics.dto.NotesForm;
import com.soham.reverselogistics.dto.ReturnRequestForm;
import com.soham.reverselogistics.model.ReturnAction;
import com.soham.reverselogistics.model.ReturnCategory;
import com.soham.reverselogistics.model.ReturnRequest;
import com.soham.reverselogistics.model.ReturnStatus;
import com.soham.reverselogistics.security.CurrentUser;
import com.soham.reverselogistics.service.ReturnRequestService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Create, view, update, search and process return requests. */
@Controller
@RequestMapping("/returns")
public class ReturnRequestController {

    private final ReturnRequestService service;

    public ReturnRequestController(ReturnRequestService service) {
        this.service = service;
    }

    // ------------------------------------------------------------------ list

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q,
                       @RequestParam(defaultValue = "") String status,
                       @RequestParam(defaultValue = "") String category,
                       @RequestParam(defaultValue = "0") int page,
                       Authentication authentication, Model model) {
        CurrentUser user = CurrentUser.from(authentication);
        Page<ReturnRequest> results = service.search(user, q, status, category, page);
        model.addAttribute("results", results);
        model.addAttribute("q", q);
        model.addAttribute("status", status.trim().toUpperCase());
        model.addAttribute("category", category.trim().toUpperCase());
        model.addAttribute("statuses", ReturnStatus.values());
        model.addAttribute("categories", ReturnCategory.values());
        return "returns/list";
    }

    // ---------------------------------------------------------------- create

    @GetMapping("/new")
    public String showForm(Authentication authentication, Model model) {
        ReturnRequestForm form = new ReturnRequestForm();
        form.setCustomerName(CurrentUser.from(authentication).getDisplayName());
        prepareForm(model, form, false, null);
        return "returns/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") ReturnRequestForm form, BindingResult result,
                         Authentication authentication, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("categories", ReturnCategory.values());
            model.addAttribute("editMode", false);
            return "returns/form";
        }
        ReturnRequest saved = service.create(form, CurrentUser.from(authentication));
        redirect.addFlashAttribute("message", "Return request " + saved.getReturnCode()
                + " was created. You can follow its progress below.");
        return "redirect:/returns/" + saved.getId();
    }

    // ------------------------------------------------------------------ view

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Authentication authentication, Model model) {
        CurrentUser user = CurrentUser.from(authentication);
        ReturnRequest request = service.get(id, user);
        model.addAttribute("r", request);
        model.addAttribute("history", service.history(id));
        model.addAttribute("actions", service.availableActions(request, user));
        model.addAttribute("notesForm", NotesForm.from(request));
        model.addAttribute("canEdit", service.canCustomerEdit(request, user));
        model.addAttribute("canAnnotate", service.canAnnotate(request, user));
        model.addAttribute("canPrintLabel", service.canPrintLabel(request));
        model.addAttribute("steps", new String[] {"Requested", "Approved", "Pickup scheduled",
                "Received", "Inspected", "Resolved"});
        return "returns/detail";
    }

    // ------------------------------------------------------------------ edit

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Authentication authentication, Model model,
                           RedirectAttributes redirect) {
        CurrentUser user = CurrentUser.from(authentication);
        ReturnRequest request = service.get(id, user);
        try {
            service.assertCustomerCanEdit(request, user);
        } catch (IllegalStateException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/returns/" + id;
        }
        prepareForm(model, ReturnRequestForm.from(request), true, request);
        return "returns/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("form") ReturnRequestForm form,
                       BindingResult result, Authentication authentication, Model model,
                       RedirectAttributes redirect) {
        CurrentUser user = CurrentUser.from(authentication);
        if (result.hasErrors()) {
            model.addAttribute("categories", ReturnCategory.values());
            model.addAttribute("editMode", true);
            model.addAttribute("r", service.get(id, user));
            return "returns/form";
        }
        try {
            service.updateByCustomer(id, form, user);
            redirect.addFlashAttribute("message", "Your changes were saved.");
        } catch (IllegalStateException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/returns/" + id;
    }

    // -------------------------------------------------------------- workflow

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam String action,
                               @ModelAttribute ActionForm form, Authentication authentication,
                               RedirectAttributes redirect) {
        try {
            ReturnRequest saved = service.applyAction(id, ReturnAction.fromParam(action), form,
                    CurrentUser.from(authentication));
            redirect.addFlashAttribute("message", "Status changed to \"" + saved.getStatus().getLabel() + "\".");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/returns/" + id;
    }

    @PostMapping("/{id}/notes")
    public String updateNotes(@PathVariable Long id, @ModelAttribute NotesForm form,
                              Authentication authentication, RedirectAttributes redirect) {
        try {
            service.updateNotes(id, form, CurrentUser.from(authentication));
            redirect.addFlashAttribute("message", "Notes updated.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/returns/" + id;
    }

    // ----------------------------------------------------------------- label

    @GetMapping("/{id}/label")
    public String label(@PathVariable Long id, Authentication authentication, Model model,
                        RedirectAttributes redirect) {
        ReturnRequest request = service.get(id, CurrentUser.from(authentication));
        if (!service.canPrintLabel(request)) {
            redirect.addFlashAttribute("error", "A return label is available after the request is approved.");
            return "redirect:/returns/" + id;
        }
        model.addAttribute("r", request);
        return "returns/label";
    }

    // ---------------------------------------------------------------- export

    @GetMapping("/export")
    public void export(@RequestParam(defaultValue = "") String q,
                       @RequestParam(defaultValue = "") String status,
                       @RequestParam(defaultValue = "") String category,
                       Authentication authentication, HttpServletResponse response) throws IOException {
        List<ReturnRequest> rows = service.searchAll(CurrentUser.from(authentication), q, status, category);
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"return-requests.csv\"");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        PrintWriter out = response.getWriter();
        out.println("Return code,Order number,Customer,Product,Quantity,Category,Status,Refund amount,Pickup date,Created at");
        for (ReturnRequest r : rows) {
            out.println(String.join(",",
                    csv(r.getReturnCode()), csv(r.getOrderNumber()), csv(r.getCustomerName()),
                    csv(r.getProductName()), csv(r.getQuantity()),
                    csv(r.getCategory() == null ? "" : r.getCategory().getLabel()),
                    csv(r.getStatus().getLabel()), csv(r.getRefundAmount()), csv(r.getPickupDate()),
                    csv(r.getCreatedAt().format(fmt))));
        }
        out.flush();
    }

    // --------------------------------------------------------------- helpers

    private void prepareForm(Model model, ReturnRequestForm form, boolean editMode, ReturnRequest request) {
        model.addAttribute("form", form);
        model.addAttribute("categories", ReturnCategory.values());
        model.addAttribute("editMode", editMode);
        if (request != null) {
            model.addAttribute("r", request);
        }
    }

    /** Quotes a value for CSV and neutralises spreadsheet formulas. */
    private static String csv(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        if (!text.isEmpty() && "=+-@".indexOf(text.charAt(0)) >= 0) {
            text = "'" + text;
        }
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            text = "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
