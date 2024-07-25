package kr.or.kosa.cmspluspayment.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfraTestController {

    @GetMapping("/infra-test-payment")
    public String infraTest() {
        return "infra-test v5";
    }

}
