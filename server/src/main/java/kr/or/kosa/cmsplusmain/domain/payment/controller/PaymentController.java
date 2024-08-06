package kr.or.kosa.cmsplusmain.domain.payment.controller;

import kr.or.kosa.cmsplusmain.domain.kafka.dto.payment.PaymentResultDto;
import kr.or.kosa.cmsplusmain.domain.payment.dto.PaymentBankRes;
import kr.or.kosa.cmsplusmain.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/test")
    public void paymentResult(PaymentResultDto paymentResult) {
        log.error("[결제 결과]: {}", paymentResult.toString());
    }

    @GetMapping("/payment/banks")
    public List<PaymentBankRes> getBanks() {
        return paymentService.getAllBanks();
    }

}
