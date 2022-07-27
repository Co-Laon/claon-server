package coLaon.ClaonBack.center.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HoldInfoKey {
    private String holdId;
    private String holdImage;

    @Override
    public int hashCode() {
        return holdId.hashCode() * holdImage.hashCode();
    }
}
