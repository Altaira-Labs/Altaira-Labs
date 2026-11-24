package com.altaira.backend.integration.github;

/** Supplies an installation credential to server-side GitHub API calls. */
public interface GitHubInstallationTokenProvider {
    /**
     * Creates installation token.
     *
     * @return the resulting text
     */
    String createInstallationToken();
}
