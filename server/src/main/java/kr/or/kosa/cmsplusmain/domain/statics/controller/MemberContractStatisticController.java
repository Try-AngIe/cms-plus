package kr.or.kosa.cmsplusmain.domain.statics.controller;

import kr.or.kosa.cmsplusmain.domain.base.security.VendorId;
import kr.or.kosa.cmsplusmain.domain.statics.dto.MemberContractStatisticDto;
import kr.or.kosa.cmsplusmain.domain.statics.dto.MemberContractStatisticRes;
import kr.or.kosa.cmsplusmain.domain.statics.service.MemberContractStatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    public MemberContractStatisticRes getContractPred(@RequestBody MemberContractStatisticDto request) throws
		JSONException {
        WebClient client = WebClient.create(ANALYSIS_URL);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contract_duration", request.getContractDuration());
        jsonObject.put("enroll_year", request.getEnrollYear());
        jsonObject.put("payment_type", request.getPaymentType());
        jsonObject.put("total_contract_amount", request.getTotalContractAmount());

        return client.post()
            .uri("/notebook/member.ipynb")
            .header("Content-Type", "application/json")
            .header("Access-Control-Allow-Origin", "*")
            .body(BodyInserters.fromValue(jsonObject.toString()))
            .accept()
            .retrieve()
            .bodyToMono(MemberContractStatisticRes.class)
            .doOnError(Throwable::printStackTrace)
            .block();
    }
}