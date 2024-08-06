package kr.or.kosa.cmsplusmain.domain.excel.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExcelErrorRes<T> {
	private T notSaved;
	private String message;
}
