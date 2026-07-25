DO $$
BEGIN

    CREATE TABLE IF NOT EXISTS tb_products_movements
    (
        id           BIGSERIAL    NOT NULL,
        quantity     NUMERIC(7,4) NOT NULL,
        unit_price   NUMERIC(7,4) NOT NULL,
        type         SMALLINT     NOT NULL,
        note         VARCHAR(255),
        product_id   BIGINT       NOT NULL,
        created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

    ALTER TABLE tb_products_movements 
        ADD CONSTRAINT pk_products_movements PRIMARY KEY (id);

    ALTER TABLE tb_products_movements 
        ADD CONSTRAINT fk_products_movements_product FOREIGN KEY(product_id) REFERENCES tb_products(id);

    ALTER TABLE tb_products_movements 
        ADD CONSTRAINT chk_products_movements_quantity CHECK (quantity > 0);

    CREATE OR REPLACE TRIGGER tg_products_movements_update_at
    BEFORE UPDATE ON tb_products_movements
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

EXCEPTION
    WHEN OTHERS THEN ROLLBACK;
END $$;