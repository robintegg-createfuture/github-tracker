package dev.cf.ai.gt;

import java.net.URL;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class RepoTracker {

	private final ChatClient chatClient;
	private final RepoSummaryRepository repository;

	public RepoTracker(ChatClient.Builder chatClientBuilder,
			ToolCallbackProvider toolCallbackProvider,
			RepoSummaryRepository repository,
			MarkdownHelper markdownHelper) {
		this.chatClient = chatClientBuilder.defaultToolCallbacks(toolCallbackProvider)
				.defaultSystem("""
					You are an expert software engineer. Capable of understanding GitHub repositories and summarizing their recent activity.
					
					Tool selection guidance:
						1. Use 'list_*' tools for broad, simple retrieval and pagination of all items of a type (e.g., all issues, all PRs, all branches) with basic filtering.
						2. Use 'search_*' tools for targeted queries with specific criteria, keywords, or complex filters (e.g., issues with certain text, PRs by author, code containing functions).

					Context management:
						1. MUST Use pagination whenever possible with batches of 2 items. In order to manage context size effectively.
						2. Use minimal_output parameter set to true if the full information is not needed to accomplish a task.

					Tool usage guidance:
						1. For 'search_*' tools: Use separate 'sort' and 'order' parameters if available for sorting results - do not include 'sort:' syntax in query strings. Query strings should contain only search criteria (e.g., 'org:google language:python'), not sorting instructions. Always call 'get_me' first to understand current user permissions and context. ## Pull Requests
						2. For `list_pull_requests` tool: Must use `perPage' and 'page' parameters for pagination. Keep to small batch sizes like 1 or 2. Always filter by 'state' set to 'open' or 'closed' as needed.

					PR review workflow: Always use 'pull_request_review_write' with method 'create' to create a pending review, then 'add_comment_to_pending_review' to add comments, and finally 'pull_request_review_write' with method 'submit_pending' to submit the review for complex reviews with line-specific comments. ## Issues

					Check 'list_issue_types' first for organizations to use proper issue types. Use 'search_issues' before creating new issues to avoid duplicates. Always set 'state_reason' when closing issues.
					""")
				.defaultTools( new DateTimeTools() )
				.build();
		this.repository = repository;
	}

	public String addRepo(final URL repoUrl) {

		final RepoSummary repoSummary = generateRepoSummary( repoUrl );
		// Save to database
		RepoSummaryEntity entity = RepoSummaryEntity.fromRepoSummary(repoSummary);
		repository.save(entity);

		return repoSummary.repoPath();

	}


	public String refreshRepo(final String repoPath) {

		// Find existing entity
		final var optional = repository.findByRepoPath(repoPath);
		if (optional.isEmpty()) {
			throw new IllegalArgumentException("Repository not found: " + repoPath);
		}
		RepoSummaryEntity entity = optional.get();

		// Build URL from stored repoUrl string
		final URL repoUrl;
		try {
			repoUrl = new URL(entity.getRepoUrl());
		} catch (Exception e) {
			throw new RuntimeException("Invalid repo URL in database for " + repoPath, e);
		}

		RepoSummary repoSummary = generateRepoSummary( repoUrl );

		// Update entity and save (repository.save uses MERGE INTO to update updated_at)
		entity.setPrActivity(repoSummary.prActivity());
		entity.setPrSummary(repoSummary.prSummary());
		repository.save(entity);

		return repoPath;

	}

	// Result object for refreshAll
	public static class RefreshAllResult {
		private final int successCount;
		private final int failureCount;

		public RefreshAllResult(int successCount, int failureCount) {
			this.successCount = successCount;
			this.failureCount = failureCount;
		}

		public int getSuccessCount() {
			return successCount;
		}

		public int getFailureCount() {
			return failureCount;
		}
	}

	// Refresh all tracked repositories synchronously. Continues on per-repo failures and returns counts.
	public RefreshAllResult refreshAll() {
		int success = 0;
		int failure = 0;

		final var tracked = repository.findAll();
		for (RepoSummaryEntity entity : tracked) {
			try {
				final URL repoUrl = new URL(entity.getRepoUrl());
				RepoSummary updated = generateRepoSummary(repoUrl);
				entity.setPrActivity(updated.prActivity());
				entity.setPrSummary(updated.prSummary());
				repository.save(entity);
				success++;
			} catch (Exception e) {
				// log and continue
				System.err.println("Failed to refresh " + entity.getRepoPath() + ": " + e.getMessage());
				failure++;
			}
		}

		return new RefreshAllResult(success, failure);
	}

	private RepoSummary generateRepoSummary(final URL repoUrl) {
		String prompt = String.format("""
				Given a GitHub repository URL of %s, fetch a provide a concise summary of the last 7 days of merged pull requests.
				
				Merged pull requests will have a valid `merged_at` date. This date must be within the last 7 days from today.
				
				We are only interested in pull requests that have 'merged' into the main or master branches within the last 7 days from today.
				
				Each summary only needs to include from the PR data:
				* The title of the pull request
				* A brief summary of the changes made in the pull request
				* Links to any jira tickets referenced in the pull request 
				""",
				repoUrl);

		String prActivity = chatClient.prompt()
				.user(prompt)
				.call()
				.content();

		System.out.println("--------------------------------------------\n"
				+ "-----------------------------------------\n"
				+ "\n"
				+ "PR Activity: " + prActivity);

		String prSummary = chatClient.prompt()
				.user( """
						Given a set of merged pull request summaries with titles, descriptions and links, provide a SINGLE concise summary of the overall changes made in these pull requests. This should be a short paragraph covering the main themes and highlights.
						
						Focus on the key features, bug fixes, and improvements that have been implemented. 
						
						The summary should be high-level and capture the essence of the changes without going into too much detail. DO NOT to mention every PR individually or include links.
						
						Focus on a succinct summary of the overall changes made in these pull requests.  Keep it brief and to the point.
						
						The tone can be professional yet approachable, suitable for a team update or release notes.
						
						This is the list of PRs to summarize:
						%s
						""".formatted( prActivity ) )
				.call()
				.content();

		final String path = repoUrl.getPath();
		// remove any trailing /
		final String cleanedPath = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;

		return new RepoSummary(cleanedPath, repoUrl, prActivity, prSummary);
	}


	public RepoSummary getSummary(final String repoPath) {
		return repository.findByRepoPath(repoPath)
				.map(RepoSummaryEntity::toRepoSummary)
				.orElse(null);
	}

	public java.util.List<RepoSummaryEntity> getTrackedRepos() {
		return repository.findAll();
	}

}
