package com.ljh.service;

import com.ljh.NotFoundException;
import com.ljh.dao.CategoryRepository;
import com.ljh.po.Category;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImplementation implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    @Override
    public Category getCategory(Long id) {
        return categoryRepository.findOne(id);
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional
    @Override
    public Page<Category> listCategory(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public List<Category> listCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> listCategoryTop(Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "blogs.size");
        Pageable pageable = new PageRequest(0, size, sort);
        return categoryRepository.findTop(pageable);
    }

    @Transactional
    @Override
    public Category updateCategory(Long id, Category category) {
        Category oldCategory = categoryRepository.findOne(id);
        if (oldCategory == null) {
            throw new NotFoundException("Category not found");
        }

        BeanUtils.copyProperties(category, oldCategory);
        return categoryRepository.save(oldCategory);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        categoryRepository.delete(id);
    }
}
