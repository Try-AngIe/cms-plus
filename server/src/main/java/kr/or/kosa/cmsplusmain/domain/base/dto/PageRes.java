package kr.or.kosa.cmsplusmain.domain.base.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class PageRes<T> {
	private final int totalPage;
	private final List<T> content;

	/*
	* 전체 페이지 수 = 올림(전체 개수 / 한 페이지 크기)
	* */
	public PageRes(int totalContentCount, int pageSize, List<T> content) {
		this.totalPage = (int) Math.ceil((double) totalContentCount / pageSize);
		this.content = content;
	}
}
