package utils;

public class ErrorMessage {
    private final int status;
    private final String message;

    public ErrorMessage(int statusCode, String message) {
        this.status = statusCode;
        this.message = message;
    }
}
