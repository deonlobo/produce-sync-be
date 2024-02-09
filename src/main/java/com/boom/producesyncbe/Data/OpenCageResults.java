package com.boom.producesyncbe.Data;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OpenCageResults {
    private GeoLocation geometry;

    public GeoLocation getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoLocation geometry) {
        this.geometry = geometry;
    }
}
