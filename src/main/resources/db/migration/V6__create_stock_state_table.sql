DO $$
BEGIN

    CREATE TABLE IF NOT EXISTS tb_stock_state
    (
        id           BIGSERIAL     NOT NULL,
        balance      NUMERIC(10,4) NOT NULL,
        product_id   BIGINT        NOT NULL,
        created_at   TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at   TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

    ALTER TABLE tb_stock_state 
        ADD CONSTRAINT pk_stock_state PRIMARY KEY (id);

    ALTER TABLE tb_stock_state 
        ADD CONSTRAINT fk_products_stock FOREIGN KEY(product_id) REFERENCES tb_products(id);


    CREATE OR REPLACE TRIGGER tg_stock_update_at
    BEFORE UPDATE ON tb_stock_state
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

EXCEPTION
    WHEN OTHERS THEN ROLLBACK;
END $$;