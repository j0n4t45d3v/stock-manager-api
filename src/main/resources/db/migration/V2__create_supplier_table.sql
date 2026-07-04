DO $$
BEGIN

    CREATE TABLE IF NOT EXISTS tb_suppliers 
    (
        id         BIGSERIAL    NOT NULL,
        name       VARCHAR(60)  NOT NULL,
        email      VARCHAR(60)  NOT NULL,
        phone      VARCHAR(14)  NOT NULL,
        created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

    ALTER TABLE tb_suppliers 
        ADD CONSTRAINT pk_suppliers PRIMARY KEY (id);

    ALTER TABLE tb_suppliers 
        ADD CONSTRAINT unq_email_suppliers UNIQUE (email);

    CREATE OR REPLACE TRIGGER tg_suppliers_update_at
    BEFORE UPDATE ON tb_suppliers
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();


EXCEPTION
    WHEN OTHERS THEN ROLLBACK;
END $$;