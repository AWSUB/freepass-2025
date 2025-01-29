package com.freepass.conference.dto;

public class DefaultResponse<T> {

    private boolean success;
    private T payload;
    private String error;

    private DefaultResponse(boolean success, T payload, String error) {
        this.success = success;
        this.payload = payload;
        this.error = error;
    }

    public static <G> DefaultResponse<G> success(G payload) {
        return new DefaultResponse<>(true, payload, null);
    }

    public static <G> DefaultResponse<G> failed(G payload, String error) {
        return new DefaultResponse<G>(false, payload, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getPayload() {
        return payload;
    }

    public String getError() {
        return error;
    }
}