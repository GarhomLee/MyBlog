package com.ljh.web.admin;

import com.ljh.po.Blog;
import com.ljh.po.User;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class BlogController {

    //private static

    @Autowired
    private BlogService blogService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size=30, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog,
                        Model model) {

        model.addAttribute("categories", categoryService.listCategory());
        model.addAttribute("page", blogService.listBlog(pageable));
        return "admin/blog management";
    }

    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size=3, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blog,
                        Model model) {
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        //System.out.println(blog.getTitle() + "," + blog.getCategoryId());
        //model.addAttribute("page", blogService.listBlog("%"+blog.getTitle()+"%", blog.getCategoryId(), blog.getIsRecommended(), pageable));
        return "admin/blog management :: blogList";
    }

    @GetMapping("/blogs/input")
    public String input(Model model) {
        model.addAttribute("categories", categoryService.listCategory());
        model.addAttribute("blog", new Blog());
        return "admin/new blog";
    }

    @GetMapping("/blogs/{id}/input")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("categories", categoryService.listCategory());
        model.addAttribute("blog", blogService.getBlog(id));
        return "admin/new blog";
    }


    @PostMapping("/blogs")
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session) {
        blog.setPublisher((User) session.getAttribute("user"));
        blog.setCategory(categoryService.getCategory(blog.getCategory().getId()));


        Blog newBlog;
        if (blog.getId() == null) {  // this is a new blog, save it
            newBlog = blogService.saveBlog(blog);
        } else {  // this is an old blog, upate it
            newBlog = blogService.updateBlog(blog.getId(), blog);
        }


        if (newBlog == null) {
            attributes.addFlashAttribute("message", "Failed to add new blog.");
        } else {
            attributes.addFlashAttribute("message", "Successfully added!");
        }

        return "redirect:/admin/blogs";
    }

    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "Successfully deleted!");
        return "redirect:/admin/blogs";
    }

}

