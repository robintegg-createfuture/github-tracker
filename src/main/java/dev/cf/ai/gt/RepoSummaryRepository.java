package dev.cf.ai.gt;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RepoSummaryRepository {

	private final JdbcTemplate jdbcTemplate;

	public RepoSummaryRepository( JdbcTemplate jdbcTemplate ) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private static final RowMapper<RepoSummaryEntity> ROW_MAPPER = ( rs, rowNum ) -> {
		RepoSummaryEntity entity = new RepoSummaryEntity();
		entity.setId( rs.getLong( "id" ) );
		entity.setRepoPath( rs.getString( "repo_path" ) );
		entity.setRepoUrl( rs.getString( "repo_url" ) );
		entity.setPrActivity( rs.getString( "pr_activity" ) );
		entity.setPrSummary( rs.getString( "pr_summary" ) );
		entity.setCreatedAt( rs.getTimestamp( "created_at" ).toLocalDateTime() );
		entity.setUpdatedAt( rs.getTimestamp( "updated_at" ).toLocalDateTime() );
		return entity;
	};

	public void save( RepoSummaryEntity entity ) {
		String sql = """
				MERGE INTO repo_summary (repo_path, repo_url, pr_activity, pr_summary, updated_at)
				KEY (repo_path)
				VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
				""";

		jdbcTemplate.update( sql, entity.getRepoPath(), entity.getRepoUrl(), entity.getPrActivity(),
				entity.getPrSummary() );
	}

	public Optional<RepoSummaryEntity> findByRepoPath( String repoPath ) {
		String sql = "SELECT * FROM repo_summary WHERE repo_path = ?";
		List<RepoSummaryEntity> results = jdbcTemplate.query( sql, ROW_MAPPER, repoPath );
		return results.isEmpty() ? Optional.empty() : Optional.of( results.get( 0 ) );
	}

	public List<RepoSummaryEntity> findAll() {
		String sql = "SELECT * FROM repo_summary ORDER BY updated_at DESC";
		return jdbcTemplate.query( sql, ROW_MAPPER );
	}

	public void deleteByRepoPath( String repoPath ) {
		String sql = "DELETE FROM repo_summary WHERE repo_path = ?";
		jdbcTemplate.update( sql, repoPath );
	}

	public boolean existsByRepoPath( String repoPath ) {
		String sql = "SELECT COUNT(*) FROM repo_summary WHERE repo_path = ?";
		Integer count = jdbcTemplate.queryForObject( sql, Integer.class, repoPath );
		return count != null && count > 0;
	}
}

