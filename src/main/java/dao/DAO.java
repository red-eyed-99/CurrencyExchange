package dao;

import exceptions.QueryExecuteException;
import exceptions.DatabaseConnectionException;
import exceptions.NotFoundException;

import java.util.List;

public interface DAO<T, K> {
    T get(K key) throws DatabaseConnectionException, QueryExecuteException, NotFoundException;

    List<T> getAll() throws DatabaseConnectionException, QueryExecuteException, NotFoundException;

    void save(T item) throws DatabaseConnectionException, QueryExecuteException;

    void update(T item);
}
