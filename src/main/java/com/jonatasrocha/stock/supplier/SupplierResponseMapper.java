package com.jonatasrocha.stock.supplier;

import org.springframework.stereotype.Component;

import com.jonatasrocha.stock.infra.http.Mapper;
import com.jonatasrocha.stock.supplier.SupplierController.SupplierResponse;

@Component
public class SupplierResponseMapper implements Mapper<SupplierEntity, SupplierResponse> {

    @Override
    public SupplierResponse map(SupplierEntity input) {
            return new SupplierResponse(
                input.getId(),
                input.getName(),
                input.getEmailValue(),
                input.getPhoneValue()
            );
    }

}
