package com.yixin.patrol.repository;

import com.yixin.patrol.entity.TaskTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, Long> {

    List<TaskTemplate> findByStatus(Integer status);

    List<TaskTemplate> findByCategory(String category);

    List<TaskTemplate> findByCategoryAndStatus(String category, Integer status);

    @Query("SELECT DISTINCT t.category FROM TaskTemplate t WHERE t.status = 1")
    List<String> findAllCategories();

    @Query("SELECT t FROM TaskTemplate t WHERE t.status = 1 ORDER BY t.createTime DESC")
    List<TaskTemplate> findAllActive();

    @Query("SELECT COUNT(t) FROM TaskTemplate t WHERE t.status = 1")
    Long countActive();
}
