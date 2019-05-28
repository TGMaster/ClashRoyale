package Util;

public class ResponseUtil {
    private int status;
    private String message;

    public ResponseUtil(APIStatus apiStatus) {
        if (apiStatus == null) {
            throw new IllegalArgumentException("APIStatus must not be null");
        }

        this.status = apiStatus.getCode();
        this.message = apiStatus.getDescription();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}