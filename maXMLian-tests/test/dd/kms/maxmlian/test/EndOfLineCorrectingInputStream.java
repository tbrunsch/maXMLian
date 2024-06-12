package dd.kms.maxmlian.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;

class EndOfLineCorrectingInputStream extends InputStream
{
	private final InputStream		delegate;
	private final LineBreakStyle	lineBreakStyle;

	private final Queue<Integer>	nextCharacters	= new ArrayDeque<>();

	EndOfLineCorrectingInputStream(InputStream delegate, LineBreakStyle lineBreakStyle) {
		this.delegate = delegate;
		this.lineBreakStyle = lineBreakStyle;
	}

	@Override
	public int read() throws IOException {
		if (nextCharacters.isEmpty()) {
			readMoreCharacters();
		}
		return nextCharacters.isEmpty() ? -1 : nextCharacters.remove();
	}

	private void readMoreCharacters() throws IOException {
		int character = delegate.read();
		while (true) {
			switch (character) {
				case '\r': {
					character = delegate.read();
					addLineBreak();
					if (character == '\r') {
						// Handle follow-up line break in next iteration
						continue;
					} else if (character != '\n') {
						// Mac line break + additional character
						nextCharacters.add(character);
					}
					break;
				}
				case '\n': {
					// Unix line break
					addLineBreak();
					break;
				}
				default: {
					nextCharacters.add(character);
					break;
				}
			}
			// next iteration can only be reached with a "continue", which happens in the case "\r\r"
			return;
		}
	}

	private void addLineBreak() {
		switch (lineBreakStyle) {
			case UNIX:
				nextCharacters.add((int) '\n');
				break;
			case WINDOWS:
				nextCharacters.add((int) '\r');
				nextCharacters.add((int) '\n');
				break;
			case MAC:
				nextCharacters.add((int) '\r');
				break;
			default:
				throw new UnsupportedOperationException("Unsupported line break style: " + lineBreakStyle);
		}
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}
}
