package com.jonatasrocha.stock.supplier;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonatasrocha.stock.supplier.SupplierEntity.Email;

public interface SupplierRepository extends JpaRepository<SupplierEntity, Long>{

    boolean existsByEmail(Email email);
    boolean existsByEmailNotAndId(Email email, Long id);
}
