package nebula.plugin.stash

import nebula.plugin.stash.tasks.StashTask
import nebula.test.ProjectSpec
import org.gradle.api.Task

class StashRestPluginTest extends ProjectSpec {
    def setup() {
        project.apply plugin: 'com.netflix.nebula.gradle-stash'
        assert project.plugins.hasPlugin(StashRestBasePlugin)
    }

    def "Creates custom extension with default values"() {
        expect:
        StashPluginExtension extension = project.extensions.findByName(StashRestPlugin.EXTENSION_NAME)
        extension != null
        extension.stashRepo == null
        extension.stashProject == null
        extension.stashHost == null
        extension.stashUser == null
        extension.stashPassword == null
    }

    def "Creates default tasks"() {
        expect:
        Task mergeBuiltPullRequestsTask = findTask('mergeBuiltPullRequests')
        mergeBuiltPullRequestsTask.description == 'Any pending Pull Request that has been built prior will be merged or declined automatically.'
        Task syncNextPullRequestTask = findTask('syncNextPullRequest')
        syncNextPullRequestTask.description == 'Update a git directory to the branch where the next pull request originates from and apply any merge from master as necessary.'
        Task closePullRequestTask = findTask('closePullRequest')
        closePullRequestTask.description == 'After a build this task should be run to apply comments and merge the pull request.'
        Task addBuildStatusTask = findTask('addBuildStatus')
        addBuildStatusTask.description == 'Add a build status to a commit.'
        Task postPullRequestTask = findTask('postPullRequest')
        postPullRequestTask.description == 'Post a new pull request.'
        Task mergeBranchTask = findTask('mergeBranch')
        mergeBranchTask.description == 'Merge any changes from one branch into another.'
    }

    def "Can use extension to set task properties"() {
        given:
        String givenStashRepo = 'myRepo'
        String givenStashProject = 'example'
        String givenStashHost = 'mytesthost'
        String givenStashUser = 'foobar'
        String givenStashPassword = 'qwerty'

        when:
        project.stash {
            stashRepo = givenStashRepo
            stashProject = givenStashProject
            stashHost = givenStashHost
            stashUser = givenStashUser
            stashPassword = givenStashPassword
        }

        then:
        project.tasks.withType(StashTask) {
            assert stashRepo == givenStashRepo
            assert stashProject == givenStashProject
            assert stashHost == givenStashHost
            assert stashUser == givenStashUser
            assert stashPassword == givenStashPassword
        }
    }

    private Task findTask(String taskName) {
        project.tasks.findByName(taskName)
    }
}
