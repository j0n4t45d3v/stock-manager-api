DO $$
BEGIN

    ALTER TABLE tb_stock_state 
        ADD COLUMN reserved_balance NUMERIC(10,4) NOT NULL DEFAULT 0;

    ALTER TABLE tb_stock_state 
        ADD COLUMN available_balance NUMERIC(10,4) GENERATED ALWAYS AS (balance - reserved_balance) STORED;

    ALTER TABLE tb_stock_state
        ADD CONSTRAINT chk_stock_state_reserved_balance CHECK (reserved_balance <= balance);

EXCEPTION
    WHEN OTHERS THEN ROLLBACK;
END $$;