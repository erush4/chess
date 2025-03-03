package model;

public record ErrorResult(String message) {
    public ErrorResult(String message) {
        this.message = "Error: " + message;
    }
}