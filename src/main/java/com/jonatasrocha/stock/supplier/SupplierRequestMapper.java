package com.jonatasrocha.stock.supplier;

import org.springframework.stereotype.Component;

import com.jonatasrocha.stock.infra.http.Mapper;
import com.jonatasrocha.stock.supplier.SupplierController.SupplierRequest;

@Component
public class SupplierRequestMapper implements Mapper<SupplierRequest, SupplierEntity> {

    @Override
    public SupplierEntity map(SupplierRequest input) {
        return SupplierEntity.of(input.name(), input.email(), input.phone());
    }

}
