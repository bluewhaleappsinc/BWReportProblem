
# BWReportProblem

## Version

 1.0.0



## How to integrate into your app?
A few changes in the build gradle and your all ready to use the library. Make the following changes.

Step 1. Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```java
allprojects {
  repositories {
    ...
    maven { url "https://jitpack.io" }
  }
}
```
Step 2. Add the dependency, Include this in your Module-level build.gradle file

```kotlin
dependencies {
	        implementation 'com.github.socialUltimus:Social_Login:0.0.1'
	}
```


## How to use the library?

```kotlin
    button.setOnClickListener {
            IssueTracker.Builder(this)
                .withProjectName("test-project") // project id/name
                .build()
                .start()

        }

```



