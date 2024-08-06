package kr.or.kosa.cmsplusbatch.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class InfraTestController {

    @GetMapping("/infra-test")
    public String infraTest() {
        log.error("infraTest");
        return "infra-test v0806-1";
    }

}
