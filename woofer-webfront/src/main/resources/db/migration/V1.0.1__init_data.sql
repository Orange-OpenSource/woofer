-- ----------------------------------------------------------
-- Authority
-- ----------------------------------------------------------

INSERT INTO authorities (id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO authorities (id, authority) VALUES (2, 'ROLE_USER');

-- ----------------------------------------------------------
-- User
-- ----------------------------------------------------------
-- `password` 'admin'
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (1, 'admin', '$2a$10$jbqsQ21a4nSqgJjmfBblJ.e1PdfWhuj/Um557OY./alTnZoJROQyW', true, 1);
-- `password` 'user'
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (2, 'bpitt', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (3, 'wsmith', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (4, 'jdepp', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (5, 'ldicaprio', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (6, 'thanks', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (7, 'tcruise', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (8, 'hford', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (9, 'gclooney', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (10, 'baffleck', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (11, 'jnicholson', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (12, 'rdeniro', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (13, 'apacino', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);
INSERT INTO users (id, username, `password`, enabled, `status`) VALUES (14, 'dhoffman', '$2a$10$j794jRcT4H0TNu8pO4ak1.mfZvQ50GGxgQFSkxCc/qnh3AuywoRf6', true, 1);

-- ----------------------------------------------------------
-- user_authorities
-- ----------------------------------------------------------

INSERT INTO user_authorities (user_id, authority_id) VALUES (1, 1);
INSERT INTO user_authorities (user_id, authority_id) VALUES (2, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (3, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (4, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (5, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (6, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (7, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (8, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (9, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (10, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (11, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (12, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (13, 2);
INSERT INTO user_authorities (user_id, authority_id) VALUES (14, 2);

