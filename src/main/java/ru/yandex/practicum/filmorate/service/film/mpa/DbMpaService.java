package ru.yandex.practicum.filmorate.service.film.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNameValidateException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DbMpaService implements MpaService {
    private static final List<String> ALLOWED_NAMES = List.of("G", "PG", "PG-13", "R", "NC-17");

    private final MpaRepository mpaRepository;

    @Override
    public Mpa createMpa(Mpa mpa) {
        validateMpa(mpa);
        return this.mpaRepository.save(mpa);
    }

    @Override
    public Mpa updateMpa(Mpa mpa) {
        validateMpa(mpa);
        return this.mpaRepository.update(mpa);
    }

    @Override
    public List<Mpa> getAllMpa() {
        return this.mpaRepository.findAll();
    }

    @Override
    public Mpa getMpa(long id) {
        return this.mpaRepository.findById(id);
    }

    private void validateMpa(Mpa mpa) {
        if (!ALLOWED_NAMES.contains(mpa.getName())) {
            throw new MpaNameValidateException("Недопустимое имя для рейтинга");
        }
    }
}
