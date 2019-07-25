package com.ljh.service;

import com.ljh.NotFoundException;
import com.ljh.dao.BlogRepository;
import com.ljh.po.Blog;
import com.ljh.po.Category;
import com.ljh.util.MarkdownUtils;
import com.ljh.util.MyBeanUtils;
import com.ljh.vo.BlogQuery;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class BlogServiceImplementation implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.findOne(id);
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = blogRepository.findOne(id);
        if (blog == null) {
            throw new NotFoundException("This blog does not exist.");
        }

        Blog temp = new Blog();  // avoid changing the content in the original blog in database
        BeanUtils.copyProperties(blog, temp);
        String content = temp.getContent();
        temp.setContent(MarkdownUtils.markdownToHtmlExtensions(content));

        blogRepository.updateViews(id);

        return temp;
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (blog.getTitle() != null && !blog.getTitle().isEmpty()) {
                    predicates.add(cb.like(root.<String>get("title"), "%"+blog.getTitle()+"%"));
                }

                if (blog.getCategoryId() != null) {
                    predicates.add(cb.equal(root.<Category>get("category").get("id"), blog.getCategoryId()));
                }

                if (blog.getIsRecommended()) {
                    predicates.add(cb.equal(root.<Boolean>get("isRecommended"), blog.getIsRecommended()));
                }

                cq.where(predicates.toArray(new Predicate[predicates.size()]));

                return null;
            }
        }, pageable);

    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query, pageable);
    }

    /*@Override
    public Page<Blog> listBlog(String title, Long categoryId, boolean isRecommended, Pageable pageable) {
        return blogRepository.findByQuery(title, categoryId, isRecommended, pageable);
    }*/

    @Override
    public List<Blog> listRecommendedBlogTop(Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "updateTime");
        Pageable pageable = new PageRequest(0, size, sort);
        return blogRepository.findTop(pageable);
    }

    @Override
    public Page<Blog> listLatestBlogs(Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = new PageRequest(0, size, sort);
        return blogRepository.findAll(pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        Map<String, List<Blog>> yearToBlogList = new HashMap<>();
        for (String year: years) {
            yearToBlogList.put(year, blogRepository.findByYear(year));
        }

        return yearToBlogList;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if (blog.getId() == null) {
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);

            if (blog.getFlag() == null) System.out.println("no flag");
            else System.out.println(blog.getFlag());

        } else {
            blog.setUpdateTime(new Date());
        }

        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog oldBlog = blogRepository.findOne(id);
        if (oldBlog == null) {
            throw new NotFoundException("This blog does not exist");
        }

        BeanUtils.copyProperties(blog, oldBlog, MyBeanUtils.getNullPropertyNames(blog));
        oldBlog.setUpdateTime(new Date());
        return blogRepository.save(oldBlog);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.delete(id);
    }
}
