package id.bafika.tag;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Tag extends PanacheEntity {

    @Column(length = 50)
    public String label;

    public static Tag findByLabel(String label) {
        return find("label", label).firstResult();
    }
    
}
