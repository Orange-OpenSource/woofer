CREATE TABLE users (
  id VARCHAR(255) NOT NULL,
  fullname VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE user_followers (
  user_id VARCHAR(255) NOT NULL,
  follower_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (user_id, follower_id)
);

CREATE TABLE woofs (
  id BIGINT NOT NULL AUTO_INCREMENT,
  author_id VARCHAR(255) NOT NULL,
  `datetime` DATETIME NOT NULL,
  message VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT FK_author_2_user_id FOREIGN KEY (author_id) REFERENCES users (id)
);
