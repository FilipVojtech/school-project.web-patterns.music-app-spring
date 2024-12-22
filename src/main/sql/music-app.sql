CREATE DATABASE IF NOT EXISTS music_app;

USE music_app;

CREATE TABLE users (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          email VARCHAR(255) NOT NULL,
                          password VARCHAR(60) NOT NULL,
                          display_name VARCHAR(60)
);

CREATE TABLE artist (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
);

CREATE TABLE album (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       artist_id INT,
                       title VARCHAR(255) NOT NULL,
                       release_date DATE,
                       FOREIGN KEY (artist_id) REFERENCES artist(id)
);

CREATE TABLE song (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      artist_id INT,
                      title VARCHAR(255) NOT NULL,
                      rating INT CHECK (rating >= 1 AND rating <= 5),  -- Rating must be between 1 and 5
                      FOREIGN KEY (artist_id) REFERENCES artist(id)
);

CREATE TABLE song_ratings (
                              rating_id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT NOT NULL,
                              song_id INT NOT NULL,
                              rating_value INT NOT NULL CHECK (rating_value >= 1 AND rating_value <= 5),
                              UNIQUE KEY unique_user_song (user_id, song_id),
                              FOREIGN KEY (user_id) REFERENCES users(id),
                              FOREIGN KEY (song_id) REFERENCES song(id)
);

CREATE TABLE album_songs (
                             album_id INT,
                             song_id INT,
                             PRIMARY KEY (album_id, song_id),
                             FOREIGN KEY (album_id) REFERENCES album(id),
                             FOREIGN KEY (song_id) REFERENCES song(id)
);

CREATE TABLE playlist (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(60) NOT NULL,
                          owner_id INT,
                          visibility TINYINT(1),  -- TINYINT(1) for boolean values (0 = false, 1 = true)
                          rating INT CHECK (rating >= 1 AND rating <= 5),  -- Rating must be between 1 and 5
                          FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE playlist_songs (
                                playlist_id INT,
                                song_id INT,
                                PRIMARY KEY (playlist_id, song_id),
                                FOREIGN KEY (playlist_id) REFERENCES playlist(id),
                                FOREIGN KEY (song_id) REFERENCES song(id)
);

CREATE TABLE user_likes (
                            user_id INT,
                            song_id INT,
                            PRIMARY KEY (user_id, song_id),
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (song_id) REFERENCES song(id)
);

