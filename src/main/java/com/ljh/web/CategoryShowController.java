package com.ljh.web;

import com.ljh.po.Category;
import com.ljh.service.BlogService;
import com.ljh.service.CategoryService;
import com.ljh.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class CategoryShowController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    BlogService blogService;

    @GetMapping("/categories/{id}")
    public String categories(@PageableDefault(size=5, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                             @PathVariable Long id,
                             Model model) {
        List<Category> categories = categoryService.listCategoryTop(Integer.MAX_VALUE);

        if (id == -1) {
            id = categories.get(0).getId();
        }

        BlogQuery blogQuery = new BlogQuery();
        blogQuery.setCategoryId(id);
        model.addAttribute("categories", categories);
        model.addAttribute("page", blogService.listBlog(pageable, blogQuery));
        model.addAttribute("activeCategoryId", id);

        return "categories";
    }
}
