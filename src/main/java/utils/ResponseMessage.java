package utils;

public class ResponseMessage {
    private final int statusCode;
    private final String message;

    public ResponseMessage(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
