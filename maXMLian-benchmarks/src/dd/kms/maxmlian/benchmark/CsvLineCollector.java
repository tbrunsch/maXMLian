package dd.kms.maxmlian.benchmark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class CsvLineCollector
{
	private static final String	COLUMN_SEPARATOR	= ",";

	private int					maxNumColumns	= 0;
	private final List<String>	lines			= new ArrayList<>();

	void addLine(String... columns) {
		addLine(Arrays.asList(columns));
	}

	void addLine(List<String> columns) {
		String line = columns.stream().map(this::encodeColumn).collect(Collectors.joining(COLUMN_SEPARATOR));
		int numColumns = columns.size();
		if (numColumns > maxNumColumns) {
			appendColumns(numColumns - maxNumColumns);
			maxNumColumns = numColumns;
		} else if (numColumns < maxNumColumns) {
			line = appendColumns(line, maxNumColumns - numColumns);
		}
		lines.add(line);
	}

	List<String> getLines() {
		return lines;
	}

	private String encodeColumn(String column) {
		return column.contains(COLUMN_SEPARATOR) ? '"' + column + '"' : column;
	}

	private void appendColumns(int numColumns) {
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			String modifiedLine = appendColumns(line, numColumns);
			lines.set(i, modifiedLine);
		}
	}

	private String appendColumns(String line, int numColumns) {
		if (numColumns == 0) {
			return line;
		}
		if (numColumns < 0) {
			throw new IllegalArgumentException("Number of columns to append must be non-negative");
		}
		StringBuilder builder = new StringBuilder(line);
		for (int i = 0; i < numColumns; i++) {
			builder.append(COLUMN_SEPARATOR);
		}
		return builder.toString();
	}
}
