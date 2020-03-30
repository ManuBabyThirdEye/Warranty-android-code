package com.ewarranty.warranty.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentCount {
    private int pendingIncidents;
    private int completedIncidents;
    private int canceledIncidents;

    public boolean isEmpty() {
        return pendingIncidents == 0 && completedIncidents == 0 && canceledIncidents ==0;
    }
}
