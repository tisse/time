package jm.model;

/**
 * Created by vk on 11.04.17.
 */
public class TimeResponse<T> {

    private ResponseCode responseCode;
    private T responseBody;

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public T getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(T responseBody) {
        this.responseBody = responseBody;
    }

    public enum ResponseCode {
        SUCCESS, FAIL;
    }

}
