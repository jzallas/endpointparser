#### Summary
Simple endpoint parser that finds methods annotated with common retrofit annotations and attempts to make rudimentary documentation.

#### Usage
1. Add the `endpointparser-compiler` module to your project
1. Update `build.gradle` of the modules that you want to parse with the following:
    ```groovy
    android {
       defaultConfig {
           javaCompileOptions {
                annotationProcessorOptions {
                    arguments = ["endpointParser": "true"]
               }
           }
       }
    }
    
    dependencies {
       kapt project(":endpointparser-compiler")
    }
    ```
1. Compile your code
1. Look for the generated documentation in the following directory:
    ```
    build/tmp/kapt3/classes/debug/com/jzallas/endpointparser/EndpointProcessor/results.json
    ```
    
#### Example
The following was generated from the attached sample app:
```json
[
  {
    "httpMethod": "GET",
    "endpoint": "users/{user}/repos",
    "methodName": "listRepos",
    "className": "com.jzallas.endpointparser.GithubService",
    "parameters": [
      {
        "type": "java.lang.String",
        "name": "user"
      }
    ],
    "result": "retrofit2.Call<java.util.List<com.jzallas.endpointparser.Repo>>"
  }
]
```