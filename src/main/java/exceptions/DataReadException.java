package exceptions;

public class DataReadException extends Exception{
    public DataReadException(){
        super("Failed to read from database");
    }
}
