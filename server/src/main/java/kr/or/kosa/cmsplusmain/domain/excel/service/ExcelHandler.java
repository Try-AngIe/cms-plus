package kr.or.kosa.cmsplusmain.domain.excel.service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import kr.or.kosa.cmsplusmain.domain.excel.ExcelColumn;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelHandler<T> {

	public List<T> handleExcelUpload(MultipartFile file, Class<T> clazz) {
		List<T> dataList = new ArrayList<>();

		try (InputStream inputStream = file.getInputStream()) {
			dataList.addAll(parseExcel(inputStream, clazz));
		} catch (IOException e) {
			return Collections.emptyList();
		}

		return dataList;
	}

	private List<T> parseExcel(InputStream inputStream, Class<T> clazz) throws IOException {
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트를 가져옴

		// 헤더 정보 추출
		Row headerRow = sheet.getRow(3);    // 예시 입력 스킵
		List<String> headers = StreamSupport.stream(headerRow.spliterator(), false)
			.map(Cell::getStringCellValue)
			.collect(Collectors.toList());

		List<T> dataList = StreamSupport.stream(sheet.spliterator(), false)
			.skip(4) // 4 번째 행은 헤더
			.filter(this::isRowNotEmpty) // 빈 행이 아닌 경우
			.map(row -> mapRowToDto(row, clazz, headers))
			.collect(Collectors.toList());

		workbook.close();
		return dataList;
	}

	private boolean isRowNotEmpty(Row row) {
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if (cell.getCellType() != CellType.BLANK) {
				return true; // 빈 셀이 아닌 경우에만 true
			}
		}
		return false; // 모든 셀이 비어 있으면 false
	}

	private T mapRowToDto(Row row, Class<T> clazz, List<String> headers) {
		T dataDTO;
		try {
			dataDTO = clazz.getDeclaredConstructor().newInstance();

			Iterator<Cell> cellIterator = row.cellIterator();
			int cellIndex = 0;
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String header = headers.get(cellIndex);

				//각 필드를 순회하며 커스텀 어노테이션인 ExcelHeader값에 맞게 값을 넣어줌
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(ExcelColumn.class)) {
						ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
						if (annotation.headerName().equals(header)) {
							field.setAccessible(true);
							setFieldValue(field, dataDTO, cell);
							break;
						}
					}
				}

				cellIndex++;
			}
		} catch (Exception e) {
			log.error("excel upload error: {}", e.getMessage());
			return null;
		}
		return dataDTO;
	}

	private void setFieldValue(Field field, T dataDTO, Cell cell) throws IllegalAccessException {
		Class<?> fieldType = field.getType();
		field.setAccessible(true);

		switch (cell.getCellType()) {
			case STRING -> {
				if (fieldType == String.class) {
					field.set(dataDTO, cell.getStringCellValue());
				} else if (fieldType == LocalDate.class) {
					field.set(dataDTO, LocalDate.parse(cell.getStringCellValue()));
				}
			}
			case NUMERIC -> {
				if (fieldType == Double.class || fieldType == double.class) {
					field.set(dataDTO, cell.getNumericCellValue());
				} else if (fieldType == int.class || fieldType == Integer.class) {
					field.set(dataDTO, (int)cell.getNumericCellValue());
				} else if (fieldType == long.class || fieldType == Long.class) {
					field.set(dataDTO, (long)cell.getNumericCellValue());
				}
			}
			case BOOLEAN -> {
				if (fieldType == Boolean.class || fieldType == boolean.class) {
					field.set(dataDTO, cell.getBooleanCellValue());
				}
			}
		}
	}
}