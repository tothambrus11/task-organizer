# Code Contributions and Code Reviews

#### Focused Commits

Grade: Very good!

Feedback: The repository has a good amount of commits. Most commits only affect a small number of files and constitute a coherent change to the system. Most commit messages are concise and describe the change clearly. Some commits still have a large size and affect multiple files (https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-50/-/commit/9420b279dd4e397fda749c2dc1fff01e97526253 and https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-50/-/commit/8a1db0eec80dc7b6019299291dfa92c1530e4a82). Some commits have a vague message (https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-50/-/commit/9420b279dd4e397fda749c2dc1fff01e97526253). 


#### Isolation

Grade: Very good!

Feedback: The group uses feature branches/merge requests to isolate individual features during development, which is great! The degree of focus on each merge request is quite high, I managed to find one very large merge request (https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-50/-/merge_requests/25/diffs), maybe this should have been multiple merge requests.

#### Reviewability

Grade: Very good!

Feedback: Most merge requests have a clear focus that becomes clear from the title and/or description. Most merge requests contain a description, but sometimes that description is too shallow (https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-50/-/merge_requests/17 and https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-50/-/merge_requests/26). Merge requests contain code changes and only a low number of formatting changes. Merge requests cover a small amount of commits, this is great! The changes is MR's are coherent and relate to eachother. Merges happen usually not after a long time after the creation of the branch.


#### Code Reviews

Grade: Good!

Feedback: Code reviews on gitlab actually happened as a back and forth discussion, and this lead to iterative improvements of the code. However, I see that only a few of you actively participate in code reviews, and I want to reiterate that everyone should be active in code reviews! Just accepting a merge request directly is not enough. Comments in the MRs are constructive and goal oriented. Some merge requests did not receive reviews (https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-50/-/merge_requests/19), and some merge requests were directly merged(https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-50/-/merge_requests/19 and https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-50/-/merge_requests/17). This is not a good practice.

#### Build Server

Grade: Very good!

Feedback: The team has selected enough checkstyle rules. Build times are very reasonable. Failing builds are most often fixed directly after them failing, which is great! Builds don't fail on main, well done! The team also has commited and pushed frequently. However, I noticed a lot of commits that are related to fixing CI pipelines, which is not that great (I encountered a streak of ~15 pipeline fix commits in a row). I would recommend checking your code locally before pushing (checking it builds, that checkstyle passes, that tests pass).