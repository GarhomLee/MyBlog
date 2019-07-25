package com.ljh.dao;

import com.ljh.po.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {

    @Query("select blog from Blog blog where blog.isRecommended = true")
    List<Blog> findTop(Pageable pageable);

    @Query("select blog from Blog blog where blog.title like ?1 or blog.content like ?1")
    Page<Blog> findByQuery(String query, Pageable pageable);

    //@Query("select blog from Blog blog where blog.title like ?1 and blog.category.id = ?2 and blog.isRecommended = ?3")
    //Page<Blog> findByQuery(String title, Long categoryId, boolean isRecommended, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Blog blog set blog.views = blog.views + 1 where blog.id = ?1")
    int updateViews(Long id);

    @Query("select function('date_format', blog.updateTime, '%Y') as year from Blog blog group by function('date_format', blog.updateTime, '%Y') order by year desc")
    List<String> findGroupYear();

    @Query("select blog from Blog blog where function('date_format', blog.updateTime, '%Y') = ?1")
    List<Blog> findByYear(String year);

}
