# Command line setup

## Configuring the project

- [Install JDK 16](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=hotspot) if you do not already have it installed.
  - Double check that you are getting version 16.
  - Choose the HotSpot JVM.
- [Install Gradle](https://gradle.org/install/) if you don't already have it.
- `cd` into your wordy project directory.
- Run `gradle test`. You should see a test report showing 20-some test cases. The tests from CompilerTest and InterpreterTest should all say SKIPPED, and the tests from WordyParserTest should all say SUCCESS.
- Run `gradle playground`. You should get a window titled “Wordy IDE.”
- Note that there is also a ShaderUI entry point, which you can run with `gradle shader`, but it won’t work at all until you have implemented the Wordy interpreter later in this assignment.

If all this worked, you're ready to go! Proceed to [Part 0](0-starting-point.md).

If anything seems broken, ask for help in the class Slack channel. If there’s trouble, it’s probably not just you.
