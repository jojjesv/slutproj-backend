/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.johan.foodi.admin;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.io.IOException;
import java.sql.SQLException;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import se.johan.foodi.util.ConnectionUtils;

/**
 *
 * @author johan
 */
@SessionScoped
@ManagedBean
public class AuthBean {

    private AuthData authData;

    /**
     * Redirects back to the login page if not authorized.
     */
    public ComponentSystemEventListener redirectIfUnauthorized() {
        return new ComponentSystemEventListener() {
            @Override
            public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
                System.out.println("redirectIfUnauthorized");
                if (!isAuthorized()) {
                    try {
                        ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse()).sendRedirect("/index.xhtml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }

    public boolean isAuthorized() {
        return this.authData != null;
    }

    /**
     * Statefully authorizes the user's credentials.
     *
     * @return Whether the authorization was successful.
     */
    public boolean authorize(String username, String password) throws SQLException {
        String hashedPassword = ConnectionUtils.querySingleString(
                "SELECT password_hash FROM admin WHERE username = ?", username
        );

        if (hashedPassword == null) {
            //  Unknown user
            return false;
        }

        BCrypt.Result verifyResult = BCrypt.verifyer().verify(
                password.getBytes(), hashedPassword.getBytes()
        );

        if (!verifyResult.verified) {
            //  Invalid password
            return false;
        }

        authData = new AuthData();
        authData.setUsername(username);

        return true;
    }

    /**
     * Data for when authorized.
     */
    static class AuthData {

        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
