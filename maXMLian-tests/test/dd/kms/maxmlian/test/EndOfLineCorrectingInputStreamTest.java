package dd.kms.maxmlian.test;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Tests that {@link EndOfLineCorrectingInputStream} correctly converts the line breaks of an {@link InputStream}
 * with characters to the desired line break style, even if the input mixes line break styles.
 */
public class EndOfLineCorrectingInputStreamTest
{
	private static final List<String>	LINES		= IntStream.range(1, 100)
														.mapToObj(i -> BigInteger.valueOf(i).isProbablePrime(10) ? "" : "Line " + i)
														.collect(Collectors.toList());
	private static final List<String>	LINE_BREAKS	= ImmutableList.of("\n", "\r\n", "\r");

	@ParameterizedTest(name = "target line breaks: {0}")
	@MethodSource("getLineBreakStyles")
	public void testLineEndings(LineBreakStyle lineBreakStyle) {
		Random random = new Random(3092372898238479L);
		String input = joinLines(() -> LINE_BREAKS.get(random.nextInt(3)));
		String actual = convertLineBreaks(input, lineBreakStyle);
		String expectedLineBreak = getLineBreak(lineBreakStyle);
		String expected = joinLines(() -> expectedLineBreak);
		int numCharacters = Math.max(actual.length(), expected.length());
		for (int i = 0; i < numCharacters; i++) {
			char actualChar = charAt(actual, i);
			char expectedChar = charAt(expected, i);
			if (actualChar != expectedChar) {
				Assertions.fail("Converting line breaks to " + lineBreakStyle + " style failed. The results differ at index " + i + ": "
					+ "expected: " + formatChar(expectedChar)
					+ ", actual: " + formatChar(actualChar));
			}
		}
	}

	static Object[] getLineBreakStyles() {
		return LineBreakStyle.values();
	}

	private static String joinLines(Supplier<String> lineBreakSupplier) {
		StringBuilder resultBuilder = new StringBuilder();
		String lastLineBreak = null;
		for (String line : LINES) {
			String lineBreak;
			do {
				lineBreak = lineBreakSupplier.get();
				// avoid Mac line break + "" + Unix line break because this will be considered a single Windows line break
			} while (line.isEmpty()  && "\r".equals(lastLineBreak) && "\n".equals(lineBreak));
			resultBuilder.append(line);
			resultBuilder.append(lineBreak);
			lastLineBreak = lineBreak;
		}
		return resultBuilder.toString();
	}

	private static String convertLineBreaks(String input, LineBreakStyle lineBreakStyle) {
		Charset charset = StandardCharsets.UTF_8;
		byte[] inputBytes = input.getBytes(charset);
		InputStream inputStream = new ByteArrayInputStream(inputBytes);
		StringBuilder resultBuilder = new StringBuilder();
		try (InputStream convertedInputStream = new EndOfLineCorrectingInputStream(inputStream, lineBreakStyle);
			 InputStreamReader reader = new InputStreamReader(convertedInputStream, charset)) {
			CharBuffer charBuffer = CharBuffer.allocate(1024);
			while (reader.read(charBuffer) >= 0) {
				charBuffer.flip();
				resultBuilder.append(charBuffer.toString());
				charBuffer.clear();
			}
		} catch (IOException e) {
			throw new IllegalStateException("Unexpected IOException: " + e, e);
		}
		return resultBuilder.toString();
	}

	private static String getLineBreak(LineBreakStyle lineBreakStyle) {
		switch (lineBreakStyle) {
			case UNIX:
				return "\n";
			case WINDOWS:
				return "\r\n";
			case MAC:
				return "\r";
			default:
				throw new UnsupportedOperationException("Unknown line break style: " + lineBreakStyle);
		}
	}

	private static char charAt(String s, int pos) {
		return 0 <= pos && pos < s.length() ? s.charAt(pos) : 0;
	}

	private static String formatChar(char c) {
		switch (c) {
			case '\r':
				return "\\r";
			case '\n':
				return "\\n";
			case 0:
				return "---";
			default:
				return String.valueOf(c);
		}
	}
}