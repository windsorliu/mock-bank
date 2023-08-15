CREATE
DATABASE mockbank;

-- User

CREATE TABLE user
(
    user_id            INT AUTO_INCREMENT PRIMARY KEY,
    user_key           VARCHAR(20)                         NOT NULL UNIQUE KEY,
    token              VARCHAR(300)                        NOT NULL,
    email              VARCHAR(50)                         NOT NULL UNIQUE KEY,
    password           VARCHAR(100)                        NOT NULL,
    role               VARCHAR(20)                         NOT NULL,
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
    transaction_id        INT AUTO_INCREMENT PRIMARY KEY,
    transaction_key       VARCHAR(50)                         NOT NULL UNIQUE KEY,
    remitter_account_IBAN VARCHAR(50)                         NOT NULL,
    payee_account_IBAN    VARCHAR(50)                         NOT NULL,
    amount                DECIMAL(12, 2)                      NOT NULL,
    currency              VARCHAR(5)                          NOT NULL,
    created_date          TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
    last_modified_date    TIMESTAMP default CURRENT_TIMESTAMP NOT NULL on update CURRENT_TIMESTAMP,
    description           VARCHAR(100),
    INDEX                 idx_remitter_account_IBAN (remitter_account_IBAN),
    INDEX                 idx_payee_account_IBAN (payee_account_IBAN)
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
    conversion_rates      JSON         NOT NULL,
    INDEX                 idx_time_last_update_unix (time_last_update_unix)
);