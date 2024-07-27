package kr.or.kosa.cmsplusmain.domain.vendor.dto.authenticationNumber;

import kr.or.kosa.cmsplusmain.domain.kafka.MessageSendMethod;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NumberReq {
    private String userInfo;
    private String methodInfo;
    private MessageSendMethod method;
}
