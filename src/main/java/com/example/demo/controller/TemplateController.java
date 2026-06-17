package com.example.demo.controller;

import com.example.demo.model.Template;
import com.example.demo.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("templates", templateService.findAll());
        return "template-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("template", new Template());
        return "template-form";
    }

    @PostMapping
    public String create(@ModelAttribute Template template,
                         RedirectAttributes redirectAttributes) {
        templateService.create(template);
        redirectAttributes.addFlashAttribute("message", "Template created successfully");
        return "redirect:/templates";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("template", templateService.findById(id));
        return "template-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Template template,
                         RedirectAttributes redirectAttributes) {
        templateService.update(id, template);
        redirectAttributes.addFlashAttribute("message", "Template updated successfully");
        return "redirect:/templates";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        templateService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Template deleted successfully");
        return "redirect:/templates";
    }

    @GetMapping("/api")
    @ResponseBody
    public List<Template> listApi() {
        return templateService.findAll();
    }
}
