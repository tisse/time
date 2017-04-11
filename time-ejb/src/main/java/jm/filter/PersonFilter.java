package jm.filter;

import jm.model.Person;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by vk on 10.04.17.
 */
public class PersonFilter extends BaseFilter<Person> {

    private String name;
    private String extName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                .append("extName", extName)
                .toString();
    }
}
