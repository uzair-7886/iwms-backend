package com.example.iwms.iwms.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VitalRecord {
    private Long   id;
    private String date;        // yyyy-MM-dd
    private String weight;      // e.g. "70 kg"
    private String bp;          // e.g. "120/80 mmHg"
    private String heartRate;   // e.g. "72 bpm"
    private String glucose;     // e.g. "90 mg/dL"
    private String spo2;        // e.g. "98%"
    private String temperature; // e.g. "36.7°C"
}
