package com.askcs.platform.entity;

import java.io.Serializable;
import java.util.UUID;

public class Team implements Serializable {

	private static final long serialVersionUID = 5886257991625965579L;
	protected String name = null;
    protected String uuid = null;

    public Team() {
    }

    public Team(String name) {
        this(name, UUID.randomUUID().toString());
    }

    public Team(String name, String uuid) {

        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (this.getUuid().equals(((Team) obj).getUuid())) {
            return true;
        }
        else {
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
