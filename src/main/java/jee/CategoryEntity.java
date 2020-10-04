package jee;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;


@Data
@Entity
@Table(name = "Categories")
@Builder
public class CategoryEntity implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String name;

    public CategoryEntity() {
    }
    
    public CategoryEntity(String arg1) {
        this.name = arg1;
    }
    
    public CategoryEntity(Integer arg1, String arg2) {
        this.id = arg1;
        this.name = arg2;
    }
}