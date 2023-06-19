package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate CINEMA_STARTING_POINT = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int globalId = 0;

    @Override
    public List<Film> getAll() {
        log.info("Запрошен список всех фильмов");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        validateReleaseDate(film);
        int id = ++globalId;
        film.setId(id);
        films.put(id, film);
        log.info("Фильм: '{}' создан", film);
        return film;
    }

    @Override
    public Film update(Film updatedFilm) {
        validateReleaseDate(updatedFilm);
        int id = updatedFilm.getId();
        if (films.containsKey(id)) {
            films.put(id, updatedFilm);
            log.info("Фильм с ID: '{}' обновлен", id);
            return updatedFilm;
        } else {
            throw new NotFoundException("Фильм с ID: '" + id + "' не найден");
        }
    }

    @Override
    public Film getById(int id) {
        log.info("Запрошен фильм с ID: '{}'", id);
        return films.get(id);
    }

    @Override
    public Film addLike(int id, int userId) {
        Film film = films.get(id);
        int likesCount = film.getLikesCount();
        likesCount += 1;
        film.setLikesCount(likesCount);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.getLikes().add(userId);
        log.info("Пользователь с ID: '{}' поставил лайк фильму с ID: '{}'", userId, id);
        return film;
    }

    @Override
    public Film removeLike(int id, int userId) {
        Film film = films.get(id);
        int likesCount = film.getLikesCount();
        likesCount -= 1;
        film.setLikesCount(likesCount);
        film.getLikes().remove(userId);
        log.info("Пользователь с ID: '{}' удалил лайк с фильма с ID: '{}'", userId, id);
        return film;
    }

    @Override
    public List<Film> getPopular(int count) {
        log.info("Запрошен список {} самых популярных фильмов", count);
        return films.values().stream()
                .sorted((film1, film2) -> film2.getLikesCount() - film1.getLikesCount())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isExists(int id) {
        return films.containsKey(id);
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза должна быть передана в запросе");
        }
        if (film.getReleaseDate().isBefore(CINEMA_STARTING_POINT)) {
            throw new ValidationException("Дата релиза должна быть позже 1895-12-28");
        }
    }
}