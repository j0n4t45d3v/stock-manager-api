package com.jonatasrocha.stock.supplier;

import java.time.Instant;

import com.jonatasrocha.stock.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_suppliers")
public class SupplierEntity extends BaseEntity {

    private String name;
    private Email email;
    private Phone phone;

    protected SupplierEntity() {
        this(null, null, null,null,null, null);
    }

    private SupplierEntity(
        Long id,
        String name,
        Email email,
        Phone phone,
        Instant createdAt,
        Instant updatedAt
    ) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.email = email;
        this.phone = phone;

    }

    public static SupplierEntity of(String name, String email, String phone){
        return SupplierEntity.of(null, name, email, phone);
    }

    public static SupplierEntity of(Long id, String name, String email, String phone){
        return new SupplierEntity(
            id, 
            name,
            new Email(email),
            new Phone(phone),
            null,
            null
        );
    }

    public String getName() {
        return this.name;
    }

    public Email getEmail() {
        return this.email;
    }
    
    public String getEmailValue() {
        return this.email.value();
    }

    public Phone getPhone() {
        return this.phone;
    }

    public String getPhoneValue() {
        return this.phone.value();
    }

    @Embeddable
    public record Email(@Column(name = "email") String value) {
    }

    @Embeddable
    public record Phone(@Column(name = "phone") String value) {
    }

}
