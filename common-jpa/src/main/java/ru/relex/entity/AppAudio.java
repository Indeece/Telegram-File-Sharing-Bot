package ru.relex.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_audio")
@Entity
public class AppAudio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramFileId;
    @OneToOne
    private BinaryContent binaryContent;
    private String mimeType;
    private Long fileSize;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppAudio appAudio = (AppAudio) o;
        return telegramFileId != null && Objects.equals(telegramFileId, appAudio.telegramFileId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
