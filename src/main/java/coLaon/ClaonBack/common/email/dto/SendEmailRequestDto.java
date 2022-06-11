package coLaon.ClaonBack.common.email.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SendEmailRequestDto {
    
    @NotNull
    private String subject;
    
    @NotNull
    private String body;
    private boolean htmlBody;
    
    @NotNull
    private List<String> toAddresses;
    private List<String> ccAddresses;
    private List<String> bccAddresses;
    
}
