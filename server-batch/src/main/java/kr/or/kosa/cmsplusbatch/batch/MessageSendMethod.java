package kr.or.kosa.cmsplusbatch.batch;

import kr.or.kosa.cmsplusbatch.domain.base.entity.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageSendMethod implements BaseEnum {
    SMS("문자"), EMAIL("이메일");

    private final String title;
    private final String code;

    MessageSendMethod(String title) {
        this.title = title;
        this.code = this.name();
    }

}
