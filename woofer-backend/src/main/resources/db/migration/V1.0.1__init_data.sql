-- ----------------------------------------------------------
-- User
-- ----------------------------------------------------------
INSERT INTO users (id, fullname, email) VALUES ('admin', 'Woofer Admin', 'woofer.admin@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('bpitt', 'Brad Pitt', 'brad.pitt@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('wsmith', 'Will Smith', 'will.smith@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('jdepp', 'Johnny Depp', 'johnny.depp@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('ldicaprio', 'Leonardo DiCaprio', 'leonardo.dicaprio@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('thanks', 'Tom Hanks', 'tom.hanks@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('tcruise', 'Tom Cruise', 'tom.cruise@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('hford', 'Harrison Ford', 'harrison.ford@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('gclooney', 'George Clooney', 'george.clooney@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('baffleck', 'Ben Affleck', 'ben.affleck@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('jnicholson', 'Jack Nicholson', 'jack.nicholson@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('rdeniro', 'Robert De Niro', 'robert.deniro@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('apacino', 'Al Pacino', 'al.pacino@gmail.com');
INSERT INTO users (id, fullname, email) VALUES ('dhoffman', 'Dustin Hoffman', 'dustin.hoffman@gmail.com');

-- ----------------------------------------------------------
-- user_followers
-- ----------------------------------------------------------
INSERT INTO user_followers (user_id, follower_id) VALUES ('bpitt', 'wsmith');
INSERT INTO user_followers (user_id, follower_id) VALUES ('bpitt', 'jdepp');
INSERT INTO user_followers (user_id, follower_id) VALUES ('bpitt', 'ldicaprio');

INSERT INTO user_followers (user_id, follower_id) VALUES ('wsmith', 'jdepp');
INSERT INTO user_followers (user_id, follower_id) VALUES ('wsmith', 'ldicaprio');
INSERT INTO user_followers (user_id, follower_id) VALUES ('wsmith', 'thanks');

INSERT INTO user_followers (user_id, follower_id) VALUES ('jdepp', 'ldicaprio');
INSERT INTO user_followers (user_id, follower_id) VALUES ('jdepp', 'thanks');
INSERT INTO user_followers (user_id, follower_id) VALUES ('jdepp', 'tcruise');

INSERT INTO user_followers (user_id, follower_id) VALUES ('ldicaprio', 'thanks');
INSERT INTO user_followers (user_id, follower_id) VALUES ('ldicaprio', 'tcruise');
INSERT INTO user_followers (user_id, follower_id) VALUES ('ldicaprio', 'hford');

INSERT INTO user_followers (user_id, follower_id) VALUES ('thanks', 'tcruise');
INSERT INTO user_followers (user_id, follower_id) VALUES ('thanks', 'hford');
INSERT INTO user_followers (user_id, follower_id) VALUES ('thanks', 'gclooney');

INSERT INTO user_followers (user_id, follower_id) VALUES ('tcruise', 'hford');
INSERT INTO user_followers (user_id, follower_id) VALUES ('tcruise', 'gclooney');
INSERT INTO user_followers (user_id, follower_id) VALUES ('tcruise', 'baffleck');

INSERT INTO user_followers (user_id, follower_id) VALUES ('hford', 'gclooney');
INSERT INTO user_followers (user_id, follower_id) VALUES ('hford', 'baffleck');
INSERT INTO user_followers (user_id, follower_id) VALUES ('hford', 'jnicholson');

INSERT INTO user_followers (user_id, follower_id) VALUES ('gclooney', 'baffleck');
INSERT INTO user_followers (user_id, follower_id) VALUES ('gclooney', 'jnicholson');
INSERT INTO user_followers (user_id, follower_id) VALUES ('gclooney', 'rdeniro');

INSERT INTO user_followers (user_id, follower_id) VALUES ('baffleck', 'jnicholson');
INSERT INTO user_followers (user_id, follower_id) VALUES ('baffleck', 'rdeniro');
INSERT INTO user_followers (user_id, follower_id) VALUES ('baffleck', 'apacino');

INSERT INTO user_followers (user_id, follower_id) VALUES ('jnicholson', 'rdeniro');
INSERT INTO user_followers (user_id, follower_id) VALUES ('jnicholson', 'apacino');
INSERT INTO user_followers (user_id, follower_id) VALUES ('jnicholson', 'dhoffman');

INSERT INTO user_followers (user_id, follower_id) VALUES ('rdeniro', 'apacino');
INSERT INTO user_followers (user_id, follower_id) VALUES ('rdeniro', 'dhoffman');
INSERT INTO user_followers (user_id, follower_id) VALUES ('rdeniro', 'bpitt');

INSERT INTO user_followers (user_id, follower_id) VALUES ('apacino', 'dhoffman');
INSERT INTO user_followers (user_id, follower_id) VALUES ('apacino', 'bpitt');
INSERT INTO user_followers (user_id, follower_id) VALUES ('apacino', 'wsmith');

INSERT INTO user_followers (user_id, follower_id) VALUES ('dhoffman', 'bpitt');
INSERT INTO user_followers (user_id, follower_id) VALUES ('dhoffman', 'wsmith');
INSERT INTO user_followers (user_id, follower_id) VALUES ('dhoffman', 'jdepp');

-- ----------------------------------------------------------
-- woof
-- ----------------------------------------------------------
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (1, 'bpitt', '2016-01-01 00:00:00', 'Hi, this is Brad !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (2, 'wsmith', '2016-01-02 00:00:00', 'Hi, this is Will !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (3, 'jdepp', '2016-01-03 00:00:00', 'Hi, this is Johnny !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (4, 'ldicaprio', '2016-01-04 00:00:00', 'Hi, this is Leonardo !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (5, 'thanks', '2016-01-05 00:00:00', 'Hi, this is Tom !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (6, 'tcruise', '2016-01-06 00:00:00', 'Hi, this is Tom !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (7, 'hford', '2016-01-07 00:00:00', 'Hi, this is Harrison !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (8, 'gclooney', '2016-01-08 00:00:00', 'Hi, this is George !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (9, 'baffleck', '2016-01-09 00:00:00', 'Hi, this is Ben !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (10, 'jnicholson', '2016-01-10 00:00:00', 'Hi, this is Jack !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (11, 'rdeniro', '2016-01-11 00:00:00', 'Hi, this is Robert !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (12, 'apacino', '2016-01-12 00:00:00', 'Hi, this is Al !');
INSERT INTO woofs (id, author_id, `datetime`, message) VALUES (13, 'dhoffman', '2016-01-13 00:00:00', 'Hi, this is Dustin !');
