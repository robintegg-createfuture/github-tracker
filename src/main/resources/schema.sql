CREATE TABLE IF NOT EXISTS repo_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    repo_path VARCHAR(500) NOT NULL UNIQUE,
    repo_url VARCHAR(500) NOT NULL,
    pr_activity TEXT NOT NULL,
    pr_summary TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_repo_path ON repo_summary(repo_path);

