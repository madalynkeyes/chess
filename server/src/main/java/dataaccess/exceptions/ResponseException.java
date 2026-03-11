package dataaccess.exceptions;

//import com.google.gson.Gson;
//
//import java.util.HashMap;
//import java.util.Map;

public class ResponseException extends Exception {

    //    public enum Code {
//        ServerError,
//        ClientError,
//    }
//
//    public ResponseException(Code code, String message) {
//        super(message);
//    }
    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }
    public Code code() {
        return code;
    }
}