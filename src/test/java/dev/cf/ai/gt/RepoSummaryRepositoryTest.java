package dev.cf.ai.gt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RepoSummaryRepositoryTest {

    @Autowired
    private RepoSummaryRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testSaveAndFindByRepoPath() throws Exception {
        // Create a test entity
        URL testUrl = new URL("https://github.com/test/repo");
        String testPath = "/test/repo";
        String testSummary = "<h1>Test Summary</h1><p>This is a test.</p>";
		String testPrSummary = "<p>PR Summary</p>";

        RepoSummaryEntity entity = new RepoSummaryEntity(testPath, testUrl.toString(), testSummary, testPrSummary);

        // Save to database
        repository.save(entity);

        // Retrieve from database
        Optional<RepoSummaryEntity> found = repository.findByRepoPath(testPath);

        // Verify
        assertTrue(found.isPresent());
        assertEquals(testPath, found.get().getRepoPath());
        assertEquals(testUrl.toString(), found.get().getRepoUrl());
        assertEquals(testSummary, found.get().getPrActivity());
        assertNotNull(found.get().getCreatedAt());
        assertNotNull(found.get().getUpdatedAt());

        // Clean up
        repository.deleteByRepoPath(testPath);
    }

    @Test
    void testSaveUpdatesExisting() throws Exception {
        // Create a test entity
        URL testUrl = new URL("https://github.com/update/test");
        String testPath = "/update/test";
        String originalSummary = "<p>Original prActivity</p>";
        String updatedSummary = "<p>Updated prActivity</p>";
		String testPrSummary = "<p>PR Summary</p>";

        RepoSummaryEntity entity1 = new RepoSummaryEntity(testPath, testUrl.toString(), originalSummary, testPrSummary);
        repository.save(entity1);

        // Update with new prActivity
        RepoSummaryEntity entity2 = new RepoSummaryEntity(testPath, testUrl.toString(), updatedSummary, testPrSummary);
        repository.save(entity2);

        // Verify update
        Optional<RepoSummaryEntity> found = repository.findByRepoPath(testPath);
        assertTrue(found.isPresent());
        assertEquals(updatedSummary, found.get().getPrActivity());

        // Clean up
        repository.deleteByRepoPath(testPath);
    }

    @Test
    void testEntityConversion() throws Exception {
		String repoPath = "/conversion/test";
		URL testUrl = new URL("https://github.com/conversion/test");
        String testSummary = "<h2>Conversion Test</h2>";
		String testPrSummary = "<p>PR Summary</p>";

        RepoSummary repoSummary = new RepoSummary(repoPath, testUrl, testSummary, testPrSummary);

        // Convert to entity
        RepoSummaryEntity entity = RepoSummaryEntity.fromRepoSummary(repoSummary);
        assertEquals(testUrl.getPath(), entity.getRepoPath());
        assertEquals(testUrl.toString(), entity.getRepoUrl());
        assertEquals(testSummary, entity.getPrActivity());

        // Save and retrieve
        repository.save(entity);
        Optional<RepoSummaryEntity> found = repository.findByRepoPath(testUrl.getPath());

        // Convert back to RepoSummary
        assertTrue(found.isPresent());
        RepoSummary converted = found.get().toRepoSummary();
        assertEquals(testUrl.toString(), converted.repoUrl().toString());
        assertEquals(testSummary, converted.prActivity());

        // Clean up
        repository.deleteByRepoPath(testUrl.getPath());
    }

    @Test
    void testExistsByRepoPath() throws Exception {
        URL testUrl = new URL("https://github.com/exists/test");
        String testPath = "/exists/test";

        // Should not exist initially
        assertFalse(repository.existsByRepoPath(testPath));

        // Save entity
        RepoSummaryEntity entity = new RepoSummaryEntity(testPath, testUrl.toString(), "<p>Test</p>", "<p>PR Summary</p>");
        repository.save(entity);

        // Should exist now
        assertTrue(repository.existsByRepoPath(testPath));

        // Clean up
        repository.deleteByRepoPath(testPath);

        // Should not exist after deletion
        assertFalse(repository.existsByRepoPath(testPath));
    }
}

