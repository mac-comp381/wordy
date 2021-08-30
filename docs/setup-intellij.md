# IntelliJ setup

## Configuring the project

- IntelliJ Community Edition will work just fine for this assignment; you do _not_ need the Ultimate Edition.
- Open the word project folder you just cloned in IntelliJ. There are two ways to do this:
  - Option 1: Drag the repository folder onto the IntelliJ app icon.
  - Option 2: Click the “Open” button on IntelliJ’s launch screen.
  - ⚠️ **Do not** use the “Get from VCS” button in IntelliJ, or you will end up with a second separate clone of the homework running around on your computer, causing yourself much confusion and grief.
- You will get an “Open or Import Project” dialog that asks whether you want to open as an Eclipse project or a Gradle project. **Choose “Grade project”** and click OK.
- If you get a prompt about trusting the project, choose **Trust project**.
- Tell IntelliJ to use Java 16 (or newer) to build your project:
  - Go to **File → Project structure…**.
  - Choose **SDKs** from the column on the left.
  - In the second column from the left, look for a Java SDK with version number 16 or newer. (It might just be called “16,” or it might have a name like “openjdk-16.”)
  - If you do see version 16 in the list, then:
      - Press the little `+` button at the top of the SDK list.
      - Choose **Download JDK**.
      - Make sure Version says **16** or newer.
      - Choose **AdoptOpenJDK** as the Vendor.
      - Click Download.
      - The new version should now show up in the list.
  - Still in the Project Structure dialog, in the far left column, choose **Project** (in the “Project Settings” group).
  - Look for the **Project SDK** setting.
  - In the drop-down menu for that setting, choose that Java SDK with version 16 or newer.
  - Make sure the **Java language level** is set to **16**.
  - Click OK. The project should now be ready to go.

## Running the code

- Run the tests:
  - In IntelliJ, right click the **test** folder in your project.
  - Choose **Run 'Tests in wordy.test…'**.
  - A test results panel should pop up, showing that WordyParserTest’s tests all passed (green ✔️), and `CompilerTest` and `InterpretedTest` are both ignored (grey ⦸).
    - If you do not see all the tests, make sure both the ✔️ and ⦸ icons above the Test Results are selected.
- Run the Wordy Playground:
  - At the top of your IntelliJ project window, there is a drop-down menu for selecting run configurations. (If you just ran the tests, it will show “Tests in 'wordy.test'.”) Select **Playground** from that drop-down menu.
  - Click the run button (green ▶︎).
  - You should get a window titled “Wordy IDE.”
- Note that there is also a ShaderUI run configuration, but it won’t work at all until you have implemented the Wordy interpreter later in this assignment.

If all this worked, you're ready to go! Proceed to [Part 0](0-starting-point.md).

If anything seems broken, ask for help in the class Slack channel. If there’s trouble, it’s probably not just you.
