package net.andrecarbajal.sysped.repository;

import net.andrecarbajal.sysped.model.Subcategory;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubcategoryRepository extends ListCrudRepository<Subcategory, Integer> {
}
