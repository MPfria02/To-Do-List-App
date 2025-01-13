INSERT INTO t_users (username, email, password) VALUES
('Alice', 'alice@example.com', 'password123'),
('Bob', 'bob@example.com', 'securepass'),
('Charlie', 'charlie@example.com', 'mypassword');

INSERT INTO t_tasks (id, title, description, user_id) VALUES
(1,'Buy groceries', 'Milk, eggs, bread', 1),
(1,'Finish project', 'Complete the final draft by Friday', 2),
(2,'Book tickets', 'Vacation tickets to Hawaii', 1),
(1,'Pay bills', 'Electricity and water bills', 3);
