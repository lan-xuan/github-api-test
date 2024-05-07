import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.File;
import java.util.Collection;

/**
 * @From: Jeffrey
 * @Date: 2024/4/17
 */
public class Test {
    static String folder = "D:\\CDP\\DigitalCapture\\simple_site";
    public static void main(String[] args) throws Exception{
        GitHub github = new GitHubBuilder().withOAuthToken("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").build();
        GHRepository repository = github.getRepository("lan-xuan/github-api-test");

        //get branch reference
        GHRef ref = repository.getRef("heads/main");
        String lastCommitSha = ref.getObject().getSha();
        System.out.println(lastCommitSha);

        //get last commit
        GHCommit lastCommit = repository.getCommit(lastCommitSha);
        String lastTreeSha = lastCommit.getTree().getSha();
        System.out.println(lastTreeSha);

        //creat tree (input stream to byte array)
        GHTreeBuilder treeBuilder = repository.createTree().baseTree(lastTreeSha);
        inputStream2ByteArray(treeBuilder, folder);
        GHTree tree = treeBuilder.create();
        System.out.println(tree.getSha());

        //create commit
        GHCommit commit = repository.createCommit()
                .parent(lastCommitSha)
                .message("test api5")
                .tree(tree.getSha())
                .create();
        System.out.println(commit.getSHA1());
        //update branch reference
        ref.updateTo(commit.getSHA1());

        //create PR
//        repository.createPullRequest("test PR", "dev", "main", "test body");

    }

    private static void inputStream2ByteArray(GHTreeBuilder treeBuilder, String folder) throws Exception{
        File dir = new File(folder);
        Collection<File> files = FileUtils.listFiles(dir, null, true);
        for (File file : files) {
            String parentPath = folder.substring(0, folder.lastIndexOf("\\"));
            String path = file.getAbsolutePath().substring(parentPath.length() + 1).replaceAll("\\\\", "/");
            System.out.println(path);
            treeBuilder.add(path, FileUtils.readFileToByteArray(file), true);
        }
    }
}
