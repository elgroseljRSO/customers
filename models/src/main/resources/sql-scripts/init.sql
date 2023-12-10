INSERT INTO service_types (name, durationHours, cost) VALUES ('Masaza', 1, 100);
INSERT INTO employees (name) VALUES ('Janez');
# INSERT INTO appointments (customer, start, employee_id) VALUES ("stranka@gmail.com", 8, 1);
INSERT INTO appointments (start, employee_id, service_type_id, customer) VALUES (8, 1, 1, 'stranka1@gmail.com');
# INSERT INTO appointments (start, employee_id, customer) VALUES (8, 1, "aba");
INSERT INTO employees_service_types (service_type_id, employee_id) VALUES (1,1);
INSERT INTO customers (email, money) VALUES ('stranka1@gmail.com', 100);


