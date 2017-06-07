package com.yotouch.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yotouch.core.Consts;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Created by king on 3/29/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Component
public class EntityModel {
    private String uuid;
    private String creatorUuid;
    private Calendar createdAt;
    private Calendar updatedAt;
    private String updaterUuid;
    private Integer status = Consts.STATUS_NORMAL;
    private String company;

    @JsonProperty(value = "wf_workflow")
    private String wfWorkflow;
    @JsonProperty(value = "wf_state")
    private String wfState;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCreatorUuid() {
        return creatorUuid;
    }

    public void setCreatorUuid(String creatorUuid) {
        this.creatorUuid = creatorUuid;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public Calendar getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Calendar updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdaterUuid() {
        return updaterUuid;
    }

    public void setUpdaterUuid(String updaterUuid) {
        this.updaterUuid = updaterUuid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getWfWorkflow() {
        return wfWorkflow;
    }

    public void setWfWorkflow(String wfWorkflow) {
        this.wfWorkflow = wfWorkflow;
    }

    public String getWfState() {
        return wfState;
    }

    public void setWfState(String wfState) {
        this.wfState = wfState;
    }
}
