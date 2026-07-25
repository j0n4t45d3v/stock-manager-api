package com.jonatasrocha.stock.supplier;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonatasrocha.stock.common.BaseController;
import com.jonatasrocha.stock.infra.http.Response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@RestController
@RequestMapping("/v1/suppliers")
public class SupplierController extends BaseController {

    private static final String SUPPLIER_NOT_FOUND_CODE = "SUPPLIER_NOT_FOUND";
    private static final String SUPPLIER_CONFLICT_CODE = "SUPPLIER_CONFLICT";

    private static final String SUPPLIER_NOT_FOUND_DEFAULT_MESSAGE = "Supplier not found";
    private static final String SUPPLIER_EMAIL_ALREADY_USED = "Already exists a supplier with this same e-mail";

    private final SupplierRepository supplierRepository;

    public SupplierController(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public record SupplierRequest(
        @Size(max = 60, message = "Name cannot has more that 60 characteres.")
        @NotBlank
        String name,

        @NotBlank
        @Email(message = "Email given is not valid")
        String email,

        @NotBlank
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone given not has valid format. give phone using pattern (E.164).")
        String phone
    ) {}

    public record SupplierResponse(
        Long id,
        String name,
        String email,
        String phone
    ) {
        public static SupplierResponse ofEntity(SupplierEntity entity) {
            return new SupplierResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmailValue(),
                entity.getPhoneValue()
            );
        }
    }

    @Transactional
    @PostMapping
    public ResponseEntity<Response> create(@RequestBody @Valid SupplierRequest request) {
        var newSupplier = SupplierEntity.of(request.name(), request.email(), request.phone());
        if (this.supplierRepository.existsByEmail(newSupplier.getEmail())) {
            return responseConflict( SUPPLIER_CONFLICT_CODE, SUPPLIER_EMAIL_ALREADY_USED);
        }
        var supplierSaved = this.supplierRepository.save(newSupplier);
        return responseCreated(
            SupplierResponse.ofEntity(supplierSaved),
            "/v1/suppliers/{id}",
            supplierSaved.getId()
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Response> getOne(@PathVariable("id") Long id) {
        var supplierFound = this.supplierRepository.findById(id);
        if (supplierFound.isEmpty()) {
            return responseNotFound(SUPPLIER_NOT_FOUND_CODE, SUPPLIER_NOT_FOUND_DEFAULT_MESSAGE);
        }
        return responseOk(SupplierResponse.ofEntity(supplierFound.get()));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Response> update(@PathVariable("id") Long id, @RequestBody @Valid SupplierRequest request) {
        var supplierFound = this.supplierRepository.findById(id);
        if (supplierFound.isEmpty()) {
            return responseNotFound(SUPPLIER_NOT_FOUND_CODE, SUPPLIER_NOT_FOUND_DEFAULT_MESSAGE);
        }

        var supplier = supplierFound.get();
        var newSupplier = SupplierEntity.of(supplier.getId(), request.name(), request.email(), request.phone());
        if (this.supplierRepository.existsByEmailAndIdNot(newSupplier.getEmail(), newSupplier.getId())) {
            return responseConflict(SUPPLIER_CONFLICT_CODE, SUPPLIER_EMAIL_ALREADY_USED);
        }

        this.supplierRepository.save(newSupplier);
        return responseNoContent();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> remove(@PathVariable("id") Long id) {
        if (!this.supplierRepository.existsById(id)) {
            return responseNotFound(SUPPLIER_NOT_FOUND_CODE, SUPPLIER_NOT_FOUND_DEFAULT_MESSAGE);
        }

        this.supplierRepository.deleteById(id);

        return responseNoContent();
    }

}
