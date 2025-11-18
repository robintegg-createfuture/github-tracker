package dev.cf.ai.gt;

import java.net.URI;
import java.net.http.HttpRequest;

import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import io.modelcontextprotocol.common.McpTransportContext;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class GithubTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubTrackerApplication.class, args);
	}

	@Bean
	McpSyncClientCustomizer mcpSyncClientCustomizer() {
		return ( name, spec ) -> log.debug( "McpSyncClientCustomizer customizer name: {}", name );
	}

	@Bean
	McpSyncHttpClientRequestCustomizer mcpSyncHttpClientRequestCustomizer(
			@Value( "${github.mcp.authorization.token}" ) String githubPATToken
	) {
		return ( builder, method, endpoint, body, context ) -> {
			log.debug( "McpSyncHttpClientRequestCustomizer customizer name: {}", method );
			builder.header( "Authorization", githubPATToken );
			builder.header( "X-MCP-Readonly", "true" );
		};
	}

}
