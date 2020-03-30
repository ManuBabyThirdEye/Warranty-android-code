package com.ewarranty.warranty.models;

import com.google.gson.annotations.Expose;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Incident {

    @Expose
    private long incidentId;
    @Expose
    private String incidentDescription;
    @Expose
    private String currentStatusDetailDescription;
    @Expose
    private long warrantyCardId;
    @Expose
    private int rating;

    @Expose (serialize = false)
    private List<ImageObject> incidentImages;

    @Expose(serialize = false,deserialize = false)
    private boolean open;

    @Expose(serialize = false)
    private Card warrantyCard;

    @Expose(serialize = false)
    private List<IncidentWorkFlow> incidentWorkFlowList;

    @Expose(serialize = false)
    private Serviceman serviceman;

    @Expose
    private IncidentStatus incidentStatus;

    @Data
    public class ImageObject{
        @Expose
        private String imageUris;

        @Expose
        private long incidentImageId;
    }
}
