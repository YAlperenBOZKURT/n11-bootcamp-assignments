INSERT INTO payment_method_types (code, display_name)
SELECT 'CREDIT_CARD', 'Kredi Kartı'
    WHERE NOT EXISTS (SELECT 1 FROM payment_method_types WHERE code = 'CREDIT_CARD');

INSERT INTO payment_method_types (code, display_name)
SELECT 'PAYPAL', 'PayPal'
    WHERE NOT EXISTS (SELECT 1 FROM payment_method_types WHERE code = 'PAYPAL');