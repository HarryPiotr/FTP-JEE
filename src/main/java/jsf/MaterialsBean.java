package jsf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import jee.*;
import java.sql.Timestamp;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.Part;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "materialsBean")
@SessionScoped
public class MaterialsBean {

    private HiberManager dbManager;
    private List<CategoryEntity> categoryList;
    private CategoryEntity chosenCategory;
    private Integer chosenCategoryId;
    private List<String> possibleViewmodes;
    private String chosenViewmode;

    private String new_category;

    private Part file;

    private List<FileEntity> browsedCategoryFiles;
    private FileEntity fileToModify;

    public MaterialsBean() {
        dbManager = new HiberManager();
        possibleViewmodes = new ArrayList<>();
        possibleViewmodes.add("everyone");
        possibleViewmodes.add("registered");
        possibleViewmodes.add("me");
        chosenViewmode = "everyone";
    }

    public String chooseCategory(CategoryEntity choice) {
        setChosenCategoryId(choice.getId());
        return "categoryfiles";
    }

    public String noBytesToString(Long bytes) {
        Double kilobytes = (double) bytes / 1024;
        Double megabytes = kilobytes / 1024;
        Double gigabytes = megabytes / 1024;

        if (gigabytes > 0.85) {
            return "" + String.format("%.2f", gigabytes) + "GB";
        } else if (megabytes > 0.85) {
            return "" + String.format("%.2f", megabytes) + "MB";
        } else if (kilobytes > 1) {
            return "" + String.format("%.2f", kilobytes) + "kB";
        } else {
            return "" + bytes + "B";
        }
    }

    public StreamedContent download(FileEntity dwf) throws FileNotFoundException {
        StreamedContent streamedFile = new DefaultStreamedContent(new FileInputStream(dwf.getPath()), dwf.getContent_type(), dwf.getName());
        return streamedFile;
    }

    public String goToRename(CategoryEntity chc) {
        chosenCategory = chc;
        return "renamecategory";
    }
    
    public String chooseFileToModify(FileEntity ftm) {
        this.fileToModify = ftm;
        return "modifyfile";
    }

    public void upload() throws SQLException, IOException {

        FileEntity nf = new FileEntity();
        nf.setName(file.getSubmittedFileName());
        nf.setContent_type(getFile().getContentType());
        nf.setDateAdded(new Timestamp(System.currentTimeMillis()));
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        String cu = (String) session.getAttribute("username");
        UserEntity curr = dbManager.findUser(cu);
        nf.setOwner_id(curr.getId());
        nf.setOwner_name(curr.getUsername());
        nf.setVisibility(this.chosenViewmode);
        nf.setSize(file.getSize());
        nf.setCategory_id(getChosenCategoryId());

        CategoryEntity cat = dbManager.findCategory(getChosenCategoryId());

        File dir = new File("files/" + cat.getName() + "/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File localFile = new File(dir, file.getSubmittedFileName());

        try (InputStream input = file.getInputStream()) {
            Files.copy(input, localFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        nf.setPath(localFile.getAbsolutePath());
        dbManager.saveFile(nf);
    }

    public String renameCategory() {
        dbManager.saveCategory(chosenCategory);
        return "materials";
    }

    public String modifyFile(FileEntity f) {
        dbManager.saveFile(f);
        return "categoryfiles";
    }

    public void addCategory() {
        dbManager.saveCategory(new CategoryEntity(new_category));
    }

    public void removeCategory(CategoryEntity cat) {
        dbManager.removeCategory(cat);
    }

    public void removeFile(FileEntity fil) {
        dbManager.removeFile(fil);
    }

    public List<CategoryEntity> getCategoryList() {
        categoryList = dbManager.getAllCategories();
        return categoryList;
    }

    public void setCategoryList(List<CategoryEntity> categoryList) {
        this.categoryList = categoryList;
    }

    public String getNew_category() {
        return new_category;
    }

    public void setNew_category(String new_category) {
        this.new_category = new_category;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public List<FileEntity> getBrowsedCategoryFiles() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        String cu = (String) session.getAttribute("username");
        UserEntity loggedUser = dbManager.findUser(cu);

        String whichViewmode = "";

        if (loggedUser.getUsername().equalsIgnoreCase("anonim")) {
            whichViewmode = "anon";
        } else if (loggedUser.getIsPrivileged()) {
            whichViewmode = "PU";
        } else {
            whichViewmode = "SU";
        }

        switch (whichViewmode) {
            case "PU":
                browsedCategoryFiles = dbManager.getPUFiles(getChosenCategoryId());
                break;
            case "SU":
                browsedCategoryFiles = dbManager.getSUFiles(getChosenCategoryId(), loggedUser.getId());
                break;
            case "anon":
                browsedCategoryFiles = dbManager.getEveryoneFiles(getChosenCategoryId());
                break;
        }
        return browsedCategoryFiles;
    }

    public void setBrowsedCategoryFiles(List<FileEntity> browsedCategoryFiles) {
        this.browsedCategoryFiles = browsedCategoryFiles;
    }

    public Integer getChosenCategoryId() {
        return chosenCategoryId;
    }

    public void setChosenCategoryId(Integer chosenCategoryId) {
        this.chosenCategoryId = chosenCategoryId;
    }

    public CategoryEntity getChosenCategory() {
        return chosenCategory;
    }

    public void setChosenCategory(CategoryEntity chosenCategory) {
        this.chosenCategory = chosenCategory;
    }

    public List<String> getPossibleViewmodes() {
        return possibleViewmodes;
    }

    public void setPossibleViewmodes(List<String> possibleViewmodes) {
        this.possibleViewmodes = possibleViewmodes;
    }

    public String getChosenViewmode() {
        return chosenViewmode;
    }

    public void setChosenViewmode(String chosenViewmode) {
        this.chosenViewmode = chosenViewmode;
    }

    public FileEntity getFileToModify() {
        return fileToModify;
    }

    public void setFileToModify(FileEntity fileToModify) {
        this.fileToModify = fileToModify;
    }
}
