DIGITAL LIBRARY MANAGEMENT SYSTEM
=================================

TECHNOLOGY
----------
- Core Java
- Java Swing (desktop graphical interface)
- Java Serialization and file handling
- Notepad (or any plain-text editor)
- Command Prompt
- No HTML, CSS, JavaScript, MySQL, JDBC, or external libraries

FILES
-----
DigitalLibrary.java   Main Java source file
data/library.dat      Created automatically when the program first runs

REQUIREMENT
-----------
Install a JDK (Java Development Kit). Open Command Prompt and check:

    java -version
    javac -version

Both commands should display a version. If javac is not recognized,
install a JDK and configure its bin folder in the Windows PATH.

INSTALL / SAVE
--------------
1. Create a folder on your Desktop named DigitalLibrary.
2. Save DigitalLibrary.java inside that folder.
3. In Notepad, choose Save as type: All files.
4. Make sure the filename is exactly DigitalLibrary.java, not
   DigitalLibrary.java.txt.
5. Use UTF-8 encoding if Notepad asks.

COMPILE AND RUN
--------------
Open Command Prompt and type:

    cd /d "%USERPROFILE%\Desktop\DigitalLibrary"
    javac DigitalLibrary.java
    java DigitalLibrary

The first command changes to the project folder.
The second compiles the Java source.
The third starts the desktop application.

DEFAULT DEMO LOGIN
------------------
Admin:
    Username: admin
    Password: admin123

Student:
    Username: student
    Password: student123

IMPORTANT: This is a classroom demonstration login system. The demo
credentials are hard-coded in the source and are not secure for a
real production application.

FEATURES
--------
ADMIN
- View dashboard statistics
- Add books
- Update book details
- Delete books (not while active issues exist)
- Search books by ID, title, author, or category
- Issue books to a student username
- Record book returns
- View transaction history

STUDENT
- Browse and search the book catalogue
- View their own issue and return records

DATA STORAGE
------------
The program creates a folder named "data" beside the Java source/class
files when run from the project folder. It stores books and issue records
in data/library.dat using Java object serialization.

Do not delete data/library.dat if you want to retain your records.
Keep the data folder with the program when moving it to another computer.
The application should be closed before manually copying the data file.

CUSTOMIZATION
-------------
1. Login credentials:
   Open DigitalLibrary.java and find the login() method.
   Change the admin and student usernames/passwords there.

2. Theme:
   Near the top of the file, find the CUSTOMIZABLE SETTINGS section.
   Change NAVY, NAVY_LIGHT, PANEL, ACCENT, TEXT, and other Color values.

3. Application title:
   Change APP_TITLE near the top.

4. Sample books:
   Find loadData(). The five sample Book entries are added only when
   the saved library has no books. Edit these entries if desired.

5. Adding another student account:
   The starter version has one demo student account. To add multiple
   students, extend the login logic and store student accounts in
   LibraryData. In the current version, the admin can issue a book to
   a student username, and the demo student can view records issued
   to "student".

VIVA CONCEPTS
-------------
- Class and object: Book, IssueRecord, LibraryData, DigitalLibrary
- Encapsulation: data is organized in model classes; you can improve
  access control further with private fields and getters/setters.
- Inheritance: DigitalLibrary extends JFrame.
- Polymorphism: Swing event listeners use interface-based callbacks;
  overridden methods are used in table models.
- Exception handling: try/catch handles file and input errors.
- Collections: ArrayList stores books and issue records.
- File handling: File, FileInputStream, FileOutputStream.
- Serialization: ObjectOutputStream and ObjectInputStream.
- Event handling: ActionListener handles button actions.
- GUI: JFrame, JPanel, JButton, JTextField, JTable, JScrollPane.

TROUBLESHOOTING
---------------
If "javac is not recognized":
- A JDK may not be installed or its bin folder may not be in PATH.
- Install a JDK, reopen Command Prompt, and check javac -version.

If "file not found":
- Check that Command Prompt is inside the DigitalLibrary folder.
- Run: dir
- Confirm DigitalLibrary.java is listed.

If "class DigitalLibrary is public, should be declared in a file named
DigitalLibrary.java":
- Rename the source file exactly DigitalLibrary.java.

If the application opens but data appears missing:
- Make sure you are running from the same project folder and that
  data/library.dat is still present.

SCOPE / LIMITATIONS
-------------------
- This is a local desktop demo, not a networked multi-user system.
- The starter version uses one built-in student demo account.
- Student registration, fines, due-date reminders, password encryption,
  and multi-computer synchronization are not included.
- Java serialization is suitable for a small educational project.
  Do not load serialized files from untrusted sources.
