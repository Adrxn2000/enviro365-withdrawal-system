-- Runs automatically after Hibernate creates the tables (see
-- spring.jpa.defer-datasource-initialization=true in application.properties).
-- IDs are left out and rely on IDENTITY auto-increment, so insertion ORDER
-- determines the IDs: first investor -> id 1, first portfolio -> id 1, etc.
-- Column names are snake_case because Hibernate's default naming strategy
-- converts Java camelCase field names (fullName -> full_name) automatically.

INSERT INTO investors (full_name, age, email) VALUES
    ('Thabo Nkosi', 68, 'thabo.nkosi@example.com'),
    ('Lerato Dube', 42, 'lerato.dube@example.com');

INSERT INTO portfolios (investor_id, balance) VALUES
    (1, 250000.00),
    (2, 90000.00);

INSERT INTO products (name, type, portfolio_id) VALUES
    ('Retirement Annuity', 'RETIREMENT_FUND', 1),
    ('Unit Trust - Balanced Fund', 'UNIT_TRUST', 1),
    ('Money Market Fund', 'MONEY_MARKET', 2);
