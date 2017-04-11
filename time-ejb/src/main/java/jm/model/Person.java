package jm.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by vk on 10.04.17.
 */
@Entity
@XmlRootElement
@Table(name = "time_person", indexes = {
//        @Index(name = "time_person_ix0001", columnList = "name"),
//        @Index(name = "time_person_ix0002", columnList = "ext_id"),
//        @Index(name = "time_person_ix0003", columnList = "ext_name"),
})
public class Person extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "ext_id")
    private String extId;

    @Column(name = "ext_name")
    private String extName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getExtName() {
        return extName;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("extId", extId)
                .append("extName", extName)
                .toString();
    }
}
