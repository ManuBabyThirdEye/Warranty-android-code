package com.ewarranty.warranty.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentWorkFlow {
    private Date date;
    private String description;
    private String nextWorkFlow;
    private IncidentWorkFlowStatus workFlowStatus;
}
