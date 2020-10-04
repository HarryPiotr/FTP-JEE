package jsf;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.print.attribute.standard.Severity;
import javax.servlet.http.HttpSession;
import jee.HiberManager;
import jee.UserEntity;
import java.sql.Timestamp;

@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean {

    public UserEntity loginUser;

    private HiberManager dbManager;

    private Integer id;
    private String username;
    private String password;
    private boolean isAdmin;
    private Timestamp lastLogin;

    public LoginBean() {

        loginUser = new UserEntity();
        dbManager = new HiberManager();
        username = "";
        password = "";
    }

    private void updateLogin() {
        this.lastLogin = new Timestamp(System.currentTimeMillis());
        this.loginUser.setLastLogin(this.lastLogin);
        dbManager.updateUser(this.loginUser);
    }
    
    public String tryLogin() {

        UserEntity u = dbManager.findUser(this.username);

        boolean isCorrect = false;
        if (u != null && u.getPassword().equals(this.password)) {
            isCorrect = true;
        }

        if (isCorrect) {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            session.setAttribute("username", this.username);
            loginUser = u;
            updateLogin();
            return "menu";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Błąd", "Zły login lub hasło"));
            return "index";
        }
    }

    public String loginAnonymous() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.setAttribute("username", "anonim");
        loginUser = dbManager.findUser("anonim");
        updateLogin();
        return "menu";
    }

    public String logout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.removeAttribute("username");
        return "index";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

}
