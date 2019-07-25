package com.ljh.web.admin;

import com.ljh.po.Category;
import com.ljh.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String categories(@PageableDefault(size = 3, sort = {"id"}, direction = Sort.Direction.DESC)
                                         Pageable pageable, Model model) {
        model.addAttribute("page", categoryService.listCategory(pageable));
        return "admin/category management";
    }

    @GetMapping("/categories/input")
    public String input(Model model) {
        model.addAttribute("category", new Category());
        return "admin/new category";
    }

    @GetMapping("/categories/{id}/input")
    public String toEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategory(id));
        return "admin/new category";
    }

    @PostMapping("/categories")
    public String post(@Valid Category category, BindingResult result, RedirectAttributes attributes) {

        if (categoryService.getCategoryByName(category.getName()) != null) {
            result.rejectValue("name", "nameDuplicateError", "Duplicate category is not allowed.");
        }

        if (result.hasErrors()) {
            return "admin/new category";
        }

        Category newCategory = categoryService.saveCategory(category);
        if (newCategory == null) {
            attributes.addFlashAttribute("message", "Failed to add new category.");
        } else {
            attributes.addFlashAttribute("message", "Successfully added!");
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}")
    public String edit(@Valid Category category, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {

        if (categoryService.getCategoryByName(category.getName()) != null) {
            result.rejectValue("name", "nameDuplicateError", "Duplicate category is not allowed.");
        }

        if (result.hasErrors()) {
            return "admin/new category";
        }

        Category newCategory = categoryService.updateCategory(id, category);
        if (newCategory == null) {
            attributes.addFlashAttribute("message", "Failed to edit new category.");
        } else {
            attributes.addFlashAttribute("message", "Successfully updated!");
        }

        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        categoryService.deleteCategory(id);
        attributes.addFlashAttribute("message", "Successfully deleted!");
        return "redirect:/admin/categories";
    }

}
