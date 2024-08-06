package kr.or.kosa.cmsplusmain.domain.statics.controller;

import kr.or.kosa.cmsplusmain.domain.base.security.VendorId;
import kr.or.kosa.cmsplusmain.domain.statics.dto.MemberContractStatisticDto;
import kr.or.kosa.cmsplusmain.domain.statics.dto.MemberContractStatisticRes;
import kr.or.kosa.cmsplusmain.domain.statics.service.MemberContractStatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Slf4j
public class MemberContractStatisticController {

    private final MemberContractStatisticService memberContractStatisticService;

    @Value("${host.analysis}")
    private String ANALYSIS_URL;

    @GetMapping("/member-contracts")
    public ResponseEntity<List<MemberContractStatisticDto>> getMemberContractStatistics(@VendorId Long vendorId) {
        List<MemberContractStatisticDto> statistics;
        statistics = memberContractStatisticService.getMemberContractStatistics(vendorId);
        return ResponseEntity.ok(statistics);
    }

    @PostMapping("/notebook/member.ipynb")
    public MemberContractStatisticRes getContractPred(@RequestBody MemberContractStatisticDto request) {
        WebClient client = WebClient.create(ANALYSIS_URL);
        Map<String, Object> body = new HashMap<>();
        body.put("contract_duration", request.getContract_duration());
        body.put("enroll_year", request.getEnroll_year());
        body.put("payment_type", request.getPayment_type());
        body.put("total_contract_amount", request.getTotal_contract_amount());

        log.info("req: {}", body.toString());

        return client.post()
            .uri("/notebook/member.ipynb")
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*")
            .bodyValue(body)
            .accept()
            .retrieve()
            .bodyToMono(MemberContractStatisticRes.class)
            .doOnError(Throwable::printStackTrace)
            .block();
    }
}