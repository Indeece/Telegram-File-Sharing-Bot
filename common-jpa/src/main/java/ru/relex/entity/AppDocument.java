package ru.relex.entity;

import lombok.*;
import org.hibernate.Hibernate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_document")
@Entity
public class AppDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramFileId;

    private String docName;

    @OneToOne
    private BinaryContent binaryContent;

    private String mimeType;

    private Long fileSize;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppDocument that = (AppDocument) o;
        return telegramFileId != null && Objects.equals(telegramFileId, that.telegramFileId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}