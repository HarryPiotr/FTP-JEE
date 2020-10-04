package jee;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import lombok.NonNull;


@Data
@Entity
@Table(name = "Files")
@Builder
public class FileEntity implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @NonNull
    private Integer category_id;
    private String content_type;
    private Integer owner_id;
    private String visibility;
    private Timestamp dateAdded;
    private Long size;
    private String path;
    private String owner_name;

    public FileEntity() {
    }

    public FileEntity(Integer arg1, String arg2, Integer arg3, String arg4, Integer arg5, String arg6, Timestamp arg7, Long arg8, String arg9, String arg10) {
        this.id = arg1;
        this.name = arg2;
        this.category_id = arg3;
        this.content_type = arg4;
        this.owner_id = arg5;
        this.visibility = arg6;
        this.dateAdded = arg7;
        this.size = arg8;
        this.path = arg9;
        this.owner_name = arg10;
    }
    
    public String getReadableTimestamp() {
        Date d = new Date(dateAdded.getTime());
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
        return df.format(d);
    }
    
    public String getCustomVisibility() {
        if(visibility.equals("me")) return owner_name;
        return visibility;
    }
}