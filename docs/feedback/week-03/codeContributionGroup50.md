# Code Contributions and Code Reviews

#### Focused Commits

Grade: Insufficient!

Feedback: The repository has accumulated a small amount of commits in the past week, the number of commits is a quite far from the norm. Note: I saw that you have lots of branches, but they were not merged to main. For the future, keep in mind that the commits should only affect a few files, and should mostly constitute a coherent change to the system. Try to avoid large commits. Most commit's messages concretely should summarise the change. Your source code should not have a lot of commented code. 


#### Isolation

Grade: Insufficient!

Feedback: The team does not have merge requests (with code) that can reviewed regarding this criterion. For the future, keep in mind that you should use seperate branches in order to isolate individual features during development. There should be a level of focus on each merge request, i.e. it should be clear what a merge request is about, it should concentrate on a feature. You should not commit code repeatedly, directly to main.


#### Reviewability

Grade: Insufficient!

Feedback: There are no merge requests that I can inspect. Keep in mind the following facts for the future: your MRs should have a clear title and description, that is directly tied to the changes of the MR. Your changes within a MR should relate to eachother. MRs should generally not contain formatting changes such as reorganising imports, commenting code, or refactoring code with spaces/tabs. MRs should not be too large, and should cover a small number of commits. You should usually merge a feature branch not too long after its creation. You should periodically merge all your changes to main.


#### Code Reviews

Grade: Insufficient

Feedback: I cannot quite give you feedback on this rubric section, as you did not review eachother's merge requests. I mentioned it in the meeting, please take the time to review the other's merge requests, by asking questions, giving feedback, suggesting changes. Whatever is said, must be constructive, of course. Remember to try to review MR in a timely manner. By doing these reviews, you can spot bugs early before merging a feature, you can actually learn by looking at how your teammates implement certain details, and this will lead to an iterative improvement of the codebase. For this, you obviously need merge requests.


#### Build Server

Grade: Sufficient!

Feedback: The team did not currently select the checkstyle rules. The team should push and commit more frequently. The build duration is reasonable. Builds did not fail often overall (good thing). For the future, try to go through the CI/CD pipeline locally, so you will not have to commit to "Fix pipeline" or "Fix checkstyle".