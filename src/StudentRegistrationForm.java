import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;


public class StudentRegistrationForm extends JFrame {
    
    private JTextField txtFirstName, txtLastName, txtEmail, txtConfirmEmail;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> comboYear, comboMonth, comboDay;
    private JRadioButton rbMale, rbFemale;
    private JRadioButton rbCivil, rbCSE, rbElectrical, rbEC, rbMechanical;
    private ButtonGroup genderGroup, departmentGroup;
    private JButton btnSubmit, btnCancel;
    private JTextArea txtDataDisplay;
    

    private JLabel lblFirstNameError, lblLastNameError, lblEmailError, lblConfirmEmailError;
    private JLabel lblPasswordError, lblConfirmPasswordError, lblDOBError, lblGenderError, lblDepartmentError;
    

    private static final String CSV_FILE = "students.csv";
    private static final String DB_FILE = "students.accdb";
    private static final String DB_URL = "jdbc:ucanaccess://" + DB_FILE;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,20}$");
    

    private static int studentCounter = 0;
    
    public StudentRegistrationForm() {
        initializeComponents();
        setupExactUILayout();
        setupEventListeners();
        initializeDatabase();
        loadStudentCounter();
    }
    
    private void initializeComponents() {

        setTitle("New Student Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        

        getContentPane().setBackground(new Color(220, 228, 236));
        

        txtFirstName = new JTextField(18);
        txtLastName = new JTextField(18);
        txtEmail = new JTextField(18);
        txtConfirmEmail = new JTextField(18);
        txtPassword = new JPasswordField(18);
        txtConfirmPassword = new JPasswordField(18);
        
        comboYear = new JComboBox<>();
        comboMonth = new JComboBox<>();
        comboDay = new JComboBox<>();
        populateDateComboBoxes();
        
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        

        rbCivil = new JRadioButton("Civil");
        rbCSE = new JRadioButton("Computer Science and Engineering");
        rbElectrical = new JRadioButton("Electrical");
        rbEC = new JRadioButton("Electronics and Communication");
        rbMechanical = new JRadioButton("Mechanical");
        departmentGroup = new ButtonGroup();
        departmentGroup.add(rbCivil);
        departmentGroup.add(rbCSE);
        departmentGroup.add(rbElectrical);
        departmentGroup.add(rbEC);
        departmentGroup.add(rbMechanical);
        
        btnSubmit = new JButton("Submit");
        btnCancel = new JButton("Cancel");
        
        txtDataDisplay = new JTextArea(10, 25);
        txtDataDisplay.setEditable(false);
        txtDataDisplay.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtDataDisplay.setLineWrap(true);
        txtDataDisplay.setWrapStyleWord(true);
        
        lblFirstNameError = createErrorLabel();
        lblLastNameError = createErrorLabel();
        lblEmailError = createErrorLabel();
        lblConfirmEmailError = createErrorLabel();
        lblPasswordError = createErrorLabel();
        lblConfirmPasswordError = createErrorLabel();
        lblDOBError = createErrorLabel();
        lblGenderError = createErrorLabel();
        lblDepartmentError = createErrorLabel();
    }
    
    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setForeground(Color.RED);
        label.setFont(new Font("Arial", Font.PLAIN, 9));
        return label;
    }
    
    private void populateDateComboBoxes() {

        int currentYear = LocalDate.now().getYear();
        int minYear = currentYear - 60;
        int maxYear = currentYear - 16;
        
        comboYear.addItem("Select Year");
        for (int year = maxYear; year >= minYear; year--) {
            comboYear.addItem(String.valueOf(year));
        }
        
        comboMonth.addItem("Select Month");
        String[] months = {"January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        for (String month : months) {
            comboMonth.addItem(month);
        }
        

        comboDay.addItem("Select Day");
        for (int day = 1; day <= 31; day++) {
            comboDay.addItem(String.valueOf(day));
        }
    }
    
    private void updateDays() {
        String yearStr = (String) comboYear.getSelectedItem();
        String month = (String) comboMonth.getSelectedItem();
        
        if (yearStr == null || yearStr.equals("Select Year") || 
            month == null || month.equals("Select Month")) {
            return;
        }
        
        int year = Integer.parseInt(yearStr);
        int monthIndex = comboMonth.getSelectedIndex();
        int daysInMonth = getDaysInMonth(year, monthIndex);
        
        String selectedDay = (String) comboDay.getSelectedItem();
        comboDay.removeAllItems();
        comboDay.addItem("Select Day");
        for (int day = 1; day <= daysInMonth; day++) {
            comboDay.addItem(String.valueOf(day));
        }
        

        if (selectedDay != null && !selectedDay.equals("Select Day")) {
            int dayNum = Integer.parseInt(selectedDay);
            if (dayNum <= daysInMonth) {
                comboDay.setSelectedItem(selectedDay);
            }
        }
    }
    
    private int getDaysInMonth(int year, int month) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return isLeapYear(year) ? 29 : 28;
            default:
                return 31;
        }
    }
    
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
    

    private void setupExactUILayout() {

        JPanel mainPanel = new JPanel(null); 
        mainPanel.setBackground(new Color(220, 228, 236));
        mainPanel.setPreferredSize(new Dimension(750, 520));
        

        JLabel lblTitle = new JLabel("New Student Registration Form");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setBounds(250, 10, 250, 25);
        mainPanel.add(lblTitle);
        

        int labelX = 20;
        int fieldX = 150;
        int startY = 45;
        int rowHeight = 32;
        

        JLabel lblFirstName = new JLabel("Student First Name");
        lblFirstName.setBounds(labelX, startY, 120, 20);
        mainPanel.add(lblFirstName);
        txtFirstName.setBounds(fieldX, startY, 180, 22);
        mainPanel.add(txtFirstName);
        lblFirstNameError.setBounds(fieldX, startY + 18, 180, 12);
        mainPanel.add(lblFirstNameError);
        

        startY += rowHeight;
        JLabel lblLastName = new JLabel("Student Last Name");
        lblLastName.setBounds(labelX, startY, 120, 20);
        mainPanel.add(lblLastName);
        txtLastName.setBounds(fieldX, startY, 180, 22);
        mainPanel.add(txtLastName);
        lblLastNameError.setBounds(fieldX, startY + 18, 180, 12);
        mainPanel.add(lblLastNameError);
        

        startY += rowHeight;
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setBounds(labelX, startY, 120, 20);
        mainPanel.add(lblEmail);
        txtEmail.setBounds(fieldX, startY, 180, 22);
        mainPanel.add(txtEmail);
        lblEmailError.setBounds(fieldX, startY + 18, 180, 12);
        mainPanel.add(lblEmailError);
        

        startY += rowHeight;
        JLabel lblConfirmEmail = new JLabel("Confirm Email Address");
        lblConfirmEmail.setBounds(labelX, startY, 125, 20);
        mainPanel.add(lblConfirmEmail);
        txtConfirmEmail.setBounds(fieldX, startY, 180, 22);
        mainPanel.add(txtConfirmEmail);
        lblConfirmEmailError.setBounds(fieldX, startY + 18, 180, 12);
        mainPanel.add(lblConfirmEmailError);
        

        startY += rowHeight;
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(labelX, startY, 120, 20);
        mainPanel.add(lblPassword);
        txtPassword.setBounds(fieldX, startY, 180, 22);
        mainPanel.add(txtPassword);
        lblPasswordError.setBounds(fieldX, startY + 18, 180, 12);
        mainPanel.add(lblPasswordError);
        

        startY += rowHeight;
        JLabel lblConfirmPassword = new JLabel("Confirm Password");
        lblConfirmPassword.setBounds(labelX, startY, 120, 20);
        mainPanel.add(lblConfirmPassword);
        txtConfirmPassword.setBounds(fieldX, startY, 180, 22);
        mainPanel.add(txtConfirmPassword);
        lblConfirmPasswordError.setBounds(fieldX, startY + 18, 180, 12);
        mainPanel.add(lblConfirmPasswordError);
        

        startY += rowHeight;
        JLabel lblDOB = new JLabel("Date of Birth");
        lblDOB.setBounds(labelX, startY, 120, 20);
        mainPanel.add(lblDOB);
        
        comboYear.setBounds(fieldX, startY, 85, 22);
        comboMonth.setBounds(fieldX + 90, startY, 100, 22);
        comboDay.setBounds(fieldX + 195, startY, 80, 22);
        mainPanel.add(comboYear);
        mainPanel.add(comboMonth);
        mainPanel.add(comboDay);
        
        lblDOBError.setBounds(fieldX, startY + 18, 250, 12);
        mainPanel.add(lblDOBError);
        

        rbMale.setBounds(fieldX, startY + 35, 60, 20);
        rbFemale.setBounds(fieldX + 65, startY + 35, 70, 20);
        rbMale.setBackground(new Color(220, 228, 236));
        rbFemale.setBackground(new Color(220, 228, 236));
        mainPanel.add(rbMale);
        mainPanel.add(rbFemale);
        
        lblGenderError.setBounds(fieldX + 140, startY + 38, 150, 12);
        mainPanel.add(lblGenderError);
        

        startY += 65;
        JLabel lblDepartment = new JLabel("Department");
        lblDepartment.setBounds(labelX, startY, 120, 20);
        mainPanel.add(lblDepartment);
        
        int deptY = startY;
        rbCivil.setBounds(fieldX, deptY, 200, 18);
        rbCivil.setBackground(new Color(220, 228, 236));
        mainPanel.add(rbCivil);
        
        deptY += 20;
        rbCSE.setBounds(fieldX, deptY, 250, 18);
        rbCSE.setBackground(new Color(220, 228, 236));
        mainPanel.add(rbCSE);
        
        deptY += 20;
        rbElectrical.setBounds(fieldX, deptY, 200, 18);
        rbElectrical.setBackground(new Color(220, 228, 236));
        mainPanel.add(rbElectrical);
        
        deptY += 20;
        rbEC.setBounds(fieldX, deptY, 220, 18);
        rbEC.setBackground(new Color(220, 228, 236));
        mainPanel.add(rbEC);
        
        deptY += 20;
        rbMechanical.setBounds(fieldX, deptY, 200, 18);
        rbMechanical.setBackground(new Color(220, 228, 236));
        mainPanel.add(rbMechanical);
        
        lblDepartmentError.setBounds(fieldX, deptY + 20, 200, 12);
        mainPanel.add(lblDepartmentError);

        btnSubmit.setBounds(fieldX, 420, 80, 25);
        btnCancel.setBounds(fieldX + 90, 420, 80, 25);
        mainPanel.add(btnSubmit);
        mainPanel.add(btnCancel);
        
        JLabel lblDataTitle = new JLabel("Your Data is Below:");
        lblDataTitle.setBounds(460, 200, 150, 20);
        lblDataTitle.setFont(new Font("Arial", Font.BOLD, 11));
        mainPanel.add(lblDataTitle);
        
        JScrollPane scrollPane = new JScrollPane(txtDataDisplay);
        scrollPane.setBounds(460, 225, 260, 220);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(scrollPane);
        
        add(mainPanel);
    }
    
    private void setupEventListeners() {

        comboYear.addActionListener(e -> updateDays());
        comboMonth.addActionListener(e -> updateDays());
        

        btnSubmit.addActionListener(e -> handleSubmit());
        
        // Cancel button
        btnCancel.addActionListener(e -> handleCancel());
    }
    
    private void handleSubmit() {
        // Clear previous errors
        clearErrors();
        
        // Validate all fields
        boolean isValid = true;
        StringBuilder errorSummary = new StringBuilder("Validation Errors:\n\n");
        
        // First Name
        String firstName = txtFirstName.getText().trim();
        if (firstName.isEmpty()) {
            lblFirstNameError.setText("First name is required");
            errorSummary.append("- First name is required\n");
            isValid = false;
        }
        
        // Last Name
        String lastName = txtLastName.getText().trim();
        if (lastName.isEmpty()) {
            lblLastNameError.setText("Last name is required");
            errorSummary.append("- Last name is required\n");
            isValid = false;
        }
        
        // Email
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            lblEmailError.setText("Email is required");
            errorSummary.append("- Email is required\n");
            isValid = false;
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            lblEmailError.setText("Invalid email format");
            errorSummary.append("- Invalid email format\n");
            isValid = false;
        }
        
        // Confirm Email
        String confirmEmail = txtConfirmEmail.getText().trim();
        if (confirmEmail.isEmpty()) {
            lblConfirmEmailError.setText("Confirm email is required");
            errorSummary.append("- Confirm email is required\n");
            isValid = false;
        } else if (!email.equals(confirmEmail)) {
            lblConfirmEmailError.setText("Emails do not match");
            errorSummary.append("- Emails do not match\n");
            isValid = false;
        }
        
        // Password
        String password = new String(txtPassword.getPassword());
        if (password.isEmpty()) {
            lblPasswordError.setText("Password is required");
            errorSummary.append("- Password is required\n");
            isValid = false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            lblPasswordError.setText("8-20 chars, 1 letter, 1 digit");
            errorSummary.append("- Password must be 8-20 chars with at least 1 letter and 1 digit\n");
            isValid = false;
        }
        
        // Confirm Password
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        if (confirmPassword.isEmpty()) {
            lblConfirmPasswordError.setText("Confirm password is required");
            errorSummary.append("- Confirm password is required\n");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            lblConfirmPasswordError.setText("Passwords do not match");
            errorSummary.append("- Passwords do not match\n");
            isValid = false;
        }
        
        // Date of Birth
        String yearStr = (String) comboYear.getSelectedItem();
        String month = (String) comboMonth.getSelectedItem();
        String dayStr = (String) comboDay.getSelectedItem();
        
        LocalDate dob = null;
        if (yearStr == null || yearStr.equals("Select Year") || 
            month == null || month.equals("Select Month") ||
            dayStr == null || dayStr.equals("Select Day")) {
            lblDOBError.setText("Complete DOB is required");
            errorSummary.append("- Complete date of birth is required\n");
            isValid = false;
        } else {
            int year = Integer.parseInt(yearStr);
            int day = Integer.parseInt(dayStr);
            int monthIndex = comboMonth.getSelectedIndex();
            dob = LocalDate.of(year, monthIndex, day);
            int age = Period.between(dob, LocalDate.now()).getYears();
            
            if (age < 16 || age > 60) {
                lblDOBError.setText("Age must be 16-60");
                errorSummary.append("- Age must be between 16 and 60 years\n");
                isValid = false;
            }
        }
        
        // Gender
        String gender = null;
        if (rbMale.isSelected()) {
            gender = "M";
        } else if (rbFemale.isSelected()) {
            gender = "F";
        } else {
            lblGenderError.setText("Gender is required");
            errorSummary.append("- Gender is required\n");
            isValid = false;
        }
        
        // Department
        String department = null;
        if (rbCivil.isSelected()) {
            department = "Civil";
        } else if (rbCSE.isSelected()) {
            department = "CSE";
        } else if (rbElectrical.isSelected()) {
            department = "Electrical";
        } else if (rbEC.isSelected()) {
            department = "E&C";
        } else if (rbMechanical.isSelected()) {
            department = "Mechanical";
        } else {
            lblDepartmentError.setText("Department is required");
            errorSummary.append("- Department is required\n");
            isValid = false;
        }
        
        if (!isValid) {
            // Show error dialog
            JOptionPane.showMessageDialog(this, errorSummary.toString(), 
                "Validation Errors", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Generate Student ID
        String studentId = generateStudentId();
        
        // Format the record
        String formattedDate = dob.toString();
        String record = String.format("ID: %s | %s %s | %s | %s | %s | %s",
            studentId, firstName, lastName, gender, department, formattedDate, email);
        
        // Display in text area
        txtDataDisplay.append(record + "\n\n");
        
        // Save to CSV and Database
        saveToCSV(studentId, firstName, lastName, gender, department, formattedDate, email);
        saveToDatabase(studentId, firstName, lastName, gender, department, formattedDate, email);
        
        // Show success message
        JOptionPane.showMessageDialog(this, 
            "Student registered successfully!\nStudent ID: " + studentId,
            "Success", JOptionPane.INFORMATION_MESSAGE);
        
        // Clear form
        clearForm();
    }
    
    private void clearErrors() {
        lblFirstNameError.setText(" ");
        lblLastNameError.setText(" ");
        lblEmailError.setText(" ");
        lblConfirmEmailError.setText(" ");
        lblPasswordError.setText(" ");
        lblConfirmPasswordError.setText(" ");
        lblDOBError.setText(" ");
        lblGenderError.setText(" ");
        lblDepartmentError.setText(" ");
    }
    
    private String generateStudentId() {
        int currentYear = LocalDate.now().getYear();
        studentCounter++;
        return String.format("%d-%05d", currentYear, studentCounter);
    }
    
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Create Students table if it doesn't exist
            String createTableSQL = "CREATE TABLE Students (" +
                "StudentID VARCHAR(15) PRIMARY KEY, " +
                "FirstName VARCHAR(50) NOT NULL, " +
                "LastName VARCHAR(50) NOT NULL, " +
                "Gender VARCHAR(1) NOT NULL, " +
                "Department VARCHAR(50) NOT NULL, " +
                "DateOfBirth DATE NOT NULL, " +
                "Email VARCHAR(100) NOT NULL)";
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            } catch (SQLException e) {
                // Table already exists, ignore
                if (!e.getMessage().contains("already exists")) {
                    System.err.println("Database initialization warning: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error initializing database: " + e.getMessage() + "\nWill save to CSV only.",
                "Database Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void saveToDatabase(String studentId, String firstName, String lastName,
                               String gender, String department, String dob, String email) {
        String insertSQL = "INSERT INTO Students (StudentID, FirstName, LastName, Gender, Department, DateOfBirth, Email) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            
            pstmt.setString(1, studentId);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, gender);
            pstmt.setString(5, department);
            pstmt.setDate(6, java.sql.Date.valueOf(dob));
            pstmt.setString(7, email);
            
            int rowsInserted = pstmt.executeUpdate();
            
            if (rowsInserted > 0) {
                System.out.println("Student record saved to database successfully.");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error saving to database: " + e.getMessage() + "\nData saved to CSV only.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveToCSV(String studentId, String firstName, String lastName, 
                          String gender, String department, String dob, String email) {
        boolean fileExists = new File(CSV_FILE).exists();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE, true))) {
            // Write header if file is new
            if (!fileExists) {
                writer.println("StudentID,FirstName,LastName,Gender,Department,DateOfBirth,Email");
            }
            
            // Write student data
            writer.println(String.format("%s,%s,%s,%s,%s,%s,%s",
                studentId, firstName, lastName, gender, department, dob, email));
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving to CSV: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadStudentCounter() {
        int currentYear = LocalDate.now().getYear();
        String yearPrefix = String.valueOf(currentYear) + "-";
        int maxCounter = 0;
        
        // Try loading from database first
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT StudentID FROM Students WHERE StudentID LIKE '" + yearPrefix + "%'")) {
            
            while (rs.next()) {
                String id = rs.getString("StudentID");
                try {
                    int count = Integer.parseInt(id.substring(5));
                    if (count > maxCounter) maxCounter = count;
                } catch (Exception e) {}
            }
            studentCounter = maxCounter;
            return;
            
        } catch (SQLException e) {
            // Fall back to CSV if database fails
            System.err.println("Could not load counter from database, trying CSV: " + e.getMessage());
        }
        
        // Fallback: Load from CSV
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            studentCounter = 0;
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        String id = parts[0];
                        if (id.startsWith(yearPrefix)) {
                            try {
                                int count = Integer.parseInt(id.substring(5));
                                if (count > maxCounter) maxCounter = count;
                            } catch (Exception e) {}
                        }
                    }
                }
            }
            studentCounter = maxCounter;
            
        } catch (IOException e) {
            studentCounter = 0;
        }
    }
    
    private void handleCancel() {
        clearForm();
        clearErrors();
    }
    
    private void clearForm() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtConfirmEmail.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        comboYear.setSelectedIndex(0);
        comboMonth.setSelectedIndex(0);
        comboDay.setSelectedIndex(0);
        genderGroup.clearSelection();
        departmentGroup.clearSelection();
    }
    
    public static void main(String[] args) {
        // Set look and feel to system default for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
        
        // Run the application
        SwingUtilities.invokeLater(() -> {
            new StudentRegistrationForm().setVisible(true);
        });
    }
}
