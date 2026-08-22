CREATE TABLE ingredient
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    unit VARCHAR(50)  NOT NULL
);

CREATE TABLE recipe
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    portions     INT          NOT NULL,
    instructions TEXT,
    created_by   VARCHAR(255),
    created_at   TIMESTAMP
);

CREATE TABLE recipe_ingredient
(
    id            BIGSERIAL PRIMARY KEY,
    recipe_id     BIGINT  NOT NULL REFERENCES recipe (id),
    ingredient_id BIGINT  NOT NULL REFERENCES ingredient (id),
    quantity      NUMERIC NOT NULL
);

CREATE TABLE shopping_list
(
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE shopping_list_item
(
    id               BIGSERIAL PRIMARY KEY,
    shopping_list_id BIGINT  NOT NULL REFERENCES shopping_list (id),
    ingredient_id    BIGINT  NOT NULL REFERENCES ingredient (id),
    quantity         NUMERIC NOT NULL,
    is_checked       BOOLEAN NOT NULL DEFAULT FALSE
);