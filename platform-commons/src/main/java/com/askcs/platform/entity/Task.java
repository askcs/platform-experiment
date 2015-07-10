package com.askcs.platform.entity;

import java.io.Serializable;

public class Task implements Serializable {
	
	
	private static final long serialVersionUID = 8153866182531796928L;
	protected String uuid = null;
	protected String description = null;
	protected String authorUuid = null;
	protected String relatedClientUuid = null;
	protected String assignedTeamUuid = null;
	protected String assignedTeamMemberUuid = null;
	
//	protected int type = 0;
	
	/*
		ACTIVE : 1
		PLANNING : 2
		FINISHED: 3
		CANCELLED: 4
	*/
	protected int status;
	protected long plannedStartVisitTime = 0;
	protected long plannedEndVisitTime = 0;
	
	protected Location realizedStartTravelLocation;
	protected long realizedStartTravelTime;
	protected long realizedStartVisitTime;
	protected long realizedEndVisitTime;
	protected Location realizedVisitLocation;
	
	
	public Task(){}
	
	public Task(String uuid,String authorUuid) {
		this.uuid = uuid;
		this.authorUuid=authorUuid;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthorUuid() {
		return authorUuid;
	}

	public void setAuthorUuid(String authorUuid) {
		this.authorUuid = authorUuid;
	}

	public String getRelatedClientUuid() {
		return relatedClientUuid;
	}

	public void setRelatedClientUuid(String relatedClientUuid) {
		this.relatedClientUuid = relatedClientUuid;
	}

	public String getAssignedTeamUuid() {
		return assignedTeamUuid;
	}

	public void setAssignedTeamUuid(String assignedTeamUuid) {
		this.assignedTeamUuid = assignedTeamUuid;
	}

	public String getAssignedTeamMemberUuid() {
		return assignedTeamMemberUuid;
	}

	public void setAssignedTeamMemberUuid(String assignedTeamMemberUuid) {
		this.assignedTeamMemberUuid = assignedTeamMemberUuid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getPlannedStartVisitTime() {
		return plannedStartVisitTime;
	}

	public void setPlannedStartVisitTime(long plannedStartVisitTime) {
		this.plannedStartVisitTime = plannedStartVisitTime;
	}

	public long getPlannedEndVisitTime() {
		return plannedEndVisitTime;
	}

	public void setPlannedEndVisitTime(long plannedEndVisitTime) {
		this.plannedEndVisitTime = plannedEndVisitTime;
	}

	public Location getRealizedStartTravelLocation() {
		return realizedStartTravelLocation;
	}

	public void setRealizedStartTravelLocation(Location realizedStartTravelLocation) {
		this.realizedStartTravelLocation = realizedStartTravelLocation;
	}

	public long getRealizedStartTravelTime() {
		return realizedStartTravelTime;
	}

	public void setRealizedStartTravelTime(long realizedStartTravelTime) {
		this.realizedStartTravelTime = realizedStartTravelTime;
	}

	public long getRealizedStartVisitTime() {
		return realizedStartVisitTime;
	}

	public void setRealizedStartVisitTime(long realizedStartVisitTime) {
		this.realizedStartVisitTime = realizedStartVisitTime;
	}

	public long getRealizedEndVisitTime() {
		return realizedEndVisitTime;
	}

	public void setRealizedEndVisitTime(long realizedEndVisitTime) {
		this.realizedEndVisitTime = realizedEndVisitTime;
	}

	public Location getRealizedVisitLocation() {
		return realizedVisitLocation;
	}

	public void setRealizedVisitLocation(Location realizedVisitLocation) {
		this.realizedVisitLocation = realizedVisitLocation;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if(this.getUuid().compareTo(((Task)obj).getUuid()) == 0){
        	return true;
        }else{
        	return false;
        }
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + uuid.hashCode();
		return hash;
	}
}
