package dao;

import exceptions.DataReadException;
import exceptions.DatabaseConnectionException;
import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    Optional<T> get(Integer id);
    List<T> getAll() throws DatabaseConnectionException, DataReadException;
    void save(T item);
    void update(T item);
}
