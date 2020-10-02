
# BWReportProblem

## Version

  0.0.4



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
	           implementation 'com.github.bluewhaleappsinc:BWReportProblem:0.0.4'
	}
```


## How to use the library?

```kotlin
    button.setOnClickListener {

      IssueTracker.Builder(this, "test-project", "158")  // project id/name
                    .withUserName("userName")
                    .withEnviroment("staging")
                    .withBuildVesion("${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
                    .build()
                    .start()

        }

```

## ISSUE?

If you facing manifest merge issue related

```
        <meta-data
          android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths" />

```

use tools:replace="android:resource" in your main project
```
     <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/filepaths"
                tools:replace="android:resource" />

```


