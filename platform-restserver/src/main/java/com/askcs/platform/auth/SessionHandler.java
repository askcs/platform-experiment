package com.askcs.platform.auth;


public class SessionHandler{

    private static SessionHandler sh = null;
    private static ThreadLocal<Session> currentSession = new ThreadLocal<Session>();

    private SessionHandler() {
    }

    public SessionHandler getSessionHandler() {
        if ( sh == null ) {
            sh = new SessionHandler();
        }

        return sh;
    }

    public static Session createSession( String accountId, String domainId ) {

        Session session = Session.createSession( accountId, domainId );

        if ( session != null )
            currentSession.set( session );

        return session;
    }

    public static boolean checkSession( String token ) {

        Session session = Session.fromToken( token );
        if ( session != null && !session.isExpired() ) {
            currentSession.set( session );
            return true;
        }

        return false;
    }

    public static Session getCurrentSession() {
        return currentSession.get();
    }

    public static void clearCurrentSession() {
        currentSession.remove();
    }
}
