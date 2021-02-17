package orangetaxiteam.cocoman.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "TB_DIRECTOR")
@EntityListeners(AuditingEntityListener.class)
public class Director {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", unique = true)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_path")
    private String imagePath;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "directorSet", cascade = CascadeType.ALL)
    private Set<Contents> contentsSet;

    private Director(
            String id,
            String name,
            String imagePath,
            Set<Contents> contentsSet
    ) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.contentsSet = contentsSet;
    }

    public static Director of(String name, String imagePath) {
        return new Director(
                null,
                name,
                imagePath,
                null
        );
    }
}
