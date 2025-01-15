DROP TABLE t_tasks IF EXISTS;
DROP TABLE t_users IF EXISTS;


CREATE TABLE t_users (
    id BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled INTEGER NOT NULL 
);

CREATE TABLE t_tasks (
    id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    user_id BIGINT NOT NULL
);


-- Create authorities table for role-based security
CREATE TABLE t_authorities (
    id BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL
);

ALTER TABLE t_users 
ALTER COLUMN enabled SET DEFAULT 1;

ALTER TABLE t_tasks ADD CONSTRAINT fk_tasks FOREIGN KEY (user_id) REFERENCES t_users(id) ON DELETE CASCADE;
ALTER TABLE t_authorities ADD CONSTRAINT fk_authorities FOREIGN KEY (user_id) REFERENCES t_users(id) ON DELETE CASCADE;

-- Create index on username and authority
CREATE UNIQUE INDEX ix_auth_username ON t_authorities (username, authority);