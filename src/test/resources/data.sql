INSERT INTO job (id, name) VALUES
  (1, 'Director'),
  (2, 'Actor');

INSERT INTO genres (id, name) VALUES
  (28, 'Action'), 
  (12, 'Adventure'), 
  (16, 'Animation'), 
  (35, 'Comedy'),
  (80, 'Crime'), 
  (99, 'Documentary'), 
  (18, 'Drama'), 
  (10751, 'Family'),
  (14, 'Fantasy'), 
  (36, 'History'), 
  (27, 'Horror'), 
  (10402, 'Music'),
  (9648, 'Mystery'), 
  (10749, 'Romance'), 
  (878, 'Science Fiction'),
  (10770, 'TV Movie'), 
  (53, 'Thriller'), 
  (10752, 'War'), 
  (37, 'Western');

INSERT INTO countries (code, continent_code, name, iso3, number, full_name) VALUES
  ('US', 'NA', 'United States of America', 'USA', '840', 'United States of America'),
  ('JP', 'AS', 'Japan', 'JPN', '392', 'Japan'),
  ('MX', 'NA', 'Mexico', 'MEX', '484', 'United Mexican States');

INSERT INTO users (id, username, email, password) VALUES
  (1, 'testuser', 'test@kino.com', '$2a$10$6BHGU.R2jxjG4sb.FRelUuEb9jw7MEe/UMlvyMsuuW9JB8tU3dnJC'),
  (2, 'otheruser', 'other@kino.com', '$2a$10$6BHGU.R2jxjG4sb.FRelUuEb9jw7MEe/UMlvyMsuuW9JB8tU3dnJC');

INSERT INTO film (id, title, release_year, length_in_minutes, poster_path) VALUES
  (129, 'Spirited Away', 2001, 125, '/spirited_away.jpg'),
  (550, 'Fight Club', 1999, 139, '/fight_club.jpg');

INSERT INTO movie_list (id, name, is_watchlist, user_id) VALUES (1, 'Watchlist', true, 1);

INSERT INTO log (id, date, review, rating, film_id, user_id, first_watch) VALUES
  (1, '2025-03-10', 'Una obra maestra del Studio Ghibli', 4.5, 129, 1, true),
  (2, '2025-04-20', 'Clásico atemporal', 5.0, 550, 1, true);

ALTER TABLE users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE log ALTER COLUMN id RESTART WITH 100;
ALTER TABLE movie_list ALTER COLUMN id RESTART WITH 100;