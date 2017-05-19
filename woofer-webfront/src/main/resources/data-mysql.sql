-- ----------------------------------------------------------
-- Authority
-- ----------------------------------------------------------

insert ignore into authorities (id, authority) values (1, 'ROLE_ADMIN');
insert ignore into authorities (id, authority) values (2, 'ROLE_USER');

-- ----------------------------------------------------------
-- User
-- ----------------------------------------------------------
-- password 'admin'
insert ignore into users (id, username, password, enabled, status) values (1, 'admin', '$2a$10$jbqsQ21a4nSqgJjmfBblJ.e1PdfWhuj/Um557OY./alTnZoJROQyW', true, 1);
-- password 'user'
insert ignore into users (id, username, password, enabled, status) values (2, 'bpitt', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (3, 'wsmith', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (4, 'jdepp', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (5, 'ldicaprio', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (6, 'thanks', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (7, 'tcruise', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (8, 'hford', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (9, 'gclooney', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (10, 'baffleck', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (11, 'jnicholson', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (12, 'rdeniro', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (13, 'apacino', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
insert ignore into users (id, username, password, enabled, status) values (14, 'dhoffman', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);

-- ----------------------------------------------------------
-- user_authorities
-- ----------------------------------------------------------

insert ignore into user_authorities (user_id, authority_id) values (1, 1);
insert ignore into user_authorities (user_id, authority_id) values (2, 2);
insert ignore into user_authorities (user_id, authority_id) values (3, 2);
insert ignore into user_authorities (user_id, authority_id) values (4, 2);
insert ignore into user_authorities (user_id, authority_id) values (5, 2);
insert ignore into user_authorities (user_id, authority_id) values (6, 2);
insert ignore into user_authorities (user_id, authority_id) values (7, 2);
insert ignore into user_authorities (user_id, authority_id) values (8, 2);
insert ignore into user_authorities (user_id, authority_id) values (9, 2);
insert ignore into user_authorities (user_id, authority_id) values (10, 2);
insert ignore into user_authorities (user_id, authority_id) values (11, 2);
insert ignore into user_authorities (user_id, authority_id) values (12, 2);
insert ignore into user_authorities (user_id, authority_id) values (13, 2);
insert ignore into user_authorities (user_id, authority_id) values (14, 2);

