package nebula.plugin.stash.tasks

import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault
class AddBuildStatusTask extends StashTask {
    @Input String buildState
    @Input String buildKey
    @Input String buildName
    @Input String buildUrl
    @Input String buildDescription
    @Input @Optional String buildCommit
    
    /**
     * Find the hash of the current commit in your current working directory
     * @return The commit hash if found, Null if not
     */
    private String getCurrentCommit() {
        logger.info("getting the sha for the HEAD of the current directory")
        String currentSha = cmd.execute("git rev-parse HEAD", System.getProperty("user.dir"))
        logger.info("currentSha : ${currentSha}")
        currentSha
    }
    
    @Override
    void executeStashCommand() {
        String commit
        
        if(buildCommit) {
            commit = buildCommit
        } else {
            logger.info("finding commit")
            commit = getCurrentCommit()
            if(!commit) {
                throw new GradleException("unable to determine the commit hash")
            }
        }
        logger.info("using commit : ${commit}")
        stash.postBuildStatus(commit, [state:buildState, key:buildKey, name:buildName, url:buildUrl, description:buildDescription])
    } 
}