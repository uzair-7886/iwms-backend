package com.example.iwms.iwms.dto;

public class RecommendationDto {
    private String vital;
    private String message;
    // (plus getters/setters or Lombok @Data)

    public RecommendationDto() {}
    public String getVital() { return vital; }
    public void setVital(String vital) { this.vital = vital; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
