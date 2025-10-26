package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.plates")
    List<Category> findAllCategoriesWithPlates();
}
