package Musicfy.MusicfyOrigin.Product.dto;

import java.time.LocalDateTime;

public class ErrorDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    // Construtor padrão
    public ErrorDTO() {
        this.timestamp = LocalDateTime.now();
    }

    // Construtor para erros simples
    public ErrorDTO(String message) {
        this();
        this.message = message;
    }

    // Construtor completo
    public ErrorDTO(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getters e Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}