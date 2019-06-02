package Util;

public enum APIStatus {
    OK(200, "OK"),
    NO_RESULT(201, "No more result."),
    //////////////////
    // CLIENT SIDE  //
    //////////////////
    ERR_BAD_REQUEST(400, "Bad request"),
    ERR_UNAUTHORIZED(401, "Unauthorized or Access Token is expired"),
    ERR_FORBIDDEN(403, "Forbidden! Access denied"),
    ERR_BAD_PARAMS(406, "Bad parameters"),
    ERR_ALREADY_EXISTED(407, "Already exsited."),
    //////////////////
    // SERVER SIDE  //
    //////////////////
    ERR_INTERNAL_SERVER(500, "Internal Server Error"),
    //////////////////
    // SESSION SIDE //
    //////////////////
    ERR_TOKEN_NOT_FOUND(600, "Access token not found"),
    ERR_INVALID_TOKEN(601, "Access token is invalid"),
    ERR_TOKEN_EXPIRED(602, "Access token is expired"),
    ERR_SESSION_DATA_INVALID(603, "Invalid session data"),
    ERR_SESSION_NOT_FOUND(604, "Session not found"),
    ERR_ACCOUNT_INVALID(605, "Invalid account"),
    ERR_CREATE_USER_SESSION(606, "Create User Session fail"),
    //////////////////
    // DATABASE SIDE//
    //////////////////
    ERR_INCORRECT_MODEL_DATA(700, "Incorrect model data"),
    ERR_USER_NOT_FOUND(701, "User not found."),
    ERR_PASSWORD_NOT_MATCH(702, "Password does not match"),
    ERR_CREATE_USER(703, "Create User fail"),
    ERR_INVALID_PARAM(704, "Param is invalid"),
    ERR_OLD_PASS(705, "Old password incorrect");

    private final int code;
    private final String description;

    private APIStatus(int s, String v) {
        code = s;
        description = v;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}