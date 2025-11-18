package dev.cf.ai.gt;

import java.net.URL;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RepoTrackerController {

    private final MarkdownHelper markdownHelper;
    private final RepoTracker repoTracker;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("trackedRepos", repoTracker.getTrackedRepos());
		model.addAttribute("markdownHelper", markdownHelper);
        return "index";
    }

    @PostMapping("/track")
    public String track(@RequestParam("repoUrl") URL repoUrl) {
        String repoPath = repoTracker.addRepo(repoUrl);
        return "redirect:/track" + repoPath;
    }

    // Refresh an existing tracked repository: regenerate summaries and update DB
    @PostMapping("/refresh/{*repoPath}")
    public String refreshRepo(@PathVariable("repoPath") String repoPath, RedirectAttributes redirectAttrs) {
        try {
            repoTracker.refreshRepo(repoPath);
            redirectAttrs.addFlashAttribute("message", "Repository refreshed successfully");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", "Repository not found: " + repoPath);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Failed to refresh repository: " + e.getMessage());
        }
        return "redirect:/track" + repoPath;
    }

    @GetMapping("/track/{*repoPath}")
    public String trackRepo(@PathVariable("repoPath") String repoPath, Model model) {
        final RepoSummary repoSummary = repoTracker.getSummary(repoPath);
        model.addAttribute("repo", repoPath);
        model.addAttribute("prActivity", repoSummary != null ? repoSummary.prActivity() : "");
        model.addAttribute("prSummary", repoSummary != null ? repoSummary.prSummary() : "");
        model.addAttribute("markdownHelper", markdownHelper);
        return "repo-tracker";
    }

	@PostMapping("/refresh-all")
	public String refreshAll(RedirectAttributes redirectAttrs) {
		try {
			RepoTracker.RefreshAllResult result = repoTracker.refreshAll();
			String message = String.format("Refreshed %d repos, %d failed", result.getSuccessCount(), result.getFailureCount());
			redirectAttrs.addFlashAttribute("message", message);
		} catch (Exception e) {
			redirectAttrs.addFlashAttribute("error", "Failed to refresh all repositories: " + e.getMessage());
		}
		return "redirect:/";
	}

}
