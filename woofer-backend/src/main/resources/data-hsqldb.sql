-- ----------------------------------------------------------
-- User
-- ----------------------------------------------------------
insert into users (id, fullname, email) values ('bpitt', 'Brad Pitt', 'brad.pitt@gmail.com');
insert into users (id, fullname, email) values ('wsmith', 'Will Smith', 'will.smith@gmail.com');
insert into users (id, fullname, email) values ('jdepp', 'Johnny Depp', 'johnny.depp@gmail.com');
insert into users (id, fullname, email) values ('ldicaprio', 'Leonardo DiCaprio', 'leonardo.dicaprio@gmail.com');
insert into users (id, fullname, email) values ('thanks', 'Tom Hanks', 'tom.hanks@gmail.com');
insert into users (id, fullname, email) values ('tcruise', 'Tom Cruise', 'tom.cruise@gmail.com');
insert into users (id, fullname, email) values ('hford', 'Harrison Ford', 'harrison.ford@gmail.com');
insert into users (id, fullname, email) values ('gclooney', 'George Clooney', 'george.clooney@gmail.com');
insert into users (id, fullname, email) values ('baffleck', 'Ben Affleck', 'ben.affleck@gmail.com');
insert into users (id, fullname, email) values ('jnicholson', 'Jack Nicholson', 'jack.nicholson@gmail.com');
insert into users (id, fullname, email) values ('rdeniro', 'Robert De Niro', 'robert.deniro@gmail.com');
insert into users (id, fullname, email) values ('apacino', 'Al Pacino', 'al.pacino@gmail.com');
insert into users (id, fullname, email) values ('dhoffman', 'Dustin Hoffman', 'dustin.hoffman@gmail.com');

-- ----------------------------------------------------------
-- user_followers
-- ----------------------------------------------------------
insert into user_followers (user_id, follower_id) values ('bpitt', 'wsmith');
insert into user_followers (user_id, follower_id) values ('bpitt', 'jdepp');
insert into user_followers (user_id, follower_id) values ('bpitt', 'ldicaprio');

insert into user_followers (user_id, follower_id) values ('wsmith', 'jdepp');
insert into user_followers (user_id, follower_id) values ('wsmith', 'ldicaprio');
insert into user_followers (user_id, follower_id) values ('wsmith', 'thanks');

insert into user_followers (user_id, follower_id) values ('jdepp', 'ldicaprio');
insert into user_followers (user_id, follower_id) values ('jdepp', 'thanks');
insert into user_followers (user_id, follower_id) values ('jdepp', 'tcruise');

insert into user_followers (user_id, follower_id) values ('ldicaprio', 'thanks');
insert into user_followers (user_id, follower_id) values ('ldicaprio', 'tcruise');
insert into user_followers (user_id, follower_id) values ('ldicaprio', 'hford');

insert into user_followers (user_id, follower_id) values ('thanks', 'tcruise');
insert into user_followers (user_id, follower_id) values ('thanks', 'hford');
insert into user_followers (user_id, follower_id) values ('thanks', 'gclooney');

insert into user_followers (user_id, follower_id) values ('tcruise', 'hford');
insert into user_followers (user_id, follower_id) values ('tcruise', 'gclooney');
insert into user_followers (user_id, follower_id) values ('tcruise', 'baffleck');

insert into user_followers (user_id, follower_id) values ('hford', 'gclooney');
insert into user_followers (user_id, follower_id) values ('hford', 'baffleck');
insert into user_followers (user_id, follower_id) values ('hford', 'jnicholson');

insert into user_followers (user_id, follower_id) values ('gclooney', 'baffleck');
insert into user_followers (user_id, follower_id) values ('gclooney', 'jnicholson');
insert into user_followers (user_id, follower_id) values ('gclooney', 'rdeniro');

insert into user_followers (user_id, follower_id) values ('baffleck', 'jnicholson');
insert into user_followers (user_id, follower_id) values ('baffleck', 'rdeniro');
insert into user_followers (user_id, follower_id) values ('baffleck', 'apacino');

insert into user_followers (user_id, follower_id) values ('jnicholson', 'rdeniro');
insert into user_followers (user_id, follower_id) values ('jnicholson', 'apacino');
insert into user_followers (user_id, follower_id) values ('jnicholson', 'dhoffman');

insert into user_followers (user_id, follower_id) values ('rdeniro', 'apacino');
insert into user_followers (user_id, follower_id) values ('rdeniro', 'dhoffman');
insert into user_followers (user_id, follower_id) values ('rdeniro', 'bpitt');

insert into user_followers (user_id, follower_id) values ('apacino', 'dhoffman');
insert into user_followers (user_id, follower_id) values ('apacino', 'bpitt');
insert into user_followers (user_id, follower_id) values ('apacino', 'wsmith');

insert into user_followers (user_id, follower_id) values ('dhoffman', 'bpitt');
insert into user_followers (user_id, follower_id) values ('dhoffman', 'wsmith');
insert into user_followers (user_id, follower_id) values ('dhoffman', 'jdepp');

-- ----------------------------------------------------------
-- woof
-- ----------------------------------------------------------
insert into woof (id, author_id, datetime, message) values (1, 'bpitt', '2016-01-01 00:00:00', 'Hi, this is Brad !');
insert into woof (id, author_id, datetime, message) values (2, 'wsmith', '2016-01-02 00:00:00', 'Hi, this is Will !');
insert into woof (id, author_id, datetime, message) values (3, 'jdepp', '2016-01-03 00:00:00', 'Hi, this is Johnny !');
insert into woof (id, author_id, datetime, message) values (4, 'ldicaprio', '2016-01-04 00:00:00', 'Hi, this is Leonardo !');
insert into woof (id, author_id, datetime, message) values (5, 'thanks', '2016-01-05 00:00:00', 'Hi, this is Tom !');
insert into woof (id, author_id, datetime, message) values (6, 'tcruise', '2016-01-06 00:00:00', 'Hi, this is Tom !');
insert into woof (id, author_id, datetime, message) values (7, 'hford', '2016-01-07 00:00:00', 'Hi, this is Harrison !');
insert into woof (id, author_id, datetime, message) values (8, 'gclooney', '2016-01-08 00:00:00', 'Hi, this is George !');
insert into woof (id, author_id, datetime, message) values (9, 'baffleck', '2016-01-09 00:00:00', 'Hi, this is Ben !');
insert into woof (id, author_id, datetime, message) values (10, 'jnicholson', '2016-01-10 00:00:00', 'Hi, this is Jack !');
insert into woof (id, author_id, datetime, message) values (11, 'rdeniro', '2016-01-11 00:00:00', 'Hi, this is Robert !');
insert into woof (id, author_id, datetime, message) values (12, 'apacino', '2016-01-12 00:00:00', 'Hi, this is Al !');
insert into woof (id, author_id, datetime, message) values (13, 'dhoffman', '2016-01-13 00:00:00', 'Hi, this is Dustin !');
