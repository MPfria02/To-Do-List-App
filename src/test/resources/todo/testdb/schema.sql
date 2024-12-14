DROP TABLE t_users IF EXISTS;
DROP TABLE t_tasks IF EXISTS;

CREATE TABLE t_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE t_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    user_id BIGINT NOT NULL,
);

ALTER TABLE t_tasks ADD CONSTRAINT fk_tasks FOREIGN KEY (user_id) REFERENCES t_users(id) ON DELETE CASCADE;