package claon.version.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "tb_app_version")
@NoArgsConstructor
public class AppVersion {
    @Id
    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "value", nullable = false)
    private String value;

    private AppVersion(
            String key,
            String value
    ) {
        this.key = key;
        this.value = value;
    }

    public static AppVersion of(
            String key,
            String value
    ) {
        return new AppVersion(key, value);
    }
}
