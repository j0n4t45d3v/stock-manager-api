DO $$
BEGIN

    CREATE TABLE IF NOT EXISTS tb_products 
    (
        id           BIGSERIAL    NOT NULL,
        name         VARCHAR(40)  NOT NULL,
        description  VARCHAR(255) NOT NULL,
        sku          VARCHAR(50)  NOT NULL,
        unit_price   NUMERIC(7,3) NOT NULL,
        category_id  BIGINT       NOT NULL,
        supplier_id  BIGINT       NOT NULL,
        created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

    ALTER TABLE tb_products 
        ADD CONSTRAINT pk_products PRIMARY KEY (id);

    ALTER TABLE tb_products 
        ADD CONSTRAINT fk_category_product FOREIGN KEY(category_id) REFERENCES tb_categories(id);

    ALTER TABLE tb_products 
        ADD CONSTRAINT fk_supplier_product FOREIGN KEY(supplier_id) REFERENCES tb_suppliers(id);

    ALTER TABLE tb_products 
        ADD CONSTRAINT unq_sku UNIQUE (sku);

    CREATE OR REPLACE TRIGGER tg_products_update_at
    BEFORE UPDATE ON tb_products
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

EXCEPTION
    WHEN OTHERS THEN ROLLBACK;
END $$;