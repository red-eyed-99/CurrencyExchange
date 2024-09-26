package exceptions;

public class QueryExecuteException extends Exception{
    public QueryExecuteException(){
        super("Failed to execute database query");
    }
}
