INSERT INTO users (email, password, role)
VALUES ('admin@gmail.com', '$2a$10$xU5C2yyujgHJpmxB4PAY8uNywU8B7RQV.bfKtRIHJuTDG2Tvo3yqG', 'ADMIN')
    ON CONFLICT (email) DO NOTHING;