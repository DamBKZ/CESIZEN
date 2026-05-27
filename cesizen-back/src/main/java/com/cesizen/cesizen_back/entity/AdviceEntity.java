package com.cesizen.cesizen_back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "advice")
public class AdviceEntity {

    @Id
    private String id;

    private String level;

    @Column(columnDefinition = "TEXT")
    private String message;

    public AdviceEntity() {}

    public AdviceEntity(String id, String level, String message) {
        this.id = id;
        this.level = level;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
