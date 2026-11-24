import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DigitalLibrary extends JFrame {

    private static final String APP_TITLE =
            "Digital Library Management System";

    private static final String DATA_DIR =
        System.getProperty("user.home")
        + File.separator + "Documents"
        + File.separator + "DigitalLibraryData";

private static final String DATA_FILE =
        DATA_DIR + File.separator + "library.dat";

    private static final Color NAVY = new Color(13, 27, 48);
    private static final Color NAVY_LIGHT = new Color(23, 43, 70);
    private static final Color PANEL = new Color(29, 48, 74);
    private static final Color ACCENT = new Color(40, 165, 225);
    private static final Color TEXT = new Color(240, 245, 252);
    private static final Color MUTED = new Color(185, 199, 216);
    private static final Color GREEN = new Color(35, 170, 120);
    private static final Color RED = new Color(210, 75, 85);
    private static final Color ORANGE = new Color(225, 155, 55);

    private static final Font NORMAL =
            new Font("Segoe UI", Font.PLAIN, 13);

    private static final Font BOLD =
            new Font("Segoe UI", Font.BOLD, 13);

    private LibraryData data = new LibraryData();

    private String currentUser = "";
    private boolean adminUser = false;

    private CardLayout rootCards;
    private JPanel root;

    private JTextField usernameField;
    private JPasswordField passwordField;

    // BOOK CLASS

    static class Book implements Serializable {

        private static final long serialVersionUID = 1L;

        String id, title, author, category;
        int quantity;

        Book(String id, String title, String author,
             String category, int quantity) {

            this.id = id;
            this.title = title;
            this.author = author;
            this.category = category;
            this.quantity = quantity;
        }
    }

    // ISSUE RECORD CLASS

    static class IssueRecord implements Serializable {

        private static final long serialVersionUID = 1L;

        String student, bookId, bookTitle;
        String issueDate, returnDate;

        boolean returned;

        IssueRecord(String student, Book book) {

            this.student = student;
            this.bookId = book.id;
            this.bookTitle = book.title;

            this.issueDate = today();
            this.returnDate = "-";

            this.returned = false;
        }
    }

    // LIBRARY DATA CLASS
 // STUDENT ACCOUNT CLASS
static class StudentAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    String username;
    String password;
    String fullName;

    StudentAccount(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }
}
    static class LibraryData implements Serializable {

        private static final long serialVersionUID = 1L;

        ArrayList<Book> books = new ArrayList<Book>();
        ArrayList<IssueRecord> issues =
                new ArrayList<IssueRecord>();
    }

    // CONSTRUCTOR

    public DigitalLibrary() {

        setTitle(APP_TITLE);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1100, 720);

        setMinimumSize(new Dimension(900, 620));

        setLocationRelativeTo(null);

        loadData();

        rootCards = new CardLayout();

        root = new JPanel(rootCards);

        root.setBackground(NAVY);

        root.add(createLoginPanel(), "LOGIN");

        setContentPane(root);

        rootCards.show(root, "LOGIN");
    }

    // CURRENT DATE

    private static String today() {

        return new SimpleDateFormat("dd-MMM-yyyy")
                .format(new Date());
    }

    // REMOVE EXTRA SPACES

    private String clean(String value) {

        return value == null ? "" : value.trim();
    }

    // LOAD DATA FROM FILE

    private void loadData() {

        File folder = new File(DATA_DIR);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(DATA_FILE);

        if (file.exists()) {

            try (ObjectInputStream in =
                    new ObjectInputStream(
                            new FileInputStream(file))) {

                Object saved = in.readObject();

                if (saved instanceof LibraryData) {
                    data = (LibraryData) saved;
                }

            } catch (Exception ex) {

                data = new LibraryData();

                JOptionPane.showMessageDialog(
                        this,
                        "Could not read saved data.\n"
                        + ex.getMessage(),
                        "Data warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }

        // SAMPLE BOOKS

        if (data.books.isEmpty()) {

            data.books.add(new Book(
                    "B101",
                    "Java: The Complete Reference",
                    "Herbert Schildt",
                    "Programming",
                    5
            ));

            data.books.add(new Book(
                    "B102",
                    "Data Structures and Algorithms",
                    "Narasimha Karumanchi",
                    "Computer Science",
                    4
            ));

            data.books.add(new Book(
                    "B103",
                    "Clean Code",
                    "Robert C. Martin",
                    "Programming",
                    3
            ));

            data.books.add(new Book(
                    "B104",
                    "Computer Networks",
                    "Andrew S. Tanenbaum",
                    "Networking",
                    2
            ));

            data.books.add(new Book(
                    "B105",
                    "Operating System Concepts",
                    "Silberschatz",
                    "Computer Science",
                    4
            ));

            saveData();
        }
    }

    // SAVE DATA
// SAVE DATA

    private void saveData() {

        File folder = new File(DATA_DIR);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        try (ObjectOutputStream out =
                new ObjectOutputStream(
                        new FileOutputStream(DATA_FILE))) {

            out.writeObject(data);

            exportBooksToCSV();

            System.out.println("Data saved successfully!");
            System.out.println("Storage location: "
                    + new File(DATA_FILE).getAbsolutePath());

        } catch (IOException ex) {

            message("Unable to save data: " + ex.getMessage());
        }
    }
    private void exportBooksToCSV() {

    File csvFile = new File(DATA_DIR, "books.csv");

    try (PrintWriter writer =
            new PrintWriter(new FileWriter(csvFile))) {

        writer.println(
            "Book ID,Title,Author,Category,Total Quantity,Available Copies"
        );

        for (Book b : data.books) {

            writer.println(
                "\"" + b.id + "\","
                + "\"" + b.title.replace("\"", "\"\"") + "\","
                + "\"" + b.author.replace("\"", "\"\"") + "\","
                + "\"" + b.category.replace("\"", "\"\"") + "\","
                + b.quantity + ","
                + availableCopies(b)
            );
        }

    } catch (IOException ex) {

        message("CSV export failed: " + ex.getMessage());
    }
}

    // FIND BOOK

    private Book findBook(String id) {

        for (Book b : data.books) {

            if (b.id.equalsIgnoreCase(clean(id))) {
                return b;
            }
        }

        return null;
    }

    // AVAILABLE COPIES

    private int availableCopies(Book book) {

        int issued = 0;

        for (IssueRecord r : data.issues) {

            if (!r.returned &&
                    r.bookId.equalsIgnoreCase(book.id)) {

                issued++;
            }
        }

        return Math.max(0, book.quantity - issued);
    }

    // LOGIN SCREEN

    private JPanel createLoginPanel() {

        JPanel background =
                new JPanel(new GridBagLayout());

        background.setBackground(NAVY);

        JPanel box = new JPanel();

        box.setPreferredSize(new Dimension(440, 500));

        box.setBackground(PANEL);

        box.setBorder(new CompoundBorder(
                new LineBorder(
                        new Color(65, 90, 120), 1, true),
                new EmptyBorder(28, 38, 28, 38)
        ));

        box.setLayout(
                new BoxLayout(box, BoxLayout.Y_AXIS)
        );

        JLabel icon =
                label("LIBRARY", 28, ACCENT, true);

        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading =
                label("DIGITAL LIBRARY", 24, TEXT, true);

        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle =
                label("Management System", 14, MUTED, false);

        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(icon);

        box.add(Box.createVerticalStrut(10));

        box.add(heading);

        box.add(Box.createVerticalStrut(5));

        box.add(subtitle);

        box.add(Box.createVerticalStrut(30));

        box.add(fieldLabel("USERNAME"));

        usernameField = new JTextField();

        styleField(usernameField);

        box.add(usernameField);

        box.add(Box.createVerticalStrut(16));

        box.add(fieldLabel("PASSWORD"));

        passwordField = new JPasswordField();

        styleField(passwordField);

        box.add(passwordField);

        box.add(Box.createVerticalStrut(24));

        JButton signIn = button("SIGN IN", ACCENT);

        signIn.setAlignmentX(Component.CENTER_ALIGNMENT);

        signIn.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44)
        );

        signIn.addActionListener(e -> login());

        box.add(signIn);

        box.add(Box.createVerticalStrut(22));

        JLabel hint = label(
                "<html><center>"
                + "DEMO ACCOUNTS<br>"
                + "Admin: admin / admin123<br>"
                + "Student: student / student123"
                + "</center></html>",
                12, MUTED, false
        );

        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(hint);

        passwordField.addActionListener(e -> login());

        background.add(box);

        return background;
    }

    // LOGIN VALIDATION

    private void login() {

        String username = clean(usernameField.getText());

        String password =
                new String(passwordField.getPassword());

        if (username.equals("admin")
                && password.equals("admin123")) {

            currentUser = "admin";
            adminUser = true;

            showDashboard();

        } else if (username.equals("student")
                && password.equals("student123")) {

            currentUser = "student";
            adminUser = false;
            showDashboard();

        } 
        else {

            JOptionPane.showMessageDialog(
                    this,
                    "Incorrect username or password.",
                    "Login failed",
                    JOptionPane.ERROR_MESSAGE
            );

            passwordField.setText("");
        }
    }

    // DASHBOARD

    private void showDashboard() {

        JPanel dashboard =
                new JPanel(new BorderLayout());

        dashboard.setBackground(NAVY);

        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(
                new Dimension(205, 0)
        );

        sidebar.setBackground(NAVY_LIGHT);

        sidebar.setBorder(
                new EmptyBorder(22, 14, 18, 14)
        );

        sidebar.setLayout(
                new BoxLayout(sidebar, BoxLayout.Y_AXIS)
        );

        JLabel brand =
                label("LIBRARY", 21, TEXT, true);

        brand.setBorder(
                new EmptyBorder(0, 8, 25, 0)
        );

        sidebar.add(brand);

        sidebar.add(label(
                adminUser ? "ADMIN PANEL" : "STUDENT PANEL",
                11, MUTED, true
        ));

        sidebar.add(Box.createVerticalStrut(14));

        CardLayout contentCards = new CardLayout();

        JPanel content = new JPanel(contentCards);

        content.setBackground(NAVY);

        addNavButton(sidebar, "Dashboard", () -> {

            refreshContent(content);

            contentCards.show(content, "HOME");
        });

        addNavButton(sidebar, "Book Catalogue", () -> {

            refreshContent(content);

            contentCards.show(content, "BOOKS");
        });

        addNavButton(sidebar,
                adminUser ? "Issue / Return" : "My Books",
                () -> {

                    refreshContent(content);

                    contentCards.show(content, "ISSUES");
                });

        sidebar.add(Box.createVerticalGlue());

        addNavButton(sidebar, "Logout", () -> {

            currentUser = "";

            usernameField.setText("");

            passwordField.setText("");

            rootCards.show(root, "LOGIN");
        });

        content.add(createHome(content), "HOME");

        content.add(createBooks(), "BOOKS");

        content.add(createIssues(), "ISSUES");

        JPanel topbar =
                new JPanel(new BorderLayout());

        topbar.setBackground(PANEL);

        topbar.setBorder(
                new EmptyBorder(16, 24, 16, 24)
        );

        topbar.add(
                label("Welcome, " + currentUser, 18, TEXT, true),
                BorderLayout.WEST
        );

        topbar.add(
                label(
                        adminUser ? "Administrator" : "Student account",
                        13, MUTED, false
                ),
                BorderLayout.EAST
        );

        dashboard.add(sidebar, BorderLayout.WEST);

        dashboard.add(topbar, BorderLayout.NORTH);

        dashboard.add(content, BorderLayout.CENTER);

        root.add(dashboard, "DASHBOARD");

        rootCards.show(root, "DASHBOARD");

        root.revalidate();

        root.repaint();
    }

    // REFRESH CONTENT

    private void refreshContent(JPanel content) {

        content.removeAll();

        content.add(createHome(content), "HOME");

        content.add(createBooks(), "BOOKS");

        content.add(createIssues(), "ISSUES");

        content.revalidate();

        content.repaint();
    }
    // NAVIGATION BUTTON

    private void addNavButton(
            JPanel sidebar, String text, Runnable action) {

        JButton b = button(text, NAVY_LIGHT);

        b.setHorizontalAlignment(SwingConstants.LEFT);

        b.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 43)
        );

        b.addActionListener(e -> action.run());

        sidebar.add(b);

        sidebar.add(Box.createVerticalStrut(7));
    }

    // HOME DASHBOARD

    private JPanel createHome(JPanel content) {

        JPanel page = page();

        page.add(
                pageTitle(
                        "Dashboard",
                        "Library summary and quick actions"
                ),
                BorderLayout.NORTH
        );

        int available = 0;

        int active = 0;

        for (Book b : data.books) {
            available += availableCopies(b);
        }

        for (IssueRecord r : data.issues) {
            if (!r.returned) active++;
        }

        JPanel stats =
                new JPanel(new GridLayout(1, 3, 14, 14));

        stats.setOpaque(false);

        stats.add(statCard(
                "BOOK TITLES",
                String.valueOf(data.books.size()),
                ACCENT
        ));

        stats.add(statCard(
                "AVAILABLE COPIES",
                String.valueOf(available),
                GREEN
        ));

        stats.add(statCard(
                "ACTIVE ISSUES",
                String.valueOf(active),
                ORANGE
        ));

        JPanel actions =
                new JPanel(new GridLayout(1, 2, 14, 14));

        actions.setOpaque(false);

        actions.add(actionCard(
                "Book Catalogue",
                "Browse and search library books",
                () -> showScreen(content, "BOOKS")
        ));

        actions.add(actionCard(
                adminUser ? "Issue / Return" : "My Books",
                adminUser
                        ? "Manage student book transactions"
                        : "View your book transactions",
                () -> showScreen(content, "ISSUES")
        ));

        JPanel body =
                new JPanel(new BorderLayout(15, 20));

        body.setOpaque(false);

        body.add(stats, BorderLayout.NORTH);

        body.add(actions, BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);

        return page;
    }

    // SHOW SELECTED SCREEN

    private void showScreen(JPanel content, String name) {

        refreshContent(content);

        CardLayout layout =
                (CardLayout) content.getLayout();

        layout.show(content, name);
    }

    // STATISTICS CARD

    private JPanel statCard(
            String heading, String value, Color accent) {

        JPanel p = new JPanel();

        p.setBackground(PANEL);

        p.setLayout(
                new BoxLayout(p, BoxLayout.Y_AXIS)
        );

        p.setBorder(new CompoundBorder(
                new LineBorder(
                        new Color(55, 77, 105), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        p.add(label(heading, 12, MUTED, true));

        p.add(Box.createVerticalStrut(12));

        p.add(label(value, 32, accent, true));

        return p;
    }

    // ACTION CARD

    private JPanel actionCard(
            String heading,
            String description,
            Runnable action) {

        JPanel p = new JPanel(
                new BorderLayout(8, 8)
        );

        p.setBackground(PANEL);

        p.setBorder(new CompoundBorder(
                new LineBorder(
                        new Color(55, 77, 105), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        p.add(
                label(heading, 17, TEXT, true),
                BorderLayout.NORTH
        );

        p.add(
                label(description, 12, MUTED, false),
                BorderLayout.CENTER
        );

        JButton open = button("OPEN", ACCENT);

        open.addActionListener(e -> action.run());

        p.add(open, BorderLayout.SOUTH);

        return p;
    }

    // BOOK CATALOGUE

    private JPanel createBooks() {

        JPanel page = page();

        page.add(
                pageTitle(
                        "Book Catalogue",
                        "Browse, search and manage books"
                ),
                BorderLayout.NORTH
        );

        JTextField search = new JTextField();

        styleField(search);

        search.setPreferredSize(
                new Dimension(280, 40)
        );

        JButton searchButton = button("Search", ACCENT);

        JButton clearButton = button("Clear", NAVY_LIGHT);

        JPanel searchBar =
                new JPanel(new FlowLayout(
                        FlowLayout.LEFT, 8, 0
                ));

        searchBar.setOpaque(false);

        searchBar.add(search);

        searchBar.add(searchButton);

        searchBar.add(clearButton);

        DefaultTableModel model =
                new DefaultTableModel(
                        new String[]{
                                "Book ID",
                                "Title",
                                "Author",
                                "Category",
                                "Available Copies"
                        }, 0) {

                    public boolean isCellEditable(
                            int row, int column) {
                        return false;
                    }
                };

        JTable table = createTable(model);

        JScrollPane scroll = new JScrollPane(table);

        styleScroll(scroll);

        Runnable fill = () -> {

            model.setRowCount(0);

            String q =
                    clean(search.getText()).toLowerCase();

            for (Book b : data.books) {

                if (b.id.toLowerCase().contains(q)
                        || b.title.toLowerCase().contains(q)
                        || b.author.toLowerCase().contains(q)
                        || b.category.toLowerCase().contains(q)) {

                    model.addRow(new Object[]{
                            b.id,
                            b.title,
                            b.author,
                            b.category,
                            availableCopies(b)
                    });
                }
            }
        };

        fill.run();

        searchButton.addActionListener(e -> fill.run());

        search.addActionListener(e -> fill.run());

        clearButton.addActionListener(e -> {

            search.setText("");

            fill.run();
        });

        JPanel center =
                new JPanel(new BorderLayout(10, 12));

        center.setOpaque(false);

        center.add(searchBar, BorderLayout.NORTH);

        center.add(scroll, BorderLayout.CENTER);

        if (adminUser) {

            JPanel actions =
                    new JPanel(new FlowLayout(
                            FlowLayout.LEFT, 8, 0
                    ));

            actions.setOpaque(false);

            JButton add = button("Add Book", GREEN);

            JButton edit = button(
                    "Update Selected", ACCENT
            );

            JButton delete = button(
                    "Delete Selected", RED
            );

            actions.add(add);

            actions.add(edit);

            actions.add(delete);

            center.add(actions, BorderLayout.SOUTH);

            // ADD BOOK

            add.addActionListener(e -> {

                if (bookDialog(null)) {
                    fill.run();
                }
            });

            // UPDATE BOOK

            edit.addActionListener(e -> {

                int row = table.getSelectedRow();

                if (row < 0) {

                    message("Select a book first.");

                    return;
                }

                row = table.convertRowIndexToModel(row);

                String id = String.valueOf(
                        model.getValueAt(row, 0)
                );

                Book selected = findBook(id);

                if (selected != null
                        && bookDialog(selected)) {

                    fill.run();
                }
            });

            // DELETE BOOK

            delete.addActionListener(e -> {

                int row = table.getSelectedRow();

                if (row < 0) {

                    message("Select a book first.");

                    return;
                }

                row = table.convertRowIndexToModel(row);

                String id = String.valueOf(
                        model.getValueAt(row, 0)
                );

                for (IssueRecord r : data.issues) {

                    if (!r.returned
                            && r.bookId.equalsIgnoreCase(id)) {

                        message(
                                "This book has an active issue. "
                                + "Return it before deleting."
                        );

                        return;
                    }
                }

                int confirm =
                        JOptionPane.showConfirmDialog(
                                this,
                                "Delete book " + id + "?",
                                "Confirm delete",
                                JOptionPane.YES_NO_OPTION
                        );

                if (confirm == JOptionPane.YES_OPTION) {

                    Book selected = findBook(id);

                    if (selected != null) {
                        data.books.remove(selected);
                    }

                    saveData();

                    fill.run();
                }
            });
        }

        page.add(center, BorderLayout.CENTER);

        return page;
    }

    // ADD / UPDATE BOOK DIALOG

    private boolean bookDialog(Book existing) {

        JTextField id = new JTextField(
                existing == null ? "" : existing.id
        );

        JTextField title = new JTextField(
                existing == null ? "" : existing.title
        );

        JTextField author = new JTextField(
                existing == null ? "" : existing.author
        );

        JTextField category = new JTextField(
                existing == null ? "" : existing.category
        );

        JTextField quantity = new JTextField(
                existing == null
                        ? "1"
                        : String.valueOf(existing.quantity)
        );

        JTextField[] fields = {
                id, title, author, category, quantity
        };

        for (JTextField f : fields) {
            styleField(f);
        }

        String[] names = {
                "Book ID",
                "Title",
                "Author",
                "Category",
                "Total quantity"
        };

        JPanel form = new JPanel(new GridBagLayout());

        form.setBackground(PANEL);

        form.setBorder(
                new EmptyBorder(15, 18, 15, 18)
        );

        form.setPreferredSize(
                new Dimension(510, 315)
        );

        GridBagConstraints g =
                new GridBagConstraints();

        g.insets = new Insets(9, 8, 9, 8);

        g.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < names.length; i++) {

            g.gridy = i;

            g.gridx = 0;

            g.weightx = 0.35;

            form.add(
                    label(names[i], 13, TEXT, true),
                    g
            );

            g.gridx = 1;

            g.weightx = 0.65;

            form.add(fields[i], g);
        }

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        form,
                        existing == null
                                ? "Add New Book"
                                : "Update Book",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        String bookId = clean(id.getText());

        String bookTitle = clean(title.getText());

        String bookAuthor = clean(author.getText());

        String bookCategory = clean(category.getText());

        if (bookId.isEmpty()
                || bookTitle.isEmpty()
                || bookAuthor.isEmpty()
                || bookCategory.isEmpty()) {

            message("Please fill in all fields.");

            return false;
        }

        int qty;

        try {

            qty = Integer.parseInt(
                    clean(quantity.getText())
            );

        } catch (NumberFormatException ex) {

            message("Quantity must be a whole number.");

            return false;
        }

        if (qty < 0) {

            message("Quantity cannot be negative.");

            return false;
        }

        if (existing == null) {

            if (findBook(bookId) != null) {

                message("A book with this ID already exists.");

                return false;
            }

            data.books.add(new Book(
                    bookId,
                    bookTitle,
                    bookAuthor,
                    bookCategory,
                    qty
            ));

        } else {

            Book duplicate = findBook(bookId);

            if (duplicate != null && duplicate != existing) {

                message("Another book already uses this ID.");

                return false;
            }

            int currentlyIssued =
                    existing.quantity - availableCopies(existing);

            if (qty < currentlyIssued) {

                message(
                        "Quantity cannot be less than copies "
                        + "currently issued (" + currentlyIssued + ")."
                );

                return false;
            }

            String oldId = existing.id;

            existing.id = bookId;

            existing.title = bookTitle;

            existing.author = bookAuthor;

            existing.category = bookCategory;

            existing.quantity = qty;

            for (IssueRecord r : data.issues) {

                if (r.bookId.equalsIgnoreCase(oldId)) {

                    r.bookId = bookId;

                    r.bookTitle = bookTitle;
                }
            }
        }

        saveData();

        return true;
    }
    // ISSUE AND RETURN SCREEN

    private JPanel createIssues() {

        JPanel page = page();

        page.add(
                pageTitle(
                        adminUser ? "Issue & Return" : "My Books",
                        adminUser
                                ? "Issue books to students and process returns"
                                : "Your current and previous book transactions"
                ),
                BorderLayout.NORTH
        );

        JPanel center =
                new JPanel(new BorderLayout(10, 12));

        center.setOpaque(false);

        String[] columns = adminUser
                ? new String[]{
                    "Student",
                    "Book ID",
                    "Book Title",
                    "Issue Date",
                    "Return Date",
                    "Status"
                }
                : new String[]{
                    "Book ID",
                    "Book Title",
                    "Issue Date",
                    "Return Date",
                    "Status"
                };

        DefaultTableModel model =
                new DefaultTableModel(columns, 0) {

                    public boolean isCellEditable(
                            int row, int column) {
                        return false;
                    }
                };

        JTable table = createTable(model);

        JScrollPane scroll = new JScrollPane(table);

        styleScroll(scroll);

        fillIssueTable(model);

        if (adminUser) {

            JPanel issueForm =
                    new JPanel(new FlowLayout(
                            FlowLayout.LEFT, 8, 10
                    ));

            issueForm.setBackground(PANEL);

            issueForm.setBorder(
                    new EmptyBorder(5, 8, 5, 8)
            );

            JTextField student = new JTextField(12);

            JTextField bookId = new JTextField(10);

            styleField(student);

            styleField(bookId);

            JButton issue = button(
                    "Issue Book", GREEN
            );

            issueForm.add(
                    label("Student username:", 12, TEXT, true)
            );

            issueForm.add(student);

            issueForm.add(
                    label("Book ID:", 12, TEXT, true)
            );

            issueForm.add(bookId);

            issueForm.add(issue);

            issue.addActionListener(e -> {

                String studentName =
                        clean(student.getText());

                String id = clean(bookId.getText());

                if (studentName.isEmpty() || id.isEmpty()) {

                    message(
                            "Enter student username and book ID."
                    );

                    return;
                }

                if (!studentName.equalsIgnoreCase("student")) {

                    message(
                            "Student account not found. "
                            + "Use username: student."
                    );

                    return;
                }

                Book book = findBook(id);

                if (book == null) {

                    message("Book ID not found.");

                    return;
                }

                if (availableCopies(book) <= 0) {

                    message(
                            "No copies are currently available."
                    );

                    return;
                }

                data.issues.add(
                        new IssueRecord(studentName, book)
                );

                saveData();

                student.setText("");

                bookId.setText("");

                fillIssueTable(model);

                message(
                        "Book issued successfully to "
                        + studentName + "."
                );
            });

            center.add(issueForm, BorderLayout.NORTH);
        }

        center.add(scroll, BorderLayout.CENTER);

        if (adminUser) {

            JPanel bottom =
                    new JPanel(new FlowLayout(FlowLayout.LEFT));

            bottom.setOpaque(false);

            JButton returnButton = button(
                    "Return Selected Book", ACCENT
            );

            bottom.add(returnButton);

            returnButton.addActionListener(e -> {

                int row = table.getSelectedRow();

                if (row < 0) {

                    message("Select an active issue first.");

                    return;
                }

                row = table.convertRowIndexToModel(row);

                String studentName =
                        String.valueOf(
                                model.getValueAt(row, 0)
                        );

                String bookId =
                        String.valueOf(
                                model.getValueAt(row, 1)
                        );

                IssueRecord found = null;

                for (IssueRecord r : data.issues) {

                    if (!r.returned
                            && r.student.equalsIgnoreCase(studentName)
                            && r.bookId.equalsIgnoreCase(bookId)) {

                        found = r;

                        break;
                    }
                }

                if (found == null) {

                    message(
                            "This record has already been returned."
                    );

                    return;
                }

                found.returned = true;

                found.returnDate = today();

                saveData();

                fillIssueTable(model);

                message(
                        "Book return recorded successfully."
                );
            });

            center.add(bottom, BorderLayout.SOUTH);
        }

        page.add(center, BorderLayout.CENTER);

        return page;
    }

    // REFRESH ISSUE TABLE

    private void fillIssueTable(DefaultTableModel model) {

        model.setRowCount(0);

        for (IssueRecord r : data.issues) {

            if (!adminUser
                    && !r.student.equalsIgnoreCase(currentUser)) {

                continue;
            }

            String status =
                    r.returned ? "Returned" : "Issued";

            if (adminUser) {

                model.addRow(new Object[]{
                        r.student,
                        r.bookId,
                        r.bookTitle,
                        r.issueDate,
                        r.returnDate,
                        status
                });

            } else {

                model.addRow(new Object[]{
                        r.bookId,
                        r.bookTitle,
                        r.issueDate,
                        r.returnDate,
                        status
                });
            }
        }
    }

    // COMMON PAGE DESIGN

    private JPanel page() {

        JPanel p =
                new JPanel(new BorderLayout(18, 18));

        p.setBackground(NAVY);

        p.setBorder(
                new EmptyBorder(25, 25, 25, 25)
        );

        return p;
    }

    // PAGE TITLE

    private JPanel pageTitle(
            String heading, String subtitle) {

        JPanel p = new JPanel();

        p.setOpaque(false);

        p.setLayout(
                new BoxLayout(p, BoxLayout.Y_AXIS)
        );

        p.add(label(heading, 25, TEXT, true));

        p.add(Box.createVerticalStrut(5));

        p.add(label(subtitle, 13, MUTED, false));

        p.setBorder(
                new EmptyBorder(0, 0, 10, 0)
        );

        return p;
    }

    // FIELD LABEL

    private JLabel fieldLabel(String text) {

        JLabel l = label(text, 11, MUTED, true);

        l.setBorder(
                new EmptyBorder(0, 0, 7, 0)
        );

        return l;
    }

    // LABEL DESIGN

    private JLabel label(
            String text,
            int size,
            Color color,
            boolean bold) {

        JLabel l = new JLabel(text);

        l.setFont(
                new Font(
                        "Segoe UI",
                        bold ? Font.BOLD : Font.PLAIN,
                        size
                )
        );

        l.setForeground(color);

        return l;
    }

    // TEXT FIELD DESIGN

    private void styleField(JTextField field) {

        field.setFont(NORMAL);

        field.setBackground(NAVY_LIGHT);

        field.setForeground(Color.WHITE);

        field.setCaretColor(Color.WHITE);

        field.setSelectionColor(ACCENT);

        field.setSelectedTextColor(Color.WHITE);

        field.setOpaque(true);

        field.setBorder(new CompoundBorder(
                new LineBorder(
                        new Color(100, 139, 180), 1, true
                ),
                new EmptyBorder(8, 10, 8, 10)
        ));

        field.setPreferredSize(
                new Dimension(220, 40)
        );
    }

    // BUTTON DESIGN

    private JButton button(String text, Color color) {

        JButton b = new JButton(text);

        b.setUI(
                new javax.swing.plaf.basic.BasicButtonUI()
        );

        b.setFont(BOLD);

        b.setForeground(Color.WHITE);

        b.setBackground(color);

        b.setOpaque(true);

        b.setContentAreaFilled(true);

        b.setBorderPainted(false);

        b.setFocusPainted(false);

        b.setBorder(
                new EmptyBorder(10, 16, 10, 16)
        );

        b.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        return b;
    }

    // TABLE DESIGN

    private JTable createTable(DefaultTableModel model) {

        JTable table = new JTable(model);

        table.setRowHeight(38);

        table.setFont(NORMAL);

        table.setForeground(TEXT);

        table.setBackground(PANEL);

        table.setSelectionBackground(ACCENT);

        table.setSelectionForeground(Color.WHITE);

        table.setGridColor(
                new Color(65, 88, 117)
        );

        table.setShowGrid(true);

        table.setFillsViewportHeight(true);

        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();

        header.setPreferredSize(
                new Dimension(100, 42)
        );

        header.setReorderingAllowed(false);

        header.setDefaultRenderer(
                new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable t,
                    Object value,
                    boolean selected,
                    boolean focused,
                    int row,
                    int column) {

                JLabel cell = (JLabel)
                        super.getTableCellRendererComponent(
                                t, value, selected,
                                focused, row, column
                        );

                cell.setOpaque(true);

                cell.setBackground(NAVY_LIGHT);

                cell.setForeground(Color.WHITE);

                cell.setFont(BOLD);

                cell.setHorizontalAlignment(
                        SwingConstants.LEFT
                );

                cell.setBorder(new CompoundBorder(
                        new MatteBorder(
                                0, 0, 1, 1,
                                new Color(65, 88, 117)
                        ),
                        new EmptyBorder(8, 10, 8, 10)
                ));

                return cell;
            }
        });

        return table;
    }

    // SCROLL PANE DESIGN

    private void styleScroll(JScrollPane scroll) {

        scroll.getViewport().setBackground(PANEL);

        scroll.setBackground(PANEL);

        scroll.setBorder(
                new LineBorder(
                        new Color(55, 77, 105), 1, true
                )
        );
    }

    // MESSAGE BOX

    private void message(String text) {

        JOptionPane.showMessageDialog(
                this,
                text,
                "Digital Library",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // MAIN METHOD

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );

            } catch (Exception ignored) {

                // Default Swing look and feel will be used.
            }

            DigitalLibrary app = new DigitalLibrary();

            app.setVisible(true);
        });
    }
}