package dev.cf.ai.gt;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class MarkdownHelper {

	private final Parser parser;
	private final HtmlRenderer renderer;

	public MarkdownHelper() {
		this.parser = Parser.builder().build();
		this.renderer = HtmlRenderer.builder().build();
	}

	/**
	 * Converts markdown content to safe HTML.
	 * Always converts the input (handles plain text safely).
	 * Sanitizes output using jsoup's relaxed safelist to prevent XSS attacks.
	 *
	 * @param markdown the markdown content to convert
	 * @return sanitized HTML string
	 */
	public String toSafeHtml(String markdown) {
		if (markdown == null || markdown.isEmpty()) {
			return "";
		}

		// Parse markdown to AST
		Node document = parser.parse(markdown);

		// Render to HTML
		String html = renderer.render(document);

		// Sanitize HTML using relaxed safelist (allows common formatting tags)
		return Jsoup.clean(html, Safelist.relaxed());
	}
}

