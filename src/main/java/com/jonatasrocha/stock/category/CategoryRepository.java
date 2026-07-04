package com.jonatasrocha.stock.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);

}
