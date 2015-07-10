package com.askcs.platform.entity;

import java.util.Set;

public class Report {

	protected String uuid;
	protected String title;
	protected String body;
	protected long creationTime;
	protected String clientUuid;
	protected String authorUuid;
	
	protected Set<Media> media;
	
	public Set<Media> getMedia() {
		return media;
	}
	public void setMedia(Set<Media> media) {
		this.media = media;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public long getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getClientUuid() {
		return clientUuid;
	}
	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}
	public String getAuthorUuid() {
		return authorUuid;
	}
	public void setAuthorUuid(String authorUuid) {
		this.authorUuid = authorUuid;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if(this.getUuid().compareTo(((Report)obj).getUuid()) == 0){
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
