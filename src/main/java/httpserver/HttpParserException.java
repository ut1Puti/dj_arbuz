package httpserver;

public class HttpParserException extends Exception {
    public HttpParserException(String message){
        super(message);
    }

    public HttpParserException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public HttpParserException(Throwable err) {
        super(err);
    }
}
