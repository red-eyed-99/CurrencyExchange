package exceptions;

public class DatabaseConnectionException extends Exception{
    public DatabaseConnectionException(){
        super("No connection to database");
    }
}
