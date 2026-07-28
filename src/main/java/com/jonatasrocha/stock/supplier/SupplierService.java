package com.jonatasrocha.stock.supplier;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonatasrocha.stock.common.ErrorCode;
import com.jonatasrocha.stock.common.Result;

@Service
public class SupplierService {


    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    private final SupplierRepository supplierRepository;

    @Transactional
    public Result<SupplierEntity, ErrorCode> create(SupplierEntity supplier) {
        if (this.supplierRepository.existsByEmail(supplier.getEmail())) {
            return Result.failure(SupplierErrorCode.SUPPLIER_CONFLICT);
        }
        var supplierSaved = this.supplierRepository.save(supplier);
        return Result.success(supplierSaved);
    }

    @Transactional
    public Result<Void, ErrorCode> edit(Long id, SupplierEntity supplier) {
        if (!this.supplierRepository.existsById(id)) {
            return Result.failure(SupplierErrorCode.SUPPLIER_NOT_FOUND);
        }

        var newSupplier = SupplierEntity.of(id, supplier);
        if (this.supplierRepository.existsByEmailAndIdNot(newSupplier.getEmail(), newSupplier.getId())) {
            return Result.failure(SupplierErrorCode.SUPPLIER_CONFLICT);
        }

        this.supplierRepository.save(newSupplier);
        return Result.successVoid();
    }

    @Transactional
    public Result<Void, ErrorCode> removeById(Long id) {
        if (!this.supplierRepository.existsById(id)) {
            return Result.failure(SupplierErrorCode.SUPPLIER_NOT_FOUND);
        }

        this.supplierRepository.deleteById(id);
        return Result.successVoid();
    }

    public Optional<SupplierEntity> findSupplierById(Long id) {
        return this.supplierRepository.findById(id);
    }

}
