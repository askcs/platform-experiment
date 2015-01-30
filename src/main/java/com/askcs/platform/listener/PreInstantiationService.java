package com.askcs.platform.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.almende.eve.agent.AgentBuilder;
import com.almende.eve.agent.AgentConfig;
import com.almende.eve.instantiation.InstantiationEntry;
import com.almende.eve.instantiation.InstantiationService;
import com.almende.eve.state.State;
import com.almende.eve.state.StateBuilder;
import com.almende.eve.state.StateConfig;
import com.almende.eve.state.StateService;
import com.almende.util.TypeUtil;
import com.almende.util.jackson.JOM;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PreInstantiationService{

    private static final Logger                                                 LOG                                     = Logger.getLogger(InstantiationService.class
                                                                                                                                           .getName());
    private static final TypeUtil<InstantiationEntry>       INSTANTIATIONENTRY      = new TypeUtil<InstantiationEntry>() {};
    private ObjectNode                                                                      myParams                        = null;
    private String                                                                          myId                            = null;
    private Map<String, InstantiationEntry>                         entries                         = new HashMap<String, InstantiationEntry>();
    private StateService                                                            stateService            = null;
    private ClassLoader                                                                     cl                                      = null;
    private AgentConfig defaultConfig = null;
    
    /**
    * Instantiates a new wake service.
    */
    public PreInstantiationService() {};
    
    /**
    * Instantiates a new InstantiationService.
    *
    * @param params
    *            the params, containing at least a "state" field, with a
    *            specific State configuration.
    * @param cl
    *            the cl
    */
    public PreInstantiationService( final ObjectNode params, final ClassLoader cl, final AgentConfig config ) {
        this.cl = cl;
        myParams = params;
        final State state = new StateBuilder().withConfig( (ObjectNode) myParams.get( "state" ) ).build();
        stateService = state.getService();
        myId = state.getId();
        defaultConfig = new AgentConfig(config.expand());
        load();
    }
    
    public void boot() {
        load();
        for (final String key : entries.keySet()) {
                init(key, true);
        }
    }
    
    public void init( final String wakeKey, final boolean onBoot ) {
        InstantiationEntry entry = entries.get( wakeKey );
        if ( entry == null ) {
            load( wakeKey );
        }
    }
    
    /**
     * Load.
     */
    private void load() {
            final Set<String> stateIds = stateService.getStateIds();
            for (String key : stateIds) {
                    if (key.equals(myId)) {
                            continue;
                    }
                    entries.put(key, null);
            }
    }
    
    /**
     * Load.
     *
     * @param key
     *            the key
     * @return the instantiation entry
     */
    protected void load(final String key) {
            final State innerState = new StateBuilder().withConfig(
                            new StateConfig((ObjectNode) myParams.get("state")).put("id",
                                            key)).build();
            final InstantiationEntry result = innerState.get("entry", INSTANTIATIONENTRY);
            String className = innerState.get("_type", String.class);
            if(result==null && className!=null) {
                AgentConfig newConfig = new AgentConfig( key, (ObjectNode) defaultConfig );
                register( key, (ObjectNode) newConfig, className );
            }
    }
    
    /**
     * Register.
     *
     * @param wakeKey
     *            the wake key
     * @param params
     *            the params
     * @param className
     *            the class name
     */
    @JsonIgnore
    public void register( final String wakeKey, final ObjectNode params,
                          final String className ) {
        final InstantiationEntry entry = new InstantiationEntry( wakeKey,
                                                                 params,
                                                                 className );
        entries.put( wakeKey, entry );
        store( wakeKey, entry );
    }

    /**
     * Store.
     *
     * @param key
     *            the key
     * @param val
     *            the val
     */
    private void store( final String key, final InstantiationEntry val ) {
        State innerState = null;
        if ( val != null ) {
            innerState = val.getState();
        }
        if ( innerState == null ) {
            innerState = new StateBuilder()
                .withConfig( new StateConfig( (ObjectNode) myParams.get( "state" ) )
                    .put( "id", key ) ).build();
        }
        if ( innerState != null ) {
            innerState.put( "entry", JOM.getInstance().valueToTree( val ) );
        }
    }
}
