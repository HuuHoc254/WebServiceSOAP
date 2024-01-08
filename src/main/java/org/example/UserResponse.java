package org.example;

public class UserResponse {
    private String status;

    public UserResponse() {
    }

    public UserResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
