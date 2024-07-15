package kr.or.kosa.cmsplusmain.domain.base.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Deprecated
public class SortPageDto {

	@Getter
	@Setter
	public static class Req {

		private int page = 1;
		private int size = 10;

		private String sort;
		private String orderBy;

		public boolean isAsc() {
			return sort != null && sort.equalsIgnoreCase("asc");
		}

		public int getPage() {
			return (page < 1) ? 0 : (page - 1) * size;
		}
	}

	@AllArgsConstructor
	@Getter
	public static class Res<T> {
		private int totalPage;
		private List<T> data;
	}

	public static int calcTotalPageNumber(int totalItemNum, int pageSize) {
		return (int) Math.ceil((double) totalItemNum / pageSize);
	}
}
