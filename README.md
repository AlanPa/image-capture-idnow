# Image Capture IdNow
## Presentation
This is an Android application designed to capture a picture, display a preview of it, and show a description fetched from a JSON API. No third-party libraries were used except for REST communication. I aimed to keep the app as simple as possible.

### Development Breakdown
#### Sub-tasks
Here are the sub-tasks that I would have specified in a real work project. I naturally followed about this organisation by creating feature and technical Git branches.

- Build: Initialize the project (architecture, libraries, CI/CD...)
- Feature: get quote (UI, service and tests)
- Feature: capture image (UI, service and tests)
- Build: Add app version

#### Time allocation
Here's how I allocated time for development:

- **1 hour** to plan the architecture and the design of the app
- **1 hour** for the API integration
- **2 hours** to capture and display the image
- **1 hour** to improve the code
- **1 hour** for testing
- **1 hour** to write this README

*Total time spent:* **7 hours**

## Technical Choices
### ```Retrofit``` for REST communication
Retrofit is a library that simplifies communication with RESTful APIs. It’s one of the most popular options, efficient, and easy to use. I’ve used it extensively in my projects and have never encountered any issues. Automatic JSON parsing with Gson is also extremely useful.

### ```PreviewView``` for displaying the camera preview
While I could have called the intent to directly open the camera app, I opted for PreviewView because it provides a better user experience.

## Architecture Choices
### MVVM & Clean Architecture
I used these two architectural patterns to enhance testability, maintainability, and clear separation between the UI, business logic, and data access layers. Clean architecture implements the SOLID principles. Although it might seem excessive for a small project, I found it a very good exercise to implement it.
I chose the MVVM pattern, beacause I almost exclusively used it in my previous projects, so I was the most confortable with it. An MVI pattern could have probably worked just as well.

## Potential Improvements
Given the time constraints and the limitation of not using third-party libraries, here are a few improvements I could have made with more time:

### Corrections
There are two things that bother me in my current implementation, but I couldn’t address them fully due to time constraints:

- When displaying the captured image in the ImageView, the image is rotated 90 degrees, so I had to manually rotate it in the ImageView. I’m unsure if this is a common issue or if I’m using ```BitmapFactory.decodeFile(photoFile.absolutePath)``` incorrectly.
- I would like to improve my UI tests. Currently, I still have to manually accept camera permissions.

### Improvements to the application
This app currently consists of only one screen and activity. I believe the user experience could be enhanced by:

- Better arrangement and styling of UI components (Take picture button styled to match the usual round design)
- Splitting in two screens thge image capture and the image preview with the download button
- Adding a button to retry the capture without downloading the previous photo
- Adding more logs
- Ensuring all errors are properly caught and displayed to the user

### Technical Improvements

#### Use Jetpack Compose for the UI
If I had the choice, I would have used Jetpack Compose instead of XML for the UI. Compose is a modern UI toolkit for Android with many advantages over XML:
- UIs are built in Kotlin, which eliminates a lot of boilerplate code
- It supports live previews and recomposition
- UI rendering is more efficient
- It’s now recommended by Google for Android development

Even though the benefits of Jetpack Compose are more noticeable in larger projects, I think it’s still worth learning and using in smaller projects like this one.

#### Use Hilt for Dependency Injection
Since no third-party libraries were to be used, I manually implemented the dependency injection. But it is very error-prone and a lot of boilerplate code has to be written.
In larger projects, Hilt can reduce boilerplate code and save development time by simplifying dependency injection management.