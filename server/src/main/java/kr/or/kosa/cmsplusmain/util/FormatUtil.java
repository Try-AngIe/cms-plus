package kr.or.kosa.cmsplusmain.util;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatUtil {

    private static final String MONEY_FORMAT = "###,###";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String formatMoney(Long money) {
        DecimalFormat df = new DecimalFormat(MONEY_FORMAT);
        return df.format(money);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DATE_TIME_FORMAT);
    }

    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    public static String formatPhone(String phoneNumber) {
        return phoneNumber != null ? phoneNumber.replaceAll("[^0-9]", "") : null;
    }

    /**
     * 카드 번호 마스킹
     * 앞 6자리와 뒤 4자리를 제외한 나머지를 '*'로 마스킹하고 4자리마다 '-'를 추가
     */
    public static String formatCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 13) {
            return cardNumber;
        }

        String cleaned = cardNumber.replaceAll("[^0-9]", "");
        StringBuilder masked = new StringBuilder(cleaned.substring(0, 6));

        for (int i = 6; i < cleaned.length() - 4; i++) {
            masked.append('*');
        }

        masked.append(cleaned.substring(cleaned.length() - 4));

        // 4자리마다 '-' 추가
        for (int i = 4; i < masked.length(); i += 5) {
            masked.insert(i, '-');
        }

        return masked.toString();
    }

    /**
     * 계좌번호 마스킹
     * 앞 3자리와 뒤 4자리를 제외한 나머지를 '*'로 마스킹하고 4자리마다 '-'를 추가
     */
    public static String formatAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 8) {
            return accountNumber;
        }

        String cleaned = accountNumber.replaceAll("[^0-9]", "");
        StringBuilder masked = new StringBuilder(cleaned.substring(0, 3));

        for (int i = 3; i < cleaned.length() - 4; i++) {
            masked.append('*');
        }

        masked.append(cleaned.substring(cleaned.length() - 4));

        return masked.toString();
    }
}