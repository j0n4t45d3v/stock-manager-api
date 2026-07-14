DO $$
BEGIN

    CREATE TABLE IF NOT EXISTS tb_categories 
    (
        id         BIGSERIAL    NOT NULL,
        name       VARCHAR(40)  NOT NULL,
        created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

    ALTER TABLE tb_categories 
        ADD CONSTRAINT pk_categories PRIMARY KEY (id);

    ALTER TABLE tb_categories 
        ADD CONSTRAINT unq_name_categories UNIQUE (name);

    CREATE OR REPLACE TRIGGER tg_categories_update_at
    BEFORE UPDATE ON tb_categories
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();


EXCEPTION
    WHEN OTHERS THEN ROLLBACK;
END $$;