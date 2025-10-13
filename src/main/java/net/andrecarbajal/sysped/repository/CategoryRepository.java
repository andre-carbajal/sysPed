package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.Category;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends ListCrudRepository<Category, Integer> {
}
