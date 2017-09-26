CREATE TABLE authorities (
  id BIGINT NOT NULL AUTO_INCREMENT,
  authority VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  enabled BOOLEAN,
  `password` VARCHAR(255),
  `status` INTEGER,
  username VARCHAR(255),
  PRIMARY KEY (id),
  CONSTRAINT UK_username UNIQUE (username)
);

CREATE TABLE user_authorities (
  user_id BIGINT NOT NULL,
  authority_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, authority_id),
  CONSTRAINT FK_user_authorities_2_authority_id FOREIGN KEY (authority_id) REFERENCES authorities (id),
  CONSTRAINT FK_user_authorities_2_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);
