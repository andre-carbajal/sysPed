package net.andrecarbajal.sysped.service;

import lombok.RequiredArgsConstructor;
import net.andrecarbajal.sysped.model.Category;
import net.andrecarbajal.sysped.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findAllCategories() {
        return this.categoryRepository.findAll();
    }

    public List<Category> findAllCategoriesWithPlates() {
        return this.categoryRepository.findAllCategoriesWithPlates();
    }
}
