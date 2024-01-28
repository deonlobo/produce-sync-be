package com.boom.producesyncbe.Data;

import org.springframework.data.annotation.Id;
public class IdCount {
    @Id
    private String id;
    private Integer count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
