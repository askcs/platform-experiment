package com.askcs.platform.listener;

public enum AgentTemplate {
	DEFAULT( "defaultAgent" ),
    XMPP( "xmppAgent" );

    private String value;

    private AgentTemplate( String value )
    {
        this.value = value;
    }
    
    public String getName()
    {
        return value;
    }

    /**
     * returns the enum based on the name or the value
     * 
     * @param value
     * @return
     */
    public static AgentTemplate getByValue(String value) {

        for (AgentTemplate template : values()) {
            if (template.getName().equalsIgnoreCase(value) || template.name().equalsIgnoreCase(value)) {
                return template;
            }
        }
        return null;
    }
}
