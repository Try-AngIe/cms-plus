package kr.or.kosa.cmsplusmain.test;

import jakarta.annotation.PostConstruct;
import kr.or.kosa.cmsplusmain.SampleDataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j(topic = "monitoring-topic")
@RequiredArgsConstructor
public class InfraTestController {

    private final SampleDataLoader sampleDataLoader;

    @PostConstruct
    public void init() {
        sampleDataLoader.init();
    }

    @GetMapping("/infra-test")
    public String infraTest() {
        log.error("infraTest");
        return "infra-test v0806-1";
    }

}
