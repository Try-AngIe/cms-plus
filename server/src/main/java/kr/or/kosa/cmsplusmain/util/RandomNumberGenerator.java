package kr.or.kosa.cmsplusmain.util;

import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;

import java.util.Random;

public class RandomNumberGenerator {
    public static String generateRandomNumber(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        Random random = new Random();
        StringBuilder randomNumber = new StringBuilder();

        for (int i = 0; i < length; i++) {
            randomNumber.append(random.nextInt(10)); // 0-9 사이의 숫자를 추가
        }

        return randomNumber.toString();
    }

    public static String generateRandomNumber(int length, PaymentType type) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        Random random = new Random();
        StringBuilder randomNumber = new StringBuilder();

        for (int i = 0; i < length - 1; i++) {
            randomNumber.append(random.nextInt(10)); // 0-9 사이의 숫자를 추가
        }

        if (type == PaymentType.VIRTUAL) {
            // 마지막 숫자는 무조건 짝수
            randomNumber.append(random.nextInt(5) * 2); // 0, 2, 4, 6, 8 중 하나
        } else {
            randomNumber.append(random.nextInt(10)); // 0-9 사이의 숫자를 추가
        }

        return randomNumber.toString();
    }
}
