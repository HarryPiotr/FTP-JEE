package jsf;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import jee.*;

@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean {

    private UserEntity loggedUser;
    private HiberManager dbManager;

    private Integer id;
    private String username;
    private Boolean isPU;
    private String rightsDescriptor;
    private List<UserEntity> usersList;

    private Integer new_id;
    private String new_username;
    private String new_password;
    private Boolean new_privileged;

    private void getLoggedUser() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        String cu = (String) session.getAttribute("username");
        loggedUser = dbManager.findUser(cu);

        if (loggedUser == null) {
            session.setAttribute("username", "anonim");
            loggedUser = dbManager.findUser("anonim");
        }

        if (loggedUser.getIsPrivileged()) {
            isPU = true;
        } else {
            isPU = false;
        }
        username = loggedUser.getUsername();
        id = loggedUser.getId();
    }

    public Boolean allowedToRemove(FileEntity f) {
        getLoggedUser();
        if (isPU) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean allowedToModify(FileEntity f) {
        getLoggedUser();
        Boolean verdict = false;
        if (isPU) {
            verdict = true;
        }
        if (!isPU && !loggedUser.getUsername().equals("anonim") && f.getOwner_id().equals(loggedUser.getId())) {
            verdict = true;
        }
        return verdict;
    }

    public UserBean() {
        dbManager = new HiberManager();
        getLoggedUser();
    }

    public void onLoad() {
        getLoggedUser();
    }

    public void modify() {
        if (new_id == null) {
            dbManager.saveUser(new UserEntity(new_username, new_password, new_privileged));
        } else {
            UserEntity u = dbManager.findUser(new_id);
            if (u == null) {
                u = new UserEntity();
                u.setId(new_id);
                u.setUsername(new_username);
                u.setPassword(new_password);
                u.setIsPrivileged(new_privileged);
                dbManager.saveUser(u);
            } else {
                if (!new_username.isEmpty() && new_username.length() > 0) {
                    u.setUsername(new_username);
                }
                if (!new_password.isEmpty() && new_password.length() > 0) {
                    if (new_password.equalsIgnoreCase("none")) {
                        u.setPassword("");
                    } else {
                        u.setPassword(new_password);
                    }
                }
                u.setPassword(new_password);
                u.setIsPrivileged(new_privileged);
                dbManager.updateUser(u);
            }
        }
    }

    public void remove(UserEntity usr) {
        dbManager.removeUser(usr);
    }

    public Boolean getIsPU() {
        return isPU;
    }

    public void setIsPU(Boolean isPU) {
        this.isPU = isPU;
    }

    public String getRightsDescriptor() {
        setRightsDescriptor(isPU ? "Użytkownik Uprzywilejowany (PU)" : "Zwykły Użytkownik (SU)");
        return rightsDescriptor;
    }

    public void setRightsDescriptor(String rightsDescriptor) {
        this.rightsDescriptor = rightsDescriptor;
    }

    public List<UserEntity> getUsersList() {
        usersList = dbManager.getAllUsers();
        return usersList;
    }

    public void setUsersList(List<UserEntity> usersList) {
        this.usersList = usersList;
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

    public String getNew_username() {
        return new_username;
    }

    public void setNew_username(String new_username) {
        this.new_username = new_username;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public Boolean getNew_privileged() {
        return new_privileged;
    }

    public void setNew_privileged(Boolean new_privileged) {
        this.new_privileged = new_privileged;
    }

    public Integer getNew_id() {
        return new_id;
    }

    public void setNew_id(Integer new_id) {
        this.new_id = new_id;
    }
}
