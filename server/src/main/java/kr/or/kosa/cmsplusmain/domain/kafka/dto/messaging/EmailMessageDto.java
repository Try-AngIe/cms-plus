package kr.or.kosa.cmsplusmain.domain.kafka.dto.messaging;

import jakarta.validation.constraints.NotNull;
import kr.or.kosa.cmsplusmain.domain.kafka.MessageSendMethod;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class EmailMessageDto extends MessageDto {

    @NotNull
    private String emailAddress;

    public EmailMessageDto(String text, String emailAddress) {
        super(MessageSendMethod.EMAIL, text);
        this.emailAddress = emailAddress;
    }

}




