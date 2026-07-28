package com.jonatasrocha.stock.supplier;

import org.springframework.http.ResponseEntity;
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

    private final SupplierService supplierService;
    private final SupplierResponseMapper supplierResponseMapper;
    private final SupplierRequestMapper supplierRequestMapper;

    public SupplierController(
        SupplierService supplierService,
        SupplierRequestMapper supplierRequestMapper,
        SupplierResponseMapper supplierResponseMapper
    ) {
        this.supplierService = supplierService;
        this.supplierRequestMapper = supplierRequestMapper;
        this.supplierResponseMapper = supplierResponseMapper;
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

    @PostMapping
    public ResponseEntity<Response> create(@RequestBody @Valid SupplierRequest request) {
        var createResult = this.supplierService.create(this.supplierRequestMapper.map(request));
        if (createResult.isFailure()) {
            return responseFail(createResult.error());
        }
        var supplierSaved = createResult.data();
        return responseCreated(
            this.supplierResponseMapper.map(supplierSaved),
            "/v1/suppliers/{id}",
            supplierSaved.getId()
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Response> getOne(@PathVariable("id") Long id) {
        var supplierFound = this.supplierService.findSupplierById(id);
        if (supplierFound.isEmpty()) {
            return responseFail(SupplierErrorCode.SUPPLIER_NOT_FOUND);
        }
        return responseOk(this.supplierResponseMapper.map(supplierFound.get()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> update(@PathVariable("id") Long id, @RequestBody @Valid SupplierRequest request) {
        var editResult = this.supplierService.edit(id, this.supplierRequestMapper.map(request));
        if (editResult.isFailure()) {
            return responseFail(editResult.error());
        }
        return responseNoContent();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> remove(@PathVariable("id") Long id) {
        var removeResult = this.supplierService.removeById(id);
        if (removeResult.isFailure()) {
            return responseFail(removeResult.error());
        }
        return responseNoContent();
    }

}
