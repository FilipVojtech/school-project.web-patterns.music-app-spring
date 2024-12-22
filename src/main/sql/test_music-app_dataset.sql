USE music_app;

INSERT INTO artist (name) VALUES
                              ('The Beatles'),
                              ('Led Zeppelin'),
                              ('Pink Floyd'),
                              ('Queen'),
                              ('The Rolling Stones');

INSERT INTO album (artist_id, title, release_date) VALUES
                                                       (1, 'Abbey Road', '1969-09-26'),
                                                       (2, 'Led Zeppelin IV', '1971-11-08'),
                                                       (3, 'The Dark Side of the Moon', '1973-03-01'),
                                                       (4, 'A Night at the Opera', '1975-11-21'),
                                                       (5, 'Let It Bleed', '1969-12-05');

INSERT INTO song (artist_id, title, rating) VALUES
                                                (1, 'Come Together', 5),
                                                (1, 'Something', 4),
                                                (2, 'Stairway to Heaven', 5),
                                                (3, 'Time', 5),
                                                (3, 'Money', 4),
                                                (4, 'Bohemian Rhapsody', 5),
                                                (5, 'Gimme Shelter', 5);

INSERT INTO users (email, password, display_name) VALUES
                                                         # All passwords are "Password"
                                                         ('user1@example.com', '$2a$14$Sci/QKCO4xEAqh2O3/PSk.XGWdEF.Jxy/AB0cDCkGWu19Gz1d3gPq', 'User One'),
                                                         ('user2@example.com', '$2a$14$IUvGQl20ZUAZ78zB./nYVODp9yrq9UtMkCvkx7kh0kBCpU1bg9vo2', 'User Two'),
                                                         ('user3@example.com', '$2a$14$BYxz1MGCgDNqzkg7wRJtveJHTdAeX1FzBaXVx/YAK8NAzKkNy8Xf2', 'User Three');

INSERT INTO song_ratings (user_id, song_id, rating_value) VALUES
                                                              (1, 1, 5),
                                                              (1, 2, 4),
                                                              (2, 3, 5),
                                                              (2, 4, 4),
                                                              (3, 5, 3);

INSERT INTO album_songs (album_id, song_id) VALUES
                                                (1, 1),
                                                (1, 2),
                                                (2, 3),
                                                (3, 4),
                                                (3, 5),
                                                (4, 6),
                                                (5, 7);

INSERT INTO playlist (name, owner_id, visibility, rating) VALUES
                                                        ('Playlist 1',1, 1, 5),
                                                        ('Playlist 2',2, 0, 4),
                                                        ('Playlist 3',3, 1, 3);

INSERT INTO playlist_songs (playlist_id, song_id) VALUES
                                                      (1, 1),
                                                      (1, 3),
                                                      (2, 4),
                                                      (2, 5),
                                                      (3, 6),
                                                      (3, 7);

INSERT INTO user_likes (user_id, song_id) VALUES
                                              (1, 3),
                                              (1, 4),
                                              (2, 6),
                                              (3, 7);
