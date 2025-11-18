package dev.cf.ai.gt;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

public class RepoSummaryEntity {
    private Long id;
    private String repoPath;
    private String repoUrl;
    private String prActivity;
	private String prSummary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RepoSummaryEntity() {
    }

    public RepoSummaryEntity(String repoPath, String repoUrl, String prActivity, String prSummary ) {
        this.repoPath = repoPath;
        this.repoUrl = repoUrl;
        this.prActivity = prActivity;
		this.prSummary = prSummary;
    }

    public static RepoSummaryEntity fromRepoSummary(RepoSummary repoSummary) {
        return new RepoSummaryEntity(
            repoSummary.repoPath(),
            repoSummary.repoUrl().toString(),
            repoSummary.prActivity(),
			repoSummary.prSummary()
        );
    }

    public RepoSummary toRepoSummary() {
        try {
            return new RepoSummary(repoPath, new URL(repoUrl), prActivity, prSummary );
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL stored in database: " + repoUrl, e);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRepoPath() {
        return repoPath;
    }

    public void setRepoPath(String repoPath) {
        this.repoPath = repoPath;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getPrActivity() {
        return prActivity;
    }

    public void setPrActivity(String prActivity ) {
        this.prActivity = prActivity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

	public String getPrSummary() {
		return prSummary;
	}

	public void setPrSummary(String prSummary) {
		this.prSummary = prSummary;
	}
}

