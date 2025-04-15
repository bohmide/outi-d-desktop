package org.example.Models;

import java.sql.Date;

public class Prof {
    private Long id;
    private Date interDate;
    private String interMode;

    // ✅ Constructors
    public Prof() {}

    public Prof(Date interDate, String interMode) {
        this.interDate = interDate;
        this.interMode = interMode;
    }

    // ✅ Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInterDate() {
        return interDate;
    }

    public void setInterDate(Date interDate) {
        this.interDate = interDate;
    }

    public String getInterMode() {
        return interMode;
    }

    public void setInterMode(String interMode) {
        this.interMode = interMode;
    }

    // ✅ Helper methods
    public boolean isOnline() {
        return "online".equalsIgnoreCase(this.interMode);
    }

    public void setOnline(boolean online) {
        this.interMode = online ? "online" : "face to face";
    }

    @Override
    public String toString() {
        return "Prof{" +
                "id=" + id +
                ", interDate=" + interDate +
                ", interMode='" + interMode + ' ' +
                '}';
    }
}

