package com.jonatasrocha.stock.supplier;

import java.util.Map;

import org.springframework.http.HttpStatus;
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
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@RestController
@RequestMapping("/v1/suppliers")
public class SupplierController {

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
    public ResponseEntity<?> create(@RequestBody @Valid SupplierRequest request) {
        var newSupplier = SupplierEntity.of(request.name(), request.email(), request.phone());
        if (this.supplierRepository.existsByEmail(newSupplier.getEmail())) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Already exists a supplier with this same e-mail"));
        }
        var supplierSaved = this.supplierRepository.save(newSupplier);
        var location = UriComponentsBuilder
                        .fromPath("/{id}")
                        .buildAndExpand(supplierSaved.getId())
                        .toUri();

        return ResponseEntity
            .created(location)
            .body(SupplierResponse.ofEntity(supplierSaved));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") Long id) {
        var supplierFound = this.supplierRepository.findById(id);
        if (supplierFound.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Supplier not found"));
        }
        return ResponseEntity.ok(SupplierResponse.ofEntity(supplierFound.get()));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid SupplierRequest request) {
        var supplierFound = this.supplierRepository.findById(id);
        if (supplierFound.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Supplier not found"));
        }

        var supplier = supplierFound.get();
        var newSupplier = SupplierEntity.of(supplier.getId(), request.name(), request.email(), request.phone());
        if (this.supplierRepository.existsByEmailAndIdNot(newSupplier.getEmail(), newSupplier.getId())) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Already exists a supplier with this same e-mail"));
        }

        this.supplierRepository.save(newSupplier);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable("id") Long id) {
        if (!this.supplierRepository.existsById(id)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Supplier not found"));
        }

        this.supplierRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
