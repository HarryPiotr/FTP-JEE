package jee;

import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(FileEntity.class)
public abstract class FileEntity_ {

	public static volatile SingularAttribute<FileEntity, String> path;
	public static volatile SingularAttribute<FileEntity, String> owner_name;
	public static volatile SingularAttribute<FileEntity, Integer> category_id;
	public static volatile SingularAttribute<FileEntity, String> content_type;
	public static volatile SingularAttribute<FileEntity, String> visibility;
	public static volatile SingularAttribute<FileEntity, Long> size;
	public static volatile SingularAttribute<FileEntity, Integer> owner_id;
	public static volatile SingularAttribute<FileEntity, String> name;
	public static volatile SingularAttribute<FileEntity, Integer> id;
	public static volatile SingularAttribute<FileEntity, Timestamp> dateAdded;

}

