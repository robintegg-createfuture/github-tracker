package dev.cf.ai.gt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MarkdownHelperTest {

	private final MarkdownHelper markdownHelper = new MarkdownHelper();

	@Test
	void testPlainTextConversion() {
		String input = "This is plain text";
		String result = markdownHelper.toSafeHtml(input);
		assertTrue(result.contains("This is plain text"));
	}

	@Test
	void testMarkdownHeadingConversion() {
		String input = "# Heading 1\n## Heading 2";
		String result = markdownHelper.toSafeHtml(input);
		assertTrue(result.contains("<h1>"));
		assertTrue(result.contains("<h2>"));
		assertTrue(result.contains("Heading 1"));
		assertTrue(result.contains("Heading 2"));
	}

	@Test
	void testMarkdownListConversion() {
		String input = "- Item 1\n- Item 2\n- Item 3";
		String result = markdownHelper.toSafeHtml(input);
		assertTrue(result.contains("<ul>"));
		assertTrue(result.contains("<li>"));
		assertTrue(result.contains("Item 1"));
	}

	@Test
	void testMarkdownCodeConversion() {
		String input = "`inline code`";
		String result = markdownHelper.toSafeHtml(input);
		assertTrue(result.contains("<code>"));
		assertTrue(result.contains("inline code"));
	}

	@Test
	void testMarkdownLinkConversion() {
		String input = "[GitHub](https://github.com)";
		String result = markdownHelper.toSafeHtml(input);
		assertTrue(result.contains("<a"));
		assertTrue(result.contains("href=\"https://github.com\""));
		assertTrue(result.contains("GitHub"));
	}

	@Test
	void testXssSanitization() {
		String input = "<script>alert('xss')</script>";
		String result = markdownHelper.toSafeHtml(input);
		assertFalse(result.contains("<script>"));
		assertFalse(result.contains("alert"));
	}

	@Test
	void testEmptyInput() {
		String result = markdownHelper.toSafeHtml("");
		assertEquals("", result);
	}

	@Test
	void testNullInput() {
		String result = markdownHelper.toSafeHtml(null);
		assertEquals("", result);
	}
}

