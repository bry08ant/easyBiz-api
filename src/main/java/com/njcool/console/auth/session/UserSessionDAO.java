package com.njcool.console.auth.session;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc 用户session管理
 */
public class UserSessionDAO extends AbstractSessionDAO {

    private static final Logger LOG = Logger.getLogger(UserSessionDAO.class.getName());

    private UserSessionRepository userSessionRepository;

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        userSessionRepository.saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return userSessionRepository.getSession(sessionId);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        userSessionRepository.saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            LOG.error("Session 不能为null");
            return;
        }
        Serializable id = session.getId();
        if (id != null)
            userSessionRepository.deleteSession(id);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return userSessionRepository.getAllSessions();
    }

    public void setUserSessionRepository(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }
}
