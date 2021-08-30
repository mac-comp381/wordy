# Visual Studio Code

## Configuring the project

- Make sure you have installed the Java language extension for VS Code. If you used VS Code in COMP 127 or 128, you should already have it. But if you don’t have it, or aren’t sure whether you do:
  - Choose **File → New Window** if VS Code does not already have a window open.
  - In the far left toolbar of the VS Code window, click the **Extensions** icon (three little squares connected in an L shape, with a detached fourth square).
  - In the “Search Extensions in Marketplace” box in the upper left, type **java**.
  - The “Language Support for Java(TM) by Red Hat” should show up, probably as the first search result. **Select it.**
  - If there is an **Install** button, then click it. If there is instead an **Uninstall** button, you already have it installed. Yay!
- Open the wordy project folder you just cloned in VS Code. There are two ways to do this:
  - Option 1: Drag the cloned wordy folder onto the VS Code app icon.
  - Option 2: In VS Code, choose File → Open….
- VS Code may take some time importing / updating / building the project. If you see an “Opening Java project” message, wait for it finish.
- Look at the blue status bar at the very bottom in the VS Code window. Almost all the way to the right, you should (eventually) see the word “Java,” with a thumbs-up icon next to it.
  - If you do not see the word “Java,” either the Java extension didn’t get installed properly, or the project didn’t open properly. Contact the instructor for help.
  - If you see the word “Java” but there is a spinning two-arrows-in-a-circle icon, give it a minute. If it keeps spinning forever, then press cmd-shift-P (Mac) / ctrl-shift-P (Windows/Linux), search for the “Clean Java langauge server workspace” command, run it, and choose “Restart and delete” in the dialog box that appears. If you _still_ don’t eventually get that thumbs-up icon next to “Java”, then contact the instructor for help.

## Running the code

- Run the tests:
  - In your VS Code project, select the **Testing icon** in the toolbar on the far left. (The icon looks like a chemistry lab beaker.)
  - At the very top of the TESTING panel that appears, click the **Run all tests** button (looks like two triangles stacked on top of each other).
  - Test results should appear, showing that WordyParserTest’s tests all passed (green ✔️⃝), and `CompilerTest` and `InterpretedTest` are both skipped (grey hollow circles).
- Run the Wordy Playground:
  - Choose the **Run/Debug icon** in the toolbar on the far left.
  - At the very top of the run panel that appears, next to the words RUN AND DEBUG, there is a drop-down menu that lets you choose what to run. From that drop-down, select **Launch Playground**.
  - Click the **run button** next to that drop-down (green triangle).
  - You should get a window titled “Wordy IDE.”
- Note that there is also a ShaderUI in that menu, but it won’t work at all until you have implemented the Wordy interpreter later in this assignment.

If all this worked, you're ready to go! Proceed to [Part 0](0-starting-point.md).

If anything seems broken, ask for help in the class Slack channel. If there’s trouble, it’s probably not just you.
