-- Фильмы
CREATE TABLE IF NOT EXISTS Film
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    release_date DATE         NOT NULL,
    duration     INT CHECK (duration > 0),
    mpa_rating   VARCHAR(10) CHECK (mpa_rating IN ('G', 'PG', 'PG-13', 'R', 'NC-17'))
);

-- Жанры
CREATE TABLE IF NOT EXISTS Genre
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE -- Уникальный жанр
);

-- Связь фильмы-жанры (многие-ко-многим)
CREATE TABLE IF NOT EXISTS Film_genre
(
    film_id  INT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES Film (id),
    FOREIGN KEY (genre_id) REFERENCES Genre (id)
);

-- Пользователи
CREATE TABLE IF NOT EXISTS Users
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    login    VARCHAR(50)  NOT NULL UNIQUE,
    email    VARCHAR(100) NOT NULL UNIQUE,
    name     VARCHAR(100),
    birthday DATE
);

-- Лайки фильмов
CREATE TABLE IF NOT EXISTS Film_likes
(
    film_id INT,
    user_id INT,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES Film (id),
    FOREIGN KEY (user_id) REFERENCES Users (id)
);

-- Статусы дружбы
CREATE TABLE IF NOT EXISTS User_friendship
(
    user_id   INT,
    friend_id INT,
    status    VARCHAR(10) CHECK (status IN ('pending', 'confirmed')),
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES Users (id),
    FOREIGN KEY (friend_id) REFERENCES Users (id),
    CHECK (user_id != friend_id) -- Запрет добавления себя в друзья
);