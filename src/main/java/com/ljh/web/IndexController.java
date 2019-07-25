package com.ljh.web;

import com.ljh.NotFoundException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String index(@PageableDefault(size=5, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("page", blogService.listBlog(pageable));
        model.addAttribute("categories", categoryService.listCategoryTop(5));
        model.addAttribute("recommendedBlogs", blogService.listRecommendedBlogTop(5));

        return "index";
    }

    @GetMapping("/blogs/{id}")
    public String blog(@PathVariable Long id, Model model) {
        model.addAttribute("blog", blogService.getAndConvert(id));

        return "blog";
    }

    @PostMapping("/search")
    public String search(@PageableDefault(size=5, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam String query, Model model) {

        model.addAttribute("page", blogService.listBlog("%"+query+"%", pageable));
        model.addAttribute("query", query);

        return "search";
    }

    @GetMapping("/footer/newBlogs")
    public String footerNewBlog(Model model) {
        //model.addAttribute("newBlogs", blogService.listRecommendedBlogTop(3));
        model.addAttribute("page", blogService.listLatestBlogs(3));
        return "_fragments :: newBlogList";
    }


}
