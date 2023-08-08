-- User

INSERT INTO user (user_key, token, email, password)
VALUES ('P-88e1db688a',
        'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJQLTg4ZTFkYjY4OGEiLCJpYXQiOjE2OTE0NTE4NTAsImV4cCI6MTY5MTQ1OTA1MH0.NKJt-9mGkKvDpzraySXRj36nayKCuYQJnNUsc3kCgcPrGSNgqn03OjPxIlbka0vmnzqlARzCHawV3gvs5WGgLg',
        'dexter.wilkinson@yahoo.com', 'Ywi9L8DhUnvU1npA'),
       ('P-40ac19fce2',
        'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJQLTQwYWMxOWZjZTIiLCJpYXQiOjE2OTE0NTE4NTUsImV4cCI6MTY5MTQ1OTA1NX0.VPuV-dI8Zr-UbmBkB2zdL2FmzaCgFL_QjS3IdZqAS2s9HHOnk_bbQZ15IVxKKoH6FZGZJ9vDPXAgKGwg-msNxw',
        'franklyn.hilll@yahoo.com', '4TO17i9UgVfFhqxQ');

-- Account

INSERT INTO account (account_IBAN, user_id, currency, balance)
VALUES ('IBAN-4e90eee2-37c8-4687-9951', 1, 'PAB', 470968.00),
       ('IBAN-6f6bfcec-67f9-44bb-9c20', 1, 'COP', 124139.00),
       ('IBAN-705b3e6c-c816-4ae8-a03d', 2, 'GYD', 585526.00),
       ('IBAN-7185b146-2165-4007-a8bb', 2, 'GEL', 29038.00),
       ('IBAN-40d98de0-0ea9-41bc-b407', 2, 'HNL', 874804.00);


