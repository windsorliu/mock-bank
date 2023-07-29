CREATE
DATABASE mockbank;

-- User

CREATE TABLE user
(
    user_id            INT AUTO_INCREMENT PRIMARY KEY,
    user_key           VARCHAR(20)                         NOT NULL UNIQUE KEY,
    email              VARCHAR(50)                         NOT NULL UNIQUE KEY,
    password           VARCHAR(50)                         NOT NULL,
    created_date       TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP default CURRENT_TIMESTAMP NOT NULL on update CURRENT_TIMESTAMP
);

-- Account

CREATE TABLE account
(
    account_id         INT AUTO_INCREMENT PRIMARY KEY,
    account_IBAN       VARCHAR(50)                         NOT NULL UNIQUE KEY,
    user_id            INT                                 NOT NULL,
    currency           VARCHAR(5)                          NOT NULL,
    balance            DECIMAL(12, 2)                      NOT NULL,
    created_date       TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP default CURRENT_TIMESTAMP NOT NULL on update CURRENT_TIMESTAMP,
    INDEX              idx_user_id (user_id)
);

-- Transaction

CREATE TABLE transaction
(
    transaction_id   INT AUTO_INCREMENT PRIMARY KEY,
    transaction_key  VARCHAR(50)                         NOT NULL UNIQUE KEY,
    user_id          INT                                 NOT NULL,
    account_id       INT                                 NOT NULL,
    amount_value     DECIMAL(12, 2)                      NOT NULL,
    amount_currency  VARCHAR(5)                          NOT NULL,
    transaction_date TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
    description      VARCHAR(100),
    INDEX            idx_user_id (user_id),
    INDEX            idx_account_id (account_id)
);

-- Exchange_Rate

CREATE TABLE exchange_rate
(
    id                    INT AUTO_INCREMENT PRIMARY KEY,
    result                VARCHAR(20)  NOT NULL,
    documentation         VARCHAR(100) NOT NULL,
    terms_of_use          VARCHAR(100) NOT NULL,
    time_last_update_unix BIGINT       NOT NULL,
    time_last_update_utc  VARCHAR(100) NOT NULL,
    time_next_update_unix BIGINT       NOT NULL,
    time_next_update_utc  VARCHAR(100) NOT NULL,
    base_code             VARCHAR(5)   NOT NULL,
    conversion_rates      JSON         NOT NULL
);