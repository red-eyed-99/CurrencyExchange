package dao;

import exceptions.QueryExecuteException;
import exceptions.DatabaseConnectionException;
import exceptions.NotFoundException;

import java.util.List;

public interface DAO<T> {
    T get(String code) throws DatabaseConnectionException, QueryExecuteException, NotFoundException;

    List<T> getAll() throws DatabaseConnectionException, QueryExecuteException, NotFoundException;

    void save(T item) throws DatabaseConnectionException, QueryExecuteException;

    void update(T item);
}
