/**
 * ****************************************************************************
 *
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 *
 *
 * NOTICE: All information contained herein is, and remains the property of
 * FaEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye
 */
package com.fareyeconnect.tool.git;

import java.io.File;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

@ApplicationScoped
public class GitService {

    public static Git git = null;

    @PostConstruct
    public void cloneRepo() throws Exception {

        CredentialsProvider cp = new UsernamePasswordCredentialsProvider("ghp_write your token here",
                "");

        git = Git.cloneRepository() 
                .setCredentialsProvider(cp)
                .setURI("https://github.com/fareye-baldeep/setu-code.git")
                .setDirectory(new File("/tmp/test/setu-code"))
                .setBranchesToClone(Arrays.asList("refs/heads/main"))
                .setBranch("refs/heads/main")
                .call();
    }

    public void push() throws NoFilepatternException, GitAPIException {
        // Some code to update the file
        git.add().addFilepattern(".").call();

        git.commit().setMessage("File Commit").call();

        // Creating tag
        git.tag().setName("testbsk").setForceUpdate(true).call();

        // Pushing the commit and tag
        git.push().call();
    }
}
