package com.example.iwms.iwms.dto;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponse {
    /**
     * Maps vital IDs ("weight", "temperature", etc.) to
     * a List of the last 7 numeric readings.
     */
    private Map<String, List<Number>> historicalData;

    /**
     * The date labels corresponding to those 7 readings,
     * formatted e.g. ["03/04", "03/05", …].
     */
    private List<String> dateLabels;

    /**
     * The most recent VitalRecord (same shape as for a single
     * history row).
     */
    private VitalRecord latest;
}

