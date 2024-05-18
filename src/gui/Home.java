/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import com.raven.datechooser.EventDateChooser;
import com.raven.datechooser.SelectedAction;
import com.raven.datechooser.SelectedDate;
import model.MySQL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.ClassShedule;
import model.Students;
import model.Subjects;
import model.Teachers;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author sanch
 */
public class Home extends javax.swing.JFrame {

    public static String SubjectName;

    public Home() {
        initComponents();
//        loadAllSubjects();
//        loadAllClassShedule();
        HomeObjectCount();
        loadHomeClassSheduleTable();

        dateChooser1.addEventDateChooser(new EventDateChooser() {
            @Override
            public void dateSelected(SelectedAction action, SelectedDate date) {
                System.out.println(date.getDay() + "-" + date.getMonth() + "-" + date.getYear());
                if (action.getAction() == SelectedAction.DAY_SELECTED) {
                    dateChooser1.hidePopup();
                }
            }
        });

        dateChooser2.addEventDateChooser(new EventDateChooser() {
            @Override
            public void dateSelected(SelectedAction action, SelectedDate date) {
                System.out.println(date.getDay() + "-" + date.getMonth() + "-" + date.getYear());
                if (action.getAction() == SelectedAction.DAY_SELECTED) {
                    dateChooser1.hidePopup();
                }
            }
        });

    }

    private void resetAllAttendancePage() {

        jTable11.clearSelection();
        jTable12.clearSelection();
        jTable12.setEnabled(true);
        loadHomeClassSheduleTable();

        jComboBox2.setSelectedIndex(0);
        jComboBox2.setEnabled(false);
        jButton17.setEnabled(false);
        jTable11.setEnabled(true);
    }

    private void loadAttendanceMarkedTable() {
        DefaultTableModel model = (DefaultTableModel) jTable13.getModel();
        model.setRowCount(0);
        try {
            ResultSet result = MySQL.Search("SELECT * FROM `attendance`\n"
                    + "INNER JOIN `class` ON `class`.`id`=`attendance`.`class_id` \n"
                    + "INNER JOIN `subject` ON `subject`.`subno`=`class`.`subject_subno`\n"
                    + "INNER JOIN `teacher`ON `teacher`.`tno`=`class`.`teacher_tno`\n"
                    + "INNER JOIN `student` ON `student`.`sno`=`attendance`.`student_sno`\n"
                    + "WHERE `class_id`='" + jTable11.getValueAt(jTable11.getSelectedRow(), 0) + "'");

            while (result.next()) {
                Vector v = new Vector();
                v.add(result.getString("attendance.id"));
                v.add(result.getString("subject.subno"));
                v.add(result.getString("subject.description"));
                v.add(result.getString("teacher.tno"));
                v.add(result.getString("teacher.firstName") + " " + result.getString("teacher.lastName"));
                v.add(result.getString("student.nic"));
                v.add(result.getString("student.firstName") + " " + result.getString("student.lastName"));
                v.add(result.getString("class.date"));
                v.add(result.getInt("attendance.status") == 1 ? "Present" : "absent");
                v.add(result.getString("class.startAt"));
                v.add(result.getString("class.endAt"));
                model.addRow(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAttendanceStudentTable(String subno) {
        try {
            ResultSet resultSet = MySQL.Search("SELECT * FROM invoice_item "
                    + "INNER JOIN teacher ON teacher.tno = invoice_item.teacher_tno "
                    + "INNER JOIN invoice ON invoice.id = invoice_item.invoice_id "
                    + "INNER JOIN student ON student.sno = invoice.student_sno "
                    + "WHERE teacher.subject_subno = '" + subno + "' ");

            DefaultTableModel model = (DefaultTableModel) jTable12.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                ResultSet result = MySQL.Search("SELECT * FROM `attendance` "
                        + "WHERE `class_id`='" + jTable11.getValueAt(jTable11.getSelectedRow(), 0) + "' "
                        + "AND `student_sno`='" + resultSet.getString("student.sno") + "' ");

                if (!result.next()) {
                    Vector v = new Vector();
                    v.add(resultSet.getString("student.sno"));
                    v.add(resultSet.getString("student.nic"));
                    v.add(resultSet.getString("student.firstName") + " " + resultSet.getString("student.lastName"));
                    model.addRow(v);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkTimeIsPassedOrNot(String time) {
        String givenTimeString = time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        LocalTime givenTime = LocalTime.parse(givenTimeString, formatter);
        LocalTime currentTime = LocalTime.now();

        if (givenTime.isBefore(currentTime)) {
            // Passed
            return true;

        } else {
            // Not Passed
            return false;

        }
    }

    private void loadAttendanceTeacherTable(String query) {

        try {

            ResultSet resultSet = MySQL.Search(query);
            DefaultTableModel model = (DefaultTableModel) jTable11.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                String time = resultSet.getString("class.endAt");
                if (!checkTimeIsPassedOrNot(time)) {
                    Vector v = new Vector();
                    v.add(resultSet.getString("class.id"));
                    v.add(resultSet.getString("teacher.nic"));
                    v.add(resultSet.getString("teacher.firstName") + " " + resultSet.getString("teacher.lastName"));
                    v.add(resultSet.getString("subject.subno"));
                    v.add(resultSet.getString("subject.description"));
                    v.add(resultSet.getString("class.startAt"));
                    v.add(resultSet.getString("class.endAt"));
                    model.addRow(v);
                } else {
                    System.out.println("pass");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void HomeObjectCount() {
        int StudentCount = 0;
        int TeacherCount = 0;
        int SubjectCount = 0;

        try {
            ResultSet result1 = MySQL.Search("SELECT COUNT(`sno`) AS `studentCount` FROM `student`");
            ResultSet result2 = MySQL.Search("SELECT COUNT(`tno`) AS `teacherCount` FROM `teacher`");
            ResultSet result3 = MySQL.Search("SELECT COUNT(`subno`) AS `subjectCount` FROM `subject`");

            while (result1.next()) {
                StudentCount += result1.getInt("studentCount");
            }

            while (result2.next()) {
                TeacherCount += result2.getInt("teacherCount");
            }

            while (result3.next()) {
                SubjectCount += result3.getInt("subjectCount");
            }

            jLabel73.setText(String.valueOf(StudentCount));
            jLabel74.setText(String.valueOf(TeacherCount));
            jLabel75.setText(String.valueOf(SubjectCount));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadHomeClassSheduleTable() {
        try {
            ResultSet resultSet = MySQL.Search("SELECT * FROM `class` "
                    + "INNER JOIN `subject` ON `subject`.`subno`=`class`.`subject_subno` "
                    + "INNER JOIN `teacher` ON `teacher`.`tno`=`class`.`teacher_tno` ORDER BY `date` DESC");
            DefaultTableModel tModel = (DefaultTableModel) jTable10.getModel();
            tModel.setRowCount(0);

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String stringToday = format.format(date);

            while (resultSet.next()) {
                if (stringToday.equals(resultSet.getString("date"))) {

                    Vector<String> vector = new Vector<>();
                    vector.add(resultSet.getString("id"));
                    vector.add(resultSet.getString("description"));
                    vector.add(resultSet.getString("nic"));
                    vector.add(resultSet.getString("firstName") + " " + resultSet.getString("lastName"));
                    vector.add(resultSet.getString("date"));
                    vector.add(resultSet.getString("startAt"));
                    vector.add(resultSet.getString("endAt"));
                    vector.add(resultSet.getInt("status") == 1 ? "Held" : "Canceld");
                    tModel.addRow(vector);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void resetAllPaymentFieldsTables() {
        jTextField13.setText("");
        jTextField45.setText("");
        jLabel67.setText("");
        jLabel68.setText("");
        jTextField46.setText("");
        jLabel66.setText("");

        DefaultTableModel invoiceItemTModel = (DefaultTableModel) jTable9.getModel();
        invoiceItemTModel.setRowCount(0);

    }

    private String generateUniqueId() {
        String text = String.valueOf(UUID.randomUUID());
        String[] results = text.split("(-)");
        String id = "#" + results[0];
        return id;

    }

    private void loadClassSheduleTable() {
        DefaultTableModel tableModel = (DefaultTableModel) jTable4.getModel();
        tableModel.setRowCount(0);

        try {
            ResultSet resultSet = MySQL.Search("SELECT * FROM `class` "
                    + "INNER JOIN `teacher` ON `class`.`teacher_tno`=`teacher`.`tno` "
                    + "INNER JOIN `subject` ON `class`.`subject_subno`=`subject`.`subno` ORDER BY `date` DESC");

            while (resultSet.next()) {
                Vector<String> vector = new Vector<String>();
                vector.add(resultSet.getString("id"));
                vector.add(resultSet.getString("description"));
                vector.add(resultSet.getString("nic"));
                vector.add(resultSet.getString("firstName") + " " + resultSet.getString("lastName"));
                vector.add(resultSet.getString("date"));
                vector.add(resultSet.getString("startAt"));
                vector.add(resultSet.getString("endAt"));
                vector.add(resultSet.getInt("status") == 1 ? "Held" : "Canceld");
                tableModel.addRow(vector);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void resetAllAddClassFields() {
        jComboBox1.setSelectedIndex(0);
        tcjComboBox2.setSelectedItem(0);
        jTextField31.setText("");
        jComboBox3.setSelectedIndex(0);
        jTextField9.setText("Search Subject By Name ...");
        jTextField30.setText("Search Class By Subject Name ...");
        jButton22.setEnabled(true);
        jButton23.setEnabled(false);
        jButton24.setEnabled(false);

    }

    private String validateAddClassFields() {

        if (jComboBox1.getSelectedIndex() == 0) {
            return "Please Select Subject";

        } else if (tcjComboBox2.getSelectedIndex() == 0) {
            return "Please Select Teacher";

        } else {
            return "Success";
        }

    }

    private void loadSubjectsTable() {

        DefaultTableModel tModel = (DefaultTableModel) jTable3.getModel();
        tModel.setRowCount(0);

        try {
            ResultSet resultSet = MySQL.Search("SELECT * FROM `subject`");

            while (resultSet.next()) {
                Vector<String> row = new Vector<>();
                row.add(resultSet.getString("subno"));
                row.add(resultSet.getString("description"));
                row.add(resultSet.getString("price"));
                tModel.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void subjectFieldReset() {
        jTextField9.setText("Search Subject By Name ...");
        jTextField25.setText("");
        jTextField4.setText("");
        jTextField26.setText("");
        jButton19.setEnabled(false);
        jButton7.setEnabled(false);
        jButton9.setEnabled(true);
        jTable3.setEnabled(true);
        jTextField25.grabFocus();

    }

    private String ValidateManageSubjectsField() {

        if (jTextField25.getText().isEmpty()) {
            return "Please Enter Subject Name ....";

        } else if (jTextField4.getText().isEmpty()) {
            return "Please Enter Subject Price ....";
        } else {
            return "Success";
        }
    }

    private void loadAllSubjects() {
        try {
            ResultSet resultSet = MySQL.Search("SELECT * FROM `subject`");
            while (resultSet.next()) {
                Subjects subjects = new Subjects();
                subjects.setSubno(resultSet.getString("subno"));
                subjects.setDescription(resultSet.getString("description"));
                subjects.setPrice(resultSet.getString("price"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String validateTeacherField() {

        if (jTextField35.getText().isEmpty()) {
            return "Please Enter First Name ....";

        } else if (jTextField27.getText().isEmpty()) {
            return "Please Enter Last Name ....";

        } else if (jTextField36.getText().isBlank()) {
            return "Please Enter Living Address ....";

        } else if (jTextField38.getText().isBlank()) {
            return "Please Enter National ID Number  ....";

        } else if (jTextField37.getText().isBlank()) {
            return "Please Enter Mobile Number ....";

        } else if (jTextField37.getText().length() != 10) {
            return "Invalid Mobile Number ....";

        } else if (buttonGroup1.getSelection() == null) {
            return "Please Select Gender ....";

        } else if (jComboBox6.getSelectedIndex() == 0) {
            return "Please Select Subject ....";

        } else {
            return "Success";
        }

    }

    private void ManageTeacherFieldReset() {
        jTextField35.setText("");
        jTextField27.setText("");
        jTextField36.setText("");
        jTextField38.setText("");
        jTextField37.setText("");
        jTextField24.setText("");
        jTextField12.setText("Search Teachers By NIC No .....");
        jComboBox6.setSelectedIndex(0);
        jRadioButton1.setSelected(true);
        jTextField35.grabFocus();

        jButton16.setEnabled(false);
        jButton5.setEnabled(false);
        jButton3.setEnabled(true);
        jRadioButton1.setEnabled(true);
        jRadioButton2.setEnabled(true);
        jTextField38.setEnabled(true);
        jTable1.setEnabled(true);

    }

    private void loadTeachersTable() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        try {
            ResultSet resultset = MySQL.Search("SELECT * FROM `teacher` "
                    + "INNER JOIN `subject` ON `teacher`.`subject_subno`=`subject`.`subno`");

            while (resultset.next()) {
                Vector<String> row = new Vector<>();
                row.add(resultset.getString("tno"));
                row.add(resultset.getString("mobile"));
                row.add(resultset.getString("nic"));
                row.add(resultset.getString("description"));
                row.add(resultset.getString("firstName"));
                row.add(resultset.getString("lastName"));
                row.add(resultset.getString("gender_id").equals("1") ? "Male" : "Female");
                row.add(resultset.getString("address"));
                model.addRow(row);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void Alert(String message, boolean isError) {
        if (isError) {
            JOptionPane.showMessageDialog(this, message, "Somthing Went Wrong", JOptionPane.ERROR_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(this, message, "All Successfully Done", JOptionPane.OK_OPTION);
        }

    }

    private String valdateStudentsFields() {
        if (jTextField1.getText().isEmpty()) {
            return "Please Enter First Name ....";

        } else if (jTextField2.getText().isEmpty()) {
            return "Please Enter Last Name ....";

        } else if (jTextField6.getText().isEmpty()) {
            return "Please Enter Living Address ....";

        } else if (jTextField7.getText().isEmpty()) {
            return "Please Enter Mobile Number ....";

        } else if (jTextField7.getText().length() != 10) {
            return "Invalid Mobile Number ....";

        } else if (jTextField11.getText().isEmpty()) {
            return "Please Enter National ID Number ....";

        } else if (jComboBox5.getSelectedIndex() == 0) {
            return "Please Select Gender ....";

        } else {
            return "Success";
        }

    }

    private void ManageStudentFieldReset() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jTextField11.setText("");
        jTextField8.setText("");
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String stringDate = format.format(date);
        jTextField33.setText(stringDate);
        jTextField10.setText("Search Students By NIC No.....");
        jButton6.setEnabled(false);
        jButton2.setEnabled(true);
        jButton15.setEnabled(false);
        jTextField11.setEnabled(true);
        jTable2.setEnabled(true);
        jComboBox5.setEnabled(true);
        jComboBox5.setSelectedIndex(0);

        jTextField1.grabFocus();
        loadStudentsTable();

    }

    private void loadStudentsTable() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);

        try {
            ResultSet resultset = MySQL.Search("SELECT * FROM `student`");

            while (resultset.next()) {
                Vector<String> studentTableRow = new Vector<>();
                studentTableRow.add(resultset.getString("sno"));
                studentTableRow.add(resultset.getString("nic"));
                studentTableRow.add(resultset.getString("mobile"));
                studentTableRow.add(resultset.getString("firstName"));
                studentTableRow.add(resultset.getString("lastName"));
                studentTableRow.add((resultset.getString("gender_id").equals("1")) ? "Male" : "Female");
                studentTableRow.add(resultset.getString("dob"));
                studentTableRow.add(resultset.getString("address"));
                model.addRow(studentTableRow);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        timePicker1 = new com.raven.swing.TimePicker();
        timePicker2 = new com.raven.swing.TimePicker();
        dateChooser1 = new com.raven.datechooser.DateChooser();
        dateChooser2 = new com.raven.datechooser.DateChooser();
        MenuPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        AddStudentPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox<>();
        jTextField33 = new javax.swing.JTextField();
        jButton28 = new javax.swing.JButton();
        jLabel88 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField10 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        AddTeacherPanel = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jTextField27 = new javax.swing.JTextField();
        jTextField35 = new javax.swing.JTextField();
        jTextField36 = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jTextField37 = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jTextField38 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextField24 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        jLabel87 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jTextField12 = new javax.swing.JTextField();
        jButton16 = new javax.swing.JButton();
        AddSubjectPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel72 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jTextField25 = new javax.swing.JTextField();
        jButton19 = new javax.swing.JButton();
        jTextField26 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jTextField5 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        tcjComboBox2 = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jTextField28 = new javax.swing.JTextField();
        jButton20 = new javax.swing.JButton();
        jLabel49 = new javax.swing.JLabel();
        jButton21 = new javax.swing.JButton();
        jTextField29 = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jLabel58 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jTextField30 = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        jTextField31 = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        jTextField32 = new javax.swing.JTextField();
        jLabel85 = new javax.swing.JLabel();
        HomePanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable10 = new javax.swing.JTable();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        AddPaymentPanel = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        AddPaymentPanel1 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();
        jLabel30 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jTextField18 = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jTextField40 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jTextField41 = new javax.swing.JTextField();
        jTextField42 = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jTextField20 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable9 = new javax.swing.JTable();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jTextField45 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        jTextField46 = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jLabel70 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jTextField48 = new javax.swing.JTextField();
        jScrollPane14 = new javax.swing.JScrollPane();
        jTable14 = new javax.swing.JTable();
        jTextField13 = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jTextField50 = new javax.swing.JTextField();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel93 = new javax.swing.JLabel();
        jTextField51 = new javax.swing.JTextField();
        AttendancePanel = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTable11 = new javax.swing.JTable();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTable12 = new javax.swing.JTable();
        jLabel79 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        jTable13 = new javax.swing.JTable();
        jLabel86 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jLabel84 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();

        timePicker1.set24hourMode(false);
        timePicker1.setDisplayText(jTextField5);

        timePicker2.setDisplayText(jTextField28);

        dateChooser1.setDateFormat("yyyy-MM-dd");
        dateChooser1.setTextRefernce(jTextField29);

        dateChooser2.setToolTipText("");
        dateChooser2.setDateFormat("yyyy-MM-dd");
        dateChooser2.setTextRefernce(jTextField33);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Harvard University");
        setFocusTraversalPolicyProvider(true);
        setMinimumSize(new java.awt.Dimension(1020, 720));
        setResizable(false);

        MenuPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 1, new java.awt.Color(0, 0, 0)));

        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel5MouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Home");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/3844435_home_house_icon (1).png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(28, 28, 28)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Manage Student ");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/309049_add_user_human_person_plus_icon.png"))); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(27, 27, 27)
                .addComponent(jLabel3)
                .addGap(18, 18, 18))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel7MouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Manage Subjects");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/6590531_add_and_book_education_learning_icon.png"))); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(20, 20, 20))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel2MouseClicked(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel35.setText("Manage Teachers");

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/2639755_add_male_user_icon (1).png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel35)
                .addGap(16, 16, 16))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel10MouseClicked(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel56.setText("Manage Payments");

        jLabel57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/8541975_file_invoice_dollar_icon (1).png"))); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel56)
                .addGap(12, 12, 12))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel13MouseClicked(evt);
            }
        });

        jLabel82.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel82.setText("Mark Attendance");

        jLabel83.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/calendar.png"))); // NOI18N

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel83, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel83, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel82)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout MenuPanelLayout = new javax.swing.GroupLayout(MenuPanel);
        MenuPanel.setLayout(MenuPanelLayout);
        MenuPanelLayout.setHorizontalGroup(
            MenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MenuPanelLayout.setVerticalGroup(
            MenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuPanelLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        AddStudentPanel.setLayout(null);

        jLabel10.setText("First Name  :-");

        jLabel11.setText("Last Name  :-");

        jTextField1.setOpaque(true);

        jTextField2.setOpaque(true);

        jLabel20.setText("Address  :-");

        jTextField6.setOpaque(true);

        jLabel12.setText("Date Of Birth  :-");

        jTextField7.setOpaque(true);

        jLabel25.setText("Mobile  :-");

        jLabel13.setText("Id");

        jTextField8.setEnabled(false);

        jTextField11.setOpaque(true);

        jLabel26.setText("Nic No  :-");

        jLabel18.setText("Gender  :-");

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "Male", "Female" }));
        jComboBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox5ActionPerformed(evt);
            }
        });

        jButton28.setText("...");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jLabel88.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel88.setText("Manage Students");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton28)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField11)
                            .addComponent(jComboBox5, 0, 250, Short.MAX_VALUE))))
                .addGap(36, 36, 36)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel88)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel88)
                .addGap(33, 33, 33)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel25)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel26)
                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12)
                                .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18)
                                .addComponent(jButton28)))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20)))
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        AddStudentPanel.add(jPanel3);
        jPanel3.setBounds(0, 0, 880, 210);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nic", "Mobile No", "First Name", "Last Name", "Gender", "Date Of Birth", "Address"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(1);
        }

        AddStudentPanel.add(jScrollPane2);
        jScrollPane2.setBounds(20, 270, 860, 350);

        jTextField10.setText("Search Students By NIC No.....");
        jTextField10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField10FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField10FocusLost(evt);
            }
        });
        jTextField10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField10ActionPerformed(evt);
            }
        });
        jTextField10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField10KeyReleased(evt);
            }
        });
        AddStudentPanel.add(jTextField10);
        jTextField10.setBounds(670, 240, 200, 22);

        jButton2.setText("Save");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        AddStudentPanel.add(jButton2);
        jButton2.setBounds(540, 640, 72, 23);

        jButton6.setText("Update");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jButton6.setEnabled(false);
        AddStudentPanel.add(jButton6);
        jButton6.setBounds(630, 640, 72, 23);

        jButton15.setText("Delete");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });
        jButton15.setEnabled(false);
        AddStudentPanel.add(jButton15);
        jButton15.setBounds(720, 640, 72, 23);

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        AddStudentPanel.add(jButton1);
        jButton1.setBounds(810, 640, 72, 23);

        AddTeacherPanel.setLayout(null);

        jPanel12.setLayout(null);

        jLabel37.setText("Teacher Last Name  :-");
        jPanel12.add(jLabel37);
        jLabel37.setBounds(65, 127, 114, 16);

        jLabel38.setText("Teacher First Name  :-");
        jPanel12.add(jLabel38);
        jLabel38.setBounds(63, 96, 115, 16);
        jPanel12.add(jTextField27);
        jTextField27.setBounds(197, 124, 243, 22);
        jPanel12.add(jTextField35);
        jTextField35.setBounds(196, 90, 244, 22);
        jPanel12.add(jTextField36);
        jTextField36.setBounds(198, 158, 242, 22);

        jLabel39.setText("Teacher Address  :- ");
        jPanel12.add(jLabel39);
        jLabel39.setBounds(77, 158, 110, 16);

        jLabel40.setText("Teacher Gender  :-");
        jPanel12.add(jLabel40);
        jLabel40.setBounds(77, 198, 100, 16);
        jPanel12.add(jTextField37);
        jTextField37.setBounds(650, 120, 227, 22);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Male ");
        jRadioButton1.setActionCommand("1");
        jPanel12.add(jRadioButton1);
        jRadioButton1.setBounds(187, 198, 60, 21);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Female");
        jRadioButton2.setActionCommand("2");
        jPanel12.add(jRadioButton2);
        jRadioButton2.setBounds(247, 198, 61, 21);

        jLabel41.setText("Teacher Mobile No  :-");
        jPanel12.add(jLabel41);
        jLabel41.setBounds(520, 120, 120, 16);

        jLabel42.setText("Teacher NIC No  :-");
        jPanel12.add(jLabel42);
        jLabel42.setBounds(540, 90, 97, 16);
        jPanel12.add(jTextField38);
        jTextField38.setBounds(650, 90, 227, 22);

        jLabel19.setText("Id  :-");
        jPanel12.add(jLabel19);
        jLabel19.setBounds(780, 200, 24, 16);

        jTextField24.setEnabled(false);
        jPanel12.add(jTextField24);
        jTextField24.setBounds(810, 200, 64, 22);

        jLabel27.setText("Subject  :-");
        jPanel12.add(jLabel27);
        jLabel27.setBounds(580, 160, 53, 16);

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));
        jPanel12.add(jComboBox6);
        jComboBox6.setBounds(650, 150, 227, 22);

        jLabel87.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel87.setText("Manage Teachers");
        jPanel12.add(jLabel87);
        jLabel87.setBounds(20, 20, 150, 30);

        AddTeacherPanel.add(jPanel12);
        jPanel12.setBounds(0, 0, 880, 250);

        jButton3.setText("Save");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        AddTeacherPanel.add(jButton3);
        jButton3.setBounds(540, 640, 72, 23);

        jButton4.setText("Cancel");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        AddTeacherPanel.add(jButton4);
        jButton4.setBounds(810, 640, 72, 23);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Mobile No", "Nic No", "Subject", "First Name", "Last Name", "Gender", "Address"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(1);
            jTable1.getColumnModel().getColumn(1).setHeaderValue("Mobile No");
            jTable1.getColumnModel().getColumn(3).setHeaderValue("Subject");
            jTable1.getColumnModel().getColumn(7).setHeaderValue("Address");
        }

        AddTeacherPanel.add(jScrollPane1);
        jScrollPane1.setBounds(10, 340, 870, 260);

        jButton5.setText("Update");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        AddTeacherPanel.add(jButton5);
        jButton5.setBounds(720, 640, 72, 23);
        jButton5.setEnabled(false);

        jTextField12.setText("Search Teachers By NIC No .....");
        jTextField12.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField12FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField12FocusLost(evt);
            }
        });
        jTextField12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField12KeyReleased(evt);
            }
        });
        AddTeacherPanel.add(jTextField12);
        jTextField12.setBounds(650, 300, 230, 22);

        jButton16.setText("Delete");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        AddTeacherPanel.add(jButton16);
        jButton16.setBounds(630, 640, 72, 23);
        jButton16.setEnabled(false);

        AddSubjectPanel.setVisible(false);
        AddSubjectPanel.setLayout(null);

        jLabel8.setText("Subject Price   :-");
        AddSubjectPanel.add(jLabel8);
        jLabel8.setBounds(70, 170, 100, 16);

        jLabel14.setText("Subject Name  :-");
        AddSubjectPanel.add(jLabel14);
        jLabel14.setBounds(70, 130, 90, 16);
        AddSubjectPanel.add(jTextField4);
        jTextField4.setBounds(180, 170, 240, 22);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "Description", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);

        AddSubjectPanel.add(jScrollPane3);
        jScrollPane3.setBounds(500, 80, 380, 160);

        jButton7.setText("Update");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jButton7);
        jButton7.setBounds(260, 220, 72, 23);
        jButton7.setEnabled(false);

        jButton8.setText("Cancel");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jButton8);
        jButton8.setBounds(350, 220, 70, 23);

        jButton9.setText("Save");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jButton9);
        jButton9.setBounds(80, 220, 70, 23);

        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(0, 0, 0)));

        jLabel72.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel72.setText("Add New Class");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel72)
                .addContainerGap(743, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(jLabel72)
                .addContainerGap())
        );

        AddSubjectPanel.add(jPanel1);
        jPanel1.setBounds(0, 260, 880, 50);

        jTextField9.setText("Search Subject By Name ...");
        jTextField9.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField9FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField9FocusLost(evt);
            }
        });
        jTextField9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField9KeyReleased(evt);
            }
        });
        AddSubjectPanel.add(jTextField9);
        jTextField9.setBounds(710, 40, 170, 22);

        jTextField25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField25ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jTextField25);
        jTextField25.setBounds(180, 130, 240, 22);

        jButton19.setText("Delete");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jButton19);
        jButton19.setBounds(170, 220, 70, 23);
        jButton19.setEnabled(false);

        jTextField26.setEnabled(false);
        AddSubjectPanel.add(jTextField26);
        jTextField26.setBounds(350, 80, 64, 22);

        jLabel31.setText("Id  :-");
        AddSubjectPanel.add(jLabel31);
        jLabel31.setBounds(310, 80, 30, 16);

        jButton10.setText("...");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jButton10);
        jButton10.setBounds(150, 400, 20, 23);

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jTextField5);
        jTextField5.setBounds(90, 400, 64, 22);

        jLabel16.setText("Select Subject  :-");
        AddSubjectPanel.add(jLabel16);
        jLabel16.setBounds(20, 350, 90, 16);

        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        AddSubjectPanel.add(jComboBox1);
        jComboBox1.setBounds(120, 350, 150, 22);

        tcjComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));
        tcjComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tcjComboBox2ItemStateChanged(evt);
            }
        });
        AddSubjectPanel.add(tcjComboBox2);
        tcjComboBox2.setBounds(410, 350, 150, 22);

        jLabel17.setText("Teacher Nic   :-");
        AddSubjectPanel.add(jLabel17);
        jLabel17.setBounds(590, 350, 90, 16);

        jLabel32.setText("Start At   :-");
        AddSubjectPanel.add(jLabel32);
        jLabel32.setBounds(20, 400, 70, 16);

        jLabel45.setText("End At  :-");
        AddSubjectPanel.add(jLabel45);
        jLabel45.setBounds(200, 400, 60, 16);
        AddSubjectPanel.add(jTextField28);
        jTextField28.setBounds(260, 400, 70, 22);

        jButton20.setText("...");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jButton20);
        jButton20.setBounds(330, 400, 20, 23);

        jLabel49.setText("Select Date  :-");
        AddSubjectPanel.add(jLabel49);
        jLabel49.setBounds(380, 400, 80, 16);

        jButton21.setText("...");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jButton21);
        jButton21.setBounds(590, 400, 20, 23);
        AddSubjectPanel.add(jTextField29);
        jTextField29.setBounds(470, 400, 120, 22);

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Subject", "Teacher NIC No", "Teacher Name", "Date", "Start At", "End At", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable4MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable4);

        AddSubjectPanel.add(jScrollPane4);
        jScrollPane4.setBounds(20, 490, 860, 130);

        jLabel58.setText("Status  :-");
        AddSubjectPanel.add(jLabel58);
        jLabel58.setBounds(660, 400, 50, 16);

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Held", "Canceld" }));
        jComboBox3.setEnabled(false);
        AddSubjectPanel.add(jComboBox3);
        jComboBox3.setBounds(720, 400, 160, 22);

        jButton22.setText("Save");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jButton22);
        jButton22.setBounds(570, 640, 70, 23);

        jButton23.setText("Update");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jButton23);
        jButton23.setBounds(650, 640, 70, 23);
        jButton23.setEnabled(false);

        jButton24.setText("Delete");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jButton24);
        jButton24.setBounds(730, 640, 70, 23);
        jButton24.setEnabled(false);

        jButton25.setText("Cancel");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jButton25);
        jButton25.setBounds(810, 640, 70, 23);

        jTextField30.setText("Search Class By Subject Name ...");
        jTextField30.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField30FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField30FocusLost(evt);
            }
        });
        jTextField30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField30ActionPerformed(evt);
            }
        });
        jTextField30.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField30KeyReleased(evt);
            }
        });
        AddSubjectPanel.add(jTextField30);
        jTextField30.setBounds(680, 450, 200, 22);

        jLabel59.setText("Select Teacher  :-");
        AddSubjectPanel.add(jLabel59);
        jLabel59.setBounds(310, 350, 90, 16);

        jTextField31.setEnabled(false);
        AddSubjectPanel.add(jTextField31);
        jTextField31.setBounds(680, 350, 200, 22);

        jLabel60.setText("Id  :-");
        AddSubjectPanel.add(jLabel60);
        jLabel60.setBounds(760, 310, 30, 16);

        jTextField32.setEnabled(false);
        jTextField32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField32ActionPerformed(evt);
            }
        });
        AddSubjectPanel.add(jTextField32);
        jTextField32.setBounds(800, 310, 80, 22);

        jLabel85.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel85.setText("Manage Subject");
        AddSubjectPanel.add(jLabel85);
        jLabel85.setBounds(20, 20, 140, 21);

        HomePanel.setLayout(null);

        jPanel4.setBackground(new java.awt.Color(204, 204, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel47.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel47.setText("All Students");

        jLabel73.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(jLabel47))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel47)
                .addGap(18, 18, 18)
                .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        HomePanel.add(jPanel4);
        jPanel4.setBounds(0, 70, 290, 190);

        jPanel8.setBackground(new java.awt.Color(255, 204, 204));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel34.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel34.setText(" All Teachers");

        jLabel74.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel74.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel34)
                .addGap(72, 72, 72))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel34)
                .addGap(18, 18, 18)
                .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        HomePanel.add(jPanel8);
        jPanel8.setBounds(300, 70, 280, 190);

        jPanel11.setBackground(new java.awt.Color(204, 255, 204));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel48.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel48.setText("All Subjects");

        jLabel75.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel75.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel48)
                .addGap(75, 75, 75))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel48)
                .addGap(18, 18, 18)
                .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
        );

        HomePanel.add(jPanel11);
        jPanel11.setBounds(590, 70, 280, 190);

        jLabel71.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel71.setText("Summery");
        HomePanel.add(jLabel71);
        jLabel71.setBounds(10, 20, 130, 21);

        jTable10.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Subject Name", "Teacher NIC", "Teacher Name", "Date", "Start At", "End At", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane10.setViewportView(jTable10);

        HomePanel.add(jScrollPane10);
        jScrollPane10.setBounds(10, 340, 850, 280);

        jLabel77.setText("2023 || Harvard University Admin Mode");
        HomePanel.add(jLabel77);
        jLabel77.setBounds(330, 650, 210, 16);

        jLabel78.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel78.setText("Toady Class Shedule");
        HomePanel.add(jLabel78);
        jLabel78.setBounds(330, 300, 220, 29);

        AddPaymentPanel.setLayout(null);

        jLabel33.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel33.setText("Add Payment ");
        AddPaymentPanel.add(jLabel33);
        jLabel33.setBounds(20, 20, 148, 21);

        AddPaymentPanel1.setLayout(null);

        jLabel43.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel43.setText("Add Payment  ....");
        AddPaymentPanel1.add(jLabel43);
        jLabel43.setBounds(34, 19, 148, 21);

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Student Nic", "Address", "Mobile", "Student Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(jTable7);
        if (jTable7.getColumnModel().getColumnCount() > 0) {
            jTable7.getColumnModel().getColumn(0).setPreferredWidth(1);
        }

        AddPaymentPanel1.add(jScrollPane7);
        jScrollPane7.setBounds(10, 310, 510, 190);

        jButton26.setText("Save Payment & Send Invoice");
        AddPaymentPanel1.add(jButton26);
        jButton26.setBounds(650, 640, 210, 23);

        jButton27.setText("Cancel");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });
        AddPaymentPanel1.add(jButton27);
        jButton27.setBounds(550, 640, 81, 23);

        jTable8.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Subject Name", "Subject Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(jTable8);
        if (jTable8.getColumnModel().getColumnCount() > 0) {
            jTable8.getColumnModel().getColumn(0).setPreferredWidth(1);
        }

        AddPaymentPanel1.add(jScrollPane8);
        jScrollPane8.setBounds(540, 310, 330, 190);

        jLabel30.setText("Strudent ID   :-");
        AddPaymentPanel1.add(jLabel30);
        jLabel30.setBounds(30, 140, 80, 16);
        AddPaymentPanel1.add(jTextField17);
        jTextField17.setBounds(110, 140, 70, 22);
        AddPaymentPanel1.add(jTextField18);
        jTextField18.setBounds(290, 140, 230, 22);

        jLabel44.setText("Strudent Name  :-");
        AddPaymentPanel1.add(jLabel44);
        jLabel44.setBounds(190, 140, 100, 16);
        AddPaymentPanel1.add(jTextField40);
        jTextField40.setBounds(140, 180, 120, 22);

        jLabel46.setText("Student Mobile   :-");
        AddPaymentPanel1.add(jLabel46);
        jLabel46.setBounds(30, 180, 100, 16);

        jLabel50.setText("Strudent ID   :-");
        AddPaymentPanel1.add(jLabel50);
        jLabel50.setBounds(270, 180, 80, 16);

        jTextField41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField41ActionPerformed(evt);
            }
        });
        AddPaymentPanel1.add(jTextField41);
        jTextField41.setBounds(350, 180, 170, 22);
        AddPaymentPanel1.add(jTextField42);
        jTextField42.setBounds(140, 220, 380, 22);

        jLabel51.setText("Strudent Address   :-");
        AddPaymentPanel1.add(jLabel51);
        jLabel51.setBounds(20, 220, 110, 16);

        jTextField19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField19ActionPerformed(evt);
            }
        });
        AddPaymentPanel1.add(jTextField19);
        jTextField19.setBounds(700, 280, 170, 22);

        jTextField20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField20ActionPerformed(evt);
            }
        });
        AddPaymentPanel1.add(jTextField20);
        jTextField20.setBounds(350, 280, 170, 22);

        AddPaymentPanel.setVisible(false);

        AddPaymentPanel.add(AddPaymentPanel1);
        AddPaymentPanel1.setBounds(0, 0, 0, 0);

        jLabel55.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel55.setText("Invoice Items");
        AddPaymentPanel.add(jLabel55);
        jLabel55.setBounds(20, 390, 130, 21);

        jTable9.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Teacher ID", "Subject ID", "Teacher Name", "Subject Name", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(jTable9);

        AddPaymentPanel.add(jScrollPane9);
        jScrollPane9.setBounds(10, 450, 670, 180);

        jLabel61.setText("Item Count  :-");
        AddPaymentPanel.add(jLabel61);
        jLabel61.setBounds(690, 460, 80, 20);

        jLabel62.setText("Total :-");
        AddPaymentPanel.add(jLabel62);
        jLabel62.setBounds(690, 480, 40, 20);

        jLabel63.setText("Invoice No");
        AddPaymentPanel.add(jLabel63);
        jLabel63.setBounds(680, 400, 60, 16);

        jTextField45.setEnabled(false);
        AddPaymentPanel.add(jTextField45);
        jTextField45.setBounds(750, 400, 120, 22);

        jLabel64.setText("Discount  :-");
        AddPaymentPanel.add(jLabel64);
        jLabel64.setBounds(690, 520, 70, 16);

        jTextField46.setText("0");
        jTextField46.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField46KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField46KeyReleased(evt);
            }
        });
        AddPaymentPanel.add(jTextField46);
        jTextField46.setBounds(760, 520, 80, 22);

        jLabel65.setText("Grand Price  :-");
        AddPaymentPanel.add(jLabel65);
        jLabel65.setBounds(690, 590, 80, 16);

        jLabel66.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel66.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        AddPaymentPanel.add(jLabel66);
        jLabel66.setBounds(770, 590, 100, 20);
        AddPaymentPanel.add(jLabel67);
        jLabel67.setBounds(770, 460, 100, 20);
        AddPaymentPanel.add(jLabel68);
        jLabel68.setBounds(770, 480, 100, 20);

        jButton13.setText("Make Payment & Print Invoice");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        AddPaymentPanel.add(jButton13);
        jButton13.setBounds(560, 650, 200, 23);

        jButton14.setText("Cancel");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        AddPaymentPanel.add(jButton14);
        jButton14.setBounds(780, 650, 81, 23);

        jLabel70.setText("%");
        AddPaymentPanel.add(jLabel70);
        jLabel70.setBounds(850, 520, 10, 20);
        AddPaymentPanel.add(jSeparator1);
        jSeparator1.setBounds(0, 373, 880, 10);

        jLabel89.setText("Student NIC :-");
        AddPaymentPanel.add(jLabel89);
        jLabel89.setBounds(20, 70, 80, 16);

        jLabel90.setText("Name :-");
        AddPaymentPanel.add(jLabel90);
        jLabel90.setBounds(240, 70, 50, 16);

        jTextField48.setEnabled(false);
        AddPaymentPanel.add(jTextField48);
        jTextField48.setBounds(290, 70, 190, 22);

        jTable14.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Subject ID", "Subject Name", "Teacher ID", "Teacher Name", "Price", "Payment Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable14MouseClicked(evt);
            }
        });
        jScrollPane14.setViewportView(jTable14);

        AddPaymentPanel.add(jScrollPane14);
        jScrollPane14.setBounds(10, 150, 860, 190);

        jTextField13.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField13KeyReleased(evt);
            }
        });
        AddPaymentPanel.add(jTextField13);
        jTextField13.setBounds(100, 70, 120, 22);

        jLabel92.setText("Mobile :-");
        AddPaymentPanel.add(jLabel92);
        jLabel92.setBounds(500, 70, 50, 16);

        jTextField50.setEnabled(false);
        AddPaymentPanel.add(jTextField50);
        jTextField50.setBounds(560, 70, 130, 22);

        jButton11.setText("Search");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        AddPaymentPanel.add(jButton11);
        jButton11.setBounds(700, 110, 81, 23);

        jButton12.setText("Cancel");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        AddPaymentPanel.add(jButton12);
        jButton12.setBounds(800, 110, 72, 23);
        AddPaymentPanel.add(jSeparator2);
        jSeparator2.setBounds(680, 560, 200, 3);

        jLabel93.setText("Student ID :-");
        AddPaymentPanel.add(jLabel93);
        jLabel93.setBounds(710, 70, 70, 16);

        jTextField51.setEnabled(false);
        AddPaymentPanel.add(jTextField51);
        jTextField51.setBounds(790, 70, 80, 22);

        AddPaymentPanel.setVisible(false);

        AttendancePanel.setLayout(null);

        jTable11.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Teacher NIC", "Teacher", "Subject ID", "Subject", "Start At", "End At"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable11MouseClicked(evt);
            }
        });
        jScrollPane11.setViewportView(jTable11);
        if (jTable11.getColumnModel().getColumnCount() > 0) {
            jTable11.getColumnModel().getColumn(0).setPreferredWidth(1);
        }

        AttendancePanel.add(jScrollPane11);
        jScrollPane11.setBounds(10, 110, 500, 170);

        jTable12.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nic No", "Student Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable12MouseClicked(evt);
            }
        });
        jScrollPane12.setViewportView(jTable12);
        if (jTable12.getColumnModel().getColumnCount() > 0) {
            jTable12.getColumnModel().getColumn(0).setPreferredWidth(1);
        }

        AttendancePanel.add(jScrollPane12);
        jScrollPane12.setBounds(530, 110, 340, 170);

        jLabel79.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel79.setText("Select Student ....");
        AttendancePanel.add(jLabel79);
        jLabel79.setBounds(530, 80, 100, 16);

        jLabel81.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel81.setText("Attendance Mark Students");
        AttendancePanel.add(jLabel81);
        jLabel81.setBounds(10, 370, 160, 20);

        jTable13.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Subject ID", "Subject", "Teacher ID", "Teacher", "Student Nic", "Student Name", "Date", "Status", "StartAt", "EndAt"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane13.setViewportView(jTable13);

        AttendancePanel.add(jScrollPane13);
        jScrollPane13.setBounds(10, 400, 860, 270);

        jLabel86.setText("Status  :-");
        AttendancePanel.add(jLabel86);
        jLabel86.setBounds(490, 310, 50, 20);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Present", "Absent" }));
        jComboBox2.setEnabled(false);
        AttendancePanel.add(jComboBox2);
        jComboBox2.setBounds(540, 310, 140, 22);

        jButton17.setText("Add");
        jButton17.setEnabled(false);
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });
        AttendancePanel.add(jButton17);
        jButton17.setBounds(700, 310, 72, 23);

        jButton18.setText("Cancel");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });
        AttendancePanel.add(jButton18);
        jButton18.setBounds(790, 310, 72, 23);

        jLabel84.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel84.setText("Select Today Class ");
        AttendancePanel.add(jLabel84);
        jLabel84.setBounds(10, 80, 110, 20);

        jLabel69.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel69.setText("Attendance");
        AttendancePanel.add(jLabel69);
        jLabel69.setBounds(10, 20, 130, 21);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(MenuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(AddStudentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 901, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 203, Short.MAX_VALUE)
                    .addComponent(HomePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 897, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 202, Short.MAX_VALUE)
                    .addComponent(AddTeacherPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 898, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 203, Short.MAX_VALUE)
                    .addComponent(AddSubjectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 897, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 203, Short.MAX_VALUE)
                    .addComponent(AddPaymentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 897, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 204, Short.MAX_VALUE)
                    .addComponent(AttendancePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 896, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(AddStudentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
            .addComponent(MenuPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(HomePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(AddTeacherPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(AddSubjectPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(AddPaymentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AttendancePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        AddStudentPanel.setVisible(false);
        AddTeacherPanel.setVisible(false);
        AttendancePanel.setVisible(false);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        HomePanel.setVisible(false);
        AddTeacherPanel.setVisible(false);
        AddSubjectPanel.setVisible(false);
        AddSubjectPanel.setVisible(false);
        AddPaymentPanel.setVisible(false);
        AttendancePanel.setVisible(false);

//        container
        loadStudentsTable();
//        container
        AddStudentPanel.setVisible(true);
    }//GEN-LAST:event_jPanel6MouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked
        AddStudentPanel.setVisible(false);
        AddTeacherPanel.setVisible(false);
        AddSubjectPanel.setVisible(false);
        AddSubjectPanel.setVisible(false);
        AddPaymentPanel.setVisible(false);
        AttendancePanel.setVisible(false);

//        Content
        HomeObjectCount();
        loadHomeClassSheduleTable();
//        Content
        HomePanel.setVisible(true);
    }//GEN-LAST:event_jPanel5MouseClicked

    private void jPanel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseClicked
        AddStudentPanel.setVisible(false);
        AddTeacherPanel.setVisible(false);
        HomePanel.setVisible(false);
        AddSubjectPanel.setVisible(false);
        AddPaymentPanel.setVisible(false);
        AttendancePanel.setVisible(false);

//        Content
        loadSubjectsTable();
        loadClassSheduleTable();

        try {
            ResultSet resultSet = MySQL.Search("SELECT * FROM `subject`");

            Vector<String> vector = new Vector<String>();
            vector.add("Select");

            while (resultSet.next()) {
                vector.add(resultSet.getString("description"));
            }

            DefaultComboBoxModel Smodel = new DefaultComboBoxModel(vector);
            jComboBox1.setModel(Smodel);

        } catch (Exception e) {
            e.printStackTrace();
        }
//        Content
        AddSubjectPanel.setVisible(true);
    }//GEN-LAST:event_jPanel7MouseClicked

    private void jPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseClicked
        AddStudentPanel.setVisible(false);
        HomePanel.setVisible(false);
        AddSubjectPanel.setVisible(false);
        AddPaymentPanel.setVisible(false);
        AttendancePanel.setVisible(false);

        //Content
        //LoadSubjectComboBox
        try {
            ResultSet resultSet = MySQL.Search("SELECT * FROM `subject` ");

            Vector<String> vector = new Vector<String>();
            vector.add("Select");

            while (resultSet.next()) {
                vector.add(resultSet.getString("description"));
            }

            DefaultComboBoxModel Combomodel = new DefaultComboBoxModel(vector);
            jComboBox6.setModel(Combomodel);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //LoadSubjectComboBox
        loadTeachersTable();
        //Content
        AddTeacherPanel.setVisible(true);
    }//GEN-LAST:event_jPanel2MouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String status = validateTeacherField();
        if (status.equals("Success")) {
            String tno = jTextField24.getText();
            String subjectName = String.valueOf(jComboBox6.getSelectedItem());

            try {
                ResultSet resultSet = MySQL.Search("SELECT * FROM `subject` WHERE `description`='" + subjectName + "' ");

                if (resultSet.next()) {
                    MySQL.Iud("UPDATE `teacher` "
                            + "SET"
                            + " `firstName`='" + jTextField35.getText() + "',"
                            + " `lastName`='" + jTextField27.getText() + "',"
                            + " `mobile`='" + jTextField37.getText() + "',"
                            + " `address`='" + jTextField36.getText() + "',"
                            + " `subject_subno`='" + resultSet.getInt("subno") + "' "
                            + "WHERE"
                            + " `tno`='" + tno + "' ");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            loadTeachersTable();
            ManageTeacherFieldReset();

        } else {
            Alert(status, true);
        }

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        subjectFieldReset();
        loadSubjectsTable();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed

        String status = ValidateManageSubjectsField();
        String subId = jTextField26.getText();
        String description = jTextField25.getText();
        String price = jTextField4.getText();

        if (status == "Success") {
            MySQL.Iud("UPDATE `subject` SET `description`='" + description + "', `price`='" + price + "' WHERE `subno`='" + subId + "' ");

            subjectFieldReset();
            loadSubjectsTable();
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jPanel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel10MouseClicked
        HomePanel.setVisible(false);
        AddTeacherPanel.setVisible(false);
        AddSubjectPanel.setVisible(false);
        AddStudentPanel.setVisible(false);
        AttendancePanel.setVisible(false);

        jTextField45.setText(generateUniqueId());
        AddPaymentPanel.setVisible(true);
    }//GEN-LAST:event_jPanel10MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        String fieldStatus = valdateStudentsFields();

        if (fieldStatus.equals("Success")) {
            String firstName = jTextField1.getText();
            String lastName = jTextField2.getText();
            String dob = jTextField33.getText();
            String address = jTextField6.getText();
            String mobile = jTextField7.getText();
            String nic = jTextField11.getText();
            String gender = String.valueOf(jComboBox5.getSelectedItem());
            String result = (gender.equals("Male")) ? "1" : "2";

            try {
                ResultSet resultSet = MySQL.Search("SELECT * FROM `student` WHERE `nic`='" + nic + "' ");

                if (resultSet.next()) {
                    Alert("This Student Already Exist", false);

                } else {
                    MySQL.Iud("INSERT INTO "
                            + "`student`(`firstName`,`lastName`,`dob`,`address`,`mobile`,`nic`,`gender_id`) "
                            + "VALUES('" + firstName + "','" + lastName + "','" + dob + "','" + address + "',"
                            + "'" + mobile + "','" + nic + "','" + result + "')");

                    loadStudentsTable();
                    ManageStudentFieldReset();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Alert(fieldStatus, true);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        if (evt.getClickCount() == 2) {
            int row = jTable2.getSelectedRow();

            if (row != -1) {
                jTextField8.setText(String.valueOf(jTable2.getValueAt(row, 0)));
                jTextField11.setText(String.valueOf(jTable2.getValueAt(row, 1)));
                jTextField7.setText(String.valueOf(jTable2.getValueAt(row, 2)));
                jTextField1.setText(String.valueOf(jTable2.getValueAt(row, 3)));
                jTextField2.setText(String.valueOf(jTable2.getValueAt(row, 4)));
                jComboBox5.setSelectedItem(jTable2.getValueAt(row, 5));
                jTextField33.setText(String.valueOf(jTable2.getValueAt(row, 6)));
                jTextField6.setText(String.valueOf(jTable2.getValueAt(row, 7)));

                jTextField33.setEnabled(false);
                jTextField11.setEnabled(false);
                jTable2.setEnabled(false);
                jButton2.setEnabled(false);
                jComboBox5.setEnabled(false);
                jButton15.setEnabled(true);
                jButton6.setEnabled(true);

            } else {
                jTable2.setEnabled(true);
                jButton2.setEnabled(true);
            }
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ManageStudentFieldReset();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField10KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField10KeyReleased
        try {

            ResultSet resultset = MySQL.Search("SELECT * FROM `student` WHERE `nic` LIKE '%" + jTextField10.getText() + "%'");
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);

            while (resultset.next()) {
                Vector<String> studentTableRow = new Vector<>();
                studentTableRow.add(resultset.getString("sno"));
                studentTableRow.add(resultset.getString("nic"));
                studentTableRow.add(resultset.getString("mobile"));
                studentTableRow.add(resultset.getString("firstName"));
                studentTableRow.add(resultset.getString("lastName"));
                studentTableRow.add(resultset.getString("dob"));
                studentTableRow.add(resultset.getString("address"));
                model.addRow(studentTableRow);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jTextField10KeyReleased

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        String status = valdateStudentsFields();
        if (status.equals("Success")) {
            MySQL.Iud(" UPDATE `student`"
                    + " SET `firstName`='" + jTextField1.getText() + "',"
                    + "`lastName`='" + jTextField2.getText() + "',"
                    + "`address`='" + jTextField6.getText() + "',"
                    + "`mobile`='" + jTextField7.getText() + "'"
                    + " WHERE `sno`='" + Integer.valueOf(jTextField8.getText()) + "'");

            ManageStudentFieldReset();
        } else {
            Alert(status, true);
        }


    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        MySQL.Iud("DELETE FROM `student` WHERE `sno`='" + Integer.valueOf(jTextField8.getText()) + "' ");
        ManageStudentFieldReset();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String status = validateTeacherField();
        if (status.equals("Success")) {
            String subjectName = String.valueOf(jComboBox6.getSelectedItem());

            try {
                ResultSet resultSet = MySQL.Search("SELECT * FROM `teacher` WHERE `nic`='" + jTextField38.getText() + "' ");

                if (resultSet.next()) {
                    Alert("This Teacher Already Exist", false);

                } else {
                    ResultSet result = MySQL.Search("SELECT * FROM `subject` WHERE `description`='" + subjectName + "' ");

                    if (result.next()) {
                        MySQL.Iud("INSERT INTO `teacher`"
                                + "(`firstName`,`lastName`,`mobile`,`nic`,`address`,`gender_id`,`subject_subno`)"
                                + "VALUES"
                                + "('" + jTextField35.getText() + "','" + jTextField27.getText() + "',"
                                + "'" + jTextField37.getText() + "',"
                                + "'" + jTextField38.getText() + "','" + jTextField36.getText() + "',"
                                + "'" + Integer.valueOf(buttonGroup1.getSelection().getActionCommand()) + "',"
                                + "'" + result.getInt("subno") + "')");

                        ManageTeacherFieldReset();
                        loadTeachersTable();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert(status, true);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2) {
            int teacherRow = jTable1.getSelectedRow();

            if (teacherRow != -1) {
                jTextField24.setText(String.valueOf(jTable1.getValueAt(teacherRow, 0)));
                jComboBox6.setSelectedItem(String.valueOf(jTable1.getValueAt(teacherRow, 3)));
                jTextField35.setText(String.valueOf(jTable1.getValueAt(teacherRow, 4)));
                jTextField27.setText(String.valueOf(jTable1.getValueAt(teacherRow, 5)));
                jTextField38.setText(String.valueOf(jTable1.getValueAt(teacherRow, 2)));
                jTextField37.setText(String.valueOf(jTable1.getValueAt(teacherRow, 1)));
                boolean condition = String.valueOf(jTable1.getValueAt(teacherRow, 6)).equalsIgnoreCase("Male") ? true : false;
                if (condition) {
                    jRadioButton1.setSelected(true);
                } else {
                    jRadioButton2.setSelected(true);
                }
                jTextField36.setText(String.valueOf(jTable1.getValueAt(teacherRow, 7)));
                jRadioButton1.setEnabled(false);
                jRadioButton2.setEnabled(false);
                jTextField38.setEnabled(false);
                jTable1.setEnabled(false);
                jButton3.setEnabled(false);
                jButton16.setEnabled(true);
                jButton5.setEnabled(true);
            }

        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        ManageTeacherFieldReset();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        MySQL.Iud("DELETE FROM `teacher` WHERE `tno`='" + jTextField24.getText() + "' ");
        ManageTeacherFieldReset();
        loadTeachersTable();
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jTextField12KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField12KeyReleased
        if (jTextField12.getText().length() != 0) {
            try {
                ResultSet resultset = MySQL.Search("SELECT * FROM `teacher` WHERE `nic` LIKE '%" + jTextField12.getText() + "%'");
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                model.setRowCount(0);

                while (resultset.next()) {
                    Vector<String> row = new Vector<>();
                    row.add(resultset.getString("tno"));
                    row.add(resultset.getString("mobile"));
                    row.add(resultset.getString("nic"));
                    row.add(resultset.getString("firstName"));
                    row.add(resultset.getString("lastName"));
                    row.add((resultset.getString("gender_id")).equals("1") ? "Male" : "Female");
                    row.add(resultset.getString("address"));
                    model.addRow(row);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            loadTeachersTable();
        }
    }//GEN-LAST:event_jTextField12KeyReleased

    private void jTextField25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField25ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField25ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        String status = ValidateManageSubjectsField();
        if (status == "Success") {
            MySQL.Iud("INSERT INTO `subject`(`description`,`price`)"
                    + " VALUES ('" + jTextField25.getText() + "','" + jTextField4.getText() + "') ");
            subjectFieldReset();
            loadSubjectsTable();

        } else {
            Alert(status, true);
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        if (evt.getClickCount() == 2) {
            int tableRow = jTable3.getSelectedRow();

            if (tableRow != -1) {
                jTextField26.setText(String.valueOf(jTable3.getValueAt(tableRow, 0)));
                jTextField25.setText(String.valueOf(jTable3.getValueAt(tableRow, 1)));
                jTextField4.setText(String.valueOf(jTable3.getValueAt(tableRow, 2)));

                SubjectName = String.valueOf(jTable3.getValueAt(tableRow, 1));

                jButton9.setEnabled(false);
                jTable3.setEnabled(false);
                jButton19.setEnabled(true);
                jButton7.setEnabled(true);

            }

        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        MySQL.Iud("DELETE FROM `subject` WHERE `subno`='" + jTextField26.getText() + "'");

        subjectFieldReset();
        loadSubjectsTable();
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jTextField9KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField9KeyReleased
        if (jTextField9.getText().length() != 0) {
            try {
                ResultSet resultSet = MySQL.Search("SELECT * FROM `subject` WHERE `description` LIKE '%" + jTextField9.getText() + "%' ");
                DefaultTableModel tModel = (DefaultTableModel) jTable3.getModel();
                tModel.setRowCount(0);
                while (resultSet.next()) {
                    Vector<String> row = new Vector<>();
                    row.add(resultSet.getString("subno"));
                    row.add(resultSet.getString("description"));
                    row.add(resultSet.getString("price"));
                    tModel.addRow(row);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            loadSubjectsTable();
        }
    }//GEN-LAST:event_jTextField9KeyReleased

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        timePicker1.showPopup(this, 50, 50);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        timePicker2.showPopup(this, 50, 50);
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        dateChooser1.showPopup();
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        String status = validateAddClassFields();

        if (status == "Success") {

            try {
                ResultSet result = MySQL.Search("SELECT * FROM `subject` "
                        + "INNER JOIN `teacher` ON `teacher`.`subject_subno`=`subject`.`subno` "
                        + "WHERE `description`='" + String.valueOf(jComboBox1.getSelectedItem()) + "' "
                        + "AND `nic`='" + jTextField31.getText() + "' ");

                if (result.next()) {
                    String statusId = String.valueOf(jComboBox3.getSelectedItem()).equals("Held") ? "1" : "2";
                    MySQL.Iud("INSERT INTO `class`(`teacher_tno`,`subject_subno`,`date`,`startAt`,`endAt`,`status`) "
                            + "VALUES('" + result.getString("tno") + "','" + result.getString("subno") + "',"
                            + "'" + jTextField29.getText() + "','" + jTextField5.getText() + "',"
                            + "'" + jTextField28.getText() + "','" + statusId + "')");

                    resetAllAddClassFields();
                    loadClassSheduleTable();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Alert(status, true);
        }
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        String Subject = String.valueOf(jComboBox1.getSelectedItem());

        try {
            ResultSet resultSet = MySQL.Search("SELECT `subno` FROM `subject` WHERE `description`='" + Subject + "' ");

            if (resultSet.next()) {
                int subNo = resultSet.getInt("subno");

                ResultSet result = MySQL.Search("SELECT * FROM `teacher` WHERE `subject_subno`='" + subNo + "' ");

                Vector<String> vector = new Vector<>();
                vector.add("Select");

                while (result.next()) {
                    vector.add(result.getString("firstName"));
                }

                DefaultComboBoxModel tModel = new DefaultComboBoxModel(vector);
                tcjComboBox2.setModel(tModel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void tcjComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tcjComboBox2ItemStateChanged
        String teacherName = String.valueOf(tcjComboBox2.getSelectedItem());
        String selectedTeacherNicNo = null;

        try {

            ResultSet result = MySQL.Search("SELECT * FROM `teacher` WHERE `firstName`='" + teacherName + "' ");

            if (result.next()) {
                selectedTeacherNicNo = result.getString("nic");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        jTextField31.setText(selectedTeacherNicNo);

    }//GEN-LAST:event_tcjComboBox2ItemStateChanged

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        resetAllAddClassFields();
        jComboBox1.setEnabled(true);
        tcjComboBox2.setEnabled(true);
        jTable4.setEnabled(true);
        jComboBox3.setEnabled(false);
        loadClassSheduleTable();
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        String status = validateAddClassFields();

        if (status.equals("Success")) {
            String statusId = String.valueOf(jComboBox3.getSelectedItem()).equals("Held") ? "1" : "2";
            MySQL.Iud("UPDATE `class` SET `date`='" + jTextField29.getText() + "',"
                    + " `startAt`='" + jTextField5.getText() + "', `endAt`='" + jTextField28.getText() + "', "
                    + "`status`='" + statusId + "' WHERE `id`='" + jTextField32.getText() + "' ");

            loadClassSheduleTable();
            resetAllAddClassFields();
            jButton23.setEnabled(false);
            jButton24.setEnabled(false);

        } else {
            Alert(status, true);
        }
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jTextField32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField32ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField32ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        MySQL.Iud("DELETE FROM `class` WHERE `id`='" + jTextField32.getText() + "' ");
        loadClassSheduleTable();
        resetAllAddClassFields();
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jTable4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable4MouseClicked
        if (evt.getClickCount() == 2) {
            int row = jTable4.getSelectedRow();

            if (row != -1) {
                jTextField32.setText(String.valueOf(jTable4.getValueAt(row, 0)));
                jComboBox1.setSelectedItem(String.valueOf(jTable4.getValueAt(row, 1)));
                jTextField31.setText(String.valueOf(jTable4.getValueAt(row, 2)));
                String[] tname = String.valueOf(jTable4.getValueAt(row, 3)).split(" ");
                String fname = tname[0];
                tcjComboBox2.setSelectedItem(fname);
                jTextField29.setText(String.valueOf(jTable4.getValueAt(row, 4)));
                jTextField5.setText(String.valueOf(jTable4.getValueAt(row, 5)));
                jTextField28.setText(String.valueOf(jTable4.getValueAt(row, 6)));
                jComboBox3.setSelectedItem(String.valueOf(jTable4.getValueAt(row, 7)));

                jComboBox1.setEnabled(false);
                tcjComboBox2.setEnabled(false);
                jTable4.setEnabled(false);
                jButton22.setEnabled(false);
                jComboBox3.setEnabled(true);
                jButton23.setEnabled(true);
                jButton24.setEnabled(true);
            } else {
                jComboBox1.setEnabled(true);
                tcjComboBox2.setEnabled(true);
                jTable4.setEnabled(true);
                jButton22.setEnabled(true);
                jComboBox3.setEnabled(false);
                jButton23.setEnabled(false);
                jButton24.setEnabled(false);

            }

        }
    }//GEN-LAST:event_jTable4MouseClicked

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jTextField41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField41ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField41ActionPerformed

    private void jTextField19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField19ActionPerformed

    private void jTextField20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField20ActionPerformed

    private void jTextField46KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField46KeyPressed

    }//GEN-LAST:event_jTextField46KeyPressed

    private void jTextField46KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField46KeyReleased
        double total = Double.parseDouble(jLabel68.getText());
        double precentage;
        if (jTextField46.getText().isEmpty()) {
            precentage = 0;
        } else {
            precentage = Double.parseDouble(jTextField46.getText());
        }

        double discount = (total * precentage) / 100;
        jLabel66.setText(String.valueOf(total - discount));

    }//GEN-LAST:event_jTextField46KeyReleased

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        jTextField13.setText("");
        jTextField48.setText("");
        jTextField50.setText("");
        jTextField51.setText("");

        DefaultTableModel model = (DefaultTableModel) jTable14.getModel();
        model.setRowCount(0);
        resetAllPaymentFieldsTables();
        jTextField45.setText(generateUniqueId());
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String stringDate = format.format(date);
        String invoiceNo = jTextField45.getText();
        String sno = jTextField51.getText();

        MySQL.Iud("INSERT INTO `invoice` VALUES('" + invoiceNo + "','" + sno + "','" + stringDate + "','" + jTextField46.getText() + "', '" + jLabel68.getText() + "') ");
        for (int i = 0; i < jTable9.getRowCount(); i++) {
            String teacherId = String.valueOf(jTable9.getValueAt(i, 0));
            MySQL.Iud("INSERT INTO `invoice_item`(`teacher_tno`,`invoice_id`) VALUES('" + teacherId + "','" + invoiceNo + "')");
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("Parameter1", stringDate);
        parameters.put("Parameter2", jTextField51.getText());
        parameters.put("Parameter3", jTextField48.getText());
        parameters.put("Parameter4", jTextField13.getText());
        parameters.put("Parameter5", jTextField50.getText());
        parameters.put("Parameter6", invoiceNo);
        parameters.put("Parameter7", jLabel67.getText());
        parameters.put("Parameter8", jLabel68.getText());
        parameters.put("Parameter9", jTextField46.getText() + "%");
        parameters.put("Parameter10", jLabel66.getText());

        try {
            JRTableModelDataSource dataSource = new JRTableModelDataSource(jTable9.getModel());
            JasperPrint report = JasperFillManager.fillReport("src/reports/java2Assignment.jasper", parameters, dataSource);
            JasperViewer.viewReport(report, false);

            jTextField13.setText("");
            jTextField48.setText("");
            jTextField50.setText("");
            jTextField51.setText("");

            DefaultTableModel model = (DefaultTableModel) jTable14.getModel();
            model.setRowCount(0);
            resetAllPaymentFieldsTables();
            jTextField45.setText(generateUniqueId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jTextField10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField10FocusGained
        jTextField10.setText("");
    }//GEN-LAST:event_jTextField10FocusGained

    private void jTextField10FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField10FocusLost
        if (jTextField10.getText().isEmpty()) {
            jTextField10.setText("Search Students By NIC No.....");
        }
    }//GEN-LAST:event_jTextField10FocusLost

    private void jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField10ActionPerformed

    private void jTextField12FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField12FocusGained
        jTextField12.setText("");
    }//GEN-LAST:event_jTextField12FocusGained

    private void jTextField12FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField12FocusLost
        if (jTextField12.getText().isEmpty()) {
            jTextField12.setText("Search Teachers By NIC No .....");
        }
    }//GEN-LAST:event_jTextField12FocusLost

    private void jTextField9FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField9FocusGained
        jTextField9.setText("");
    }//GEN-LAST:event_jTextField9FocusGained

    private void jTextField9FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField9FocusLost
        if (jTextField9.getText().isEmpty()) {
            jTextField9.setText("Search Subject By Name ...");
        }
    }//GEN-LAST:event_jTextField9FocusLost

    private void jTextField30FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField30FocusGained
        jTextField30.setText("");
    }//GEN-LAST:event_jTextField30FocusGained

    private void jTextField30FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField30FocusLost
        if (jTextField30.getText().isEmpty()) {
            jTextField30.setText("Search Subject By Name ...");
        }
    }//GEN-LAST:event_jTextField30FocusLost

    private void jTextField30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField30ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField30ActionPerformed

    private void jTextField30KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField30KeyReleased
        try {

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String stringdate = format.format(date);

            ResultSet resultSet = MySQL.Search("SELECT * FROM `class` "
                    + "INNER JOIN `subject` ON `subject`.`subno`=`class`.`subject_subno` "
                    + "INNER JOIN `teacher` ON `teacher`.`tno`=`class`.`teacher_tno` "
                    + "WHERE `description` LIKE '%" + jTextField30.getText() + "%' "
                    + "AND `date`='" + stringdate + "' ORDER BY `date` DESC ");
            DefaultTableModel tmodel = (DefaultTableModel) jTable4.getModel();
            tmodel.setRowCount(0);

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("class.id"));
                vector.add(resultSet.getString("subject.description"));
                vector.add(resultSet.getString("nic"));
                vector.add(resultSet.getString("firstName") + " " + resultSet.getString("lastName"));
                vector.add(resultSet.getString("date"));
                vector.add(resultSet.getString("startAt"));
                vector.add(resultSet.getString("endAt"));
                vector.add(resultSet.getString("status"));
                tmodel.addRow(vector);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jTextField30KeyReleased

    private void jTable11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable11MouseClicked
        if (evt.getClickCount() == 1) {

            int selectedRowIndex = jTable11.getSelectedRow();

            if (selectedRowIndex != -1) {

                loadAttendanceStudentTable(jTable11.getValueAt(selectedRowIndex, 3).toString());
                loadAttendanceMarkedTable();

            }

        }
    }//GEN-LAST:event_jTable11MouseClicked

    private void jPanel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel13MouseClicked
        AddStudentPanel.setVisible(false);
        AddTeacherPanel.setVisible(false);
        AddSubjectPanel.setVisible(false);
        AddSubjectPanel.setVisible(false);
        AddPaymentPanel.setVisible(false);

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String stringToday = format.format(date);

        loadAttendanceTeacherTable("SELECT * FROM `subject` \n"
                + "INNER JOIN `class` ON `subject`.`subno`=`class`.`subject_subno`\n"
                + "INNER JOIN `teacher` ON `class`.`teacher_tno`=`teacher`.`tno` \n"
                + "WHERE `status`!='Canceld' AND `date`='" + stringToday + "'");

        HomePanel.setVisible(false);
        AttendancePanel.setVisible(true);
    }//GEN-LAST:event_jPanel13MouseClicked

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed

        if (jTable12.getSelectedRow() != -1) {
            String classId = jTable11.getValueAt(jTable11.getSelectedRow(), 0).toString();
            String sno = jTable12.getValueAt(jTable12.getSelectedRow(), 0).toString();
            String status = "Present".equals(jComboBox2.getSelectedItem().toString()) ? "1" : "2";

            MySQL.Iud("INSERT INTO `attendance` (`class_id`,`student_sno`,`status`) "
                    + "VALUES ('" + classId + "','" + sno + "','" + status + "') ");
            int selectedRowIndex = jTable11.getSelectedRow();

            if (selectedRowIndex != -1) {
                loadAttendanceStudentTable(jTable11.getValueAt(selectedRowIndex, 3).toString());
                loadAttendanceMarkedTable();

            }
            resetAllAttendancePage();
        } else {
            JOptionPane.showMessageDialog(this, "Please Select Class", "Error Message", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jTextField13KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField13KeyReleased
        try {

            ResultSet resultSet = MySQL.Search("SELECT * FROM `student` WHERE `nic` LIKE '" + jTextField13.getText() + "' ");

            while (resultSet.next()) {
                jTextField48.setText(resultSet.getString("firstName") + " " + resultSet.getString("lastName"));
                jTextField50.setText(resultSet.getString("mobile"));
                jTextField51.setText(resultSet.getString("sno"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jTextField13KeyReleased

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        try {
            DefaultTableModel model = (DefaultTableModel) jTable14.getModel();
            model.setRowCount(0);
            int no = 0;

            ResultSet resultSet = MySQL.Search("SELECT * FROM `Subject` INNER JOIN `teacher` ON `teacher`.`subject_subno`=`subject`.`subno` ");

            while (resultSet.next()) {
                no += 1;
                Vector<String> vector = new Vector<String>();
                vector.add(String.valueOf(no));
                vector.add(resultSet.getString("subno"));
                vector.add(resultSet.getString("description"));
                vector.add(resultSet.getString("tno"));
                vector.add(resultSet.getString("firstName") + " " + resultSet.getString("lastName"));
                vector.add(resultSet.getString("price"));
                ResultSet result = MySQL.Search("SELECT * FROM `invoice` "
                        + "INNER JOIN `invoice_item` ON `invoice`.`id`=`invoice_item`.`invoice_id` "
                        + "WHERE `student_sno`='" + jTextField51.getText() + "' AND `teacher_tno`='" + resultSet.getInt("tno") + "' ");
                if (result.next()) {
                    vector.add("Paid");

                } else {
                    vector.add("Not Paid");
                }
                model.addRow(vector);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        jTextField13.setText("");
        jTextField48.setText("");
        jTextField50.setText("");
        jTextField51.setText("");

        DefaultTableModel model = (DefaultTableModel) jTable14.getModel();
        model.setRowCount(0);

    }//GEN-LAST:event_jButton12ActionPerformed

    private void jTable14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable14MouseClicked
        int total = 0;
        if (evt.getClickCount() == 2) {
            DefaultTableModel model = (DefaultTableModel) jTable9.getModel();
            int row = jTable14.getSelectedRow();

            if (row != -1) {
                if (String.valueOf(jTable14.getValueAt(row, 6)).equals("Paid")) {
                    Alert("This Subject Already Paid", true);
                } else {
                    Vector<String> vector = new Vector<String>();
                    vector.add(String.valueOf(jTable14.getValueAt(row, 3)));
                    vector.add(String.valueOf(jTable14.getValueAt(row, 1)));
                    vector.add(String.valueOf(jTable14.getValueAt(row, 4)));
                    vector.add(String.valueOf(jTable14.getValueAt(row, 2)));
                    vector.add(String.valueOf(jTable14.getValueAt(row, 5)));

                    model.addRow(vector);
                    ((DefaultTableModel) jTable14.getModel()).removeRow(row);

                    for (int i = 0; i < jTable9.getRowCount(); i++) {
                        double price = Double.parseDouble(jTable9.getValueAt(i, 4).toString());
                        total += price;
                    }
                }
            }

            jLabel68.setText(String.valueOf(total));
            jLabel67.setText(String.valueOf(jTable9.getRowCount()));

            double persentage = 0;
            jLabel66.setText(String.valueOf(persentage));
            if (jTextField46.getText().isEmpty()) {
                persentage = 0;
            } else {
                persentage = Double.parseDouble(jTextField46.getText());
            }
            double discount = (total * persentage) / 100;
            jLabel66.setText(String.valueOf(total - discount));

        }
    }//GEN-LAST:event_jTable14MouseClicked

    private void jTable12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable12MouseClicked
        int selectedRow = jTable12.getSelectedRow();

        if (selectedRow != -1) {
            jTable12.setEnabled(false);
            System.out.println("selected");
            jComboBox2.setEnabled(true);
            jButton17.setEnabled(true);

        } else {
            jComboBox2.setSelectedIndex(0);
            jComboBox2.setEnabled(false);
            jButton17.setEnabled(false);
            jTable12.setEnabled(false);
        }

    }//GEN-LAST:event_jTable12MouseClicked

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        resetAllAttendancePage();
        DefaultTableModel model2 = (DefaultTableModel) jTable13.getModel();
        model2.setRowCount(0);
        DefaultTableModel model1 = (DefaultTableModel) jTable12.getModel();
        model1.setRowCount(0);
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        dateChooser2.showPopup();
    }//GEN-LAST:event_jButton28ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AddPaymentPanel;
    private javax.swing.JPanel AddPaymentPanel1;
    private javax.swing.JPanel AddStudentPanel;
    private javax.swing.JPanel AddSubjectPanel;
    private javax.swing.JPanel AddTeacherPanel;
    private javax.swing.JPanel AttendancePanel;
    private javax.swing.JPanel HomePanel;
    private javax.swing.JPanel MenuPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.raven.datechooser.DateChooser dateChooser1;
    private com.raven.datechooser.DateChooser dateChooser2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable10;
    private javax.swing.JTable jTable11;
    private javax.swing.JTable jTable12;
    private javax.swing.JTable jTable13;
    private javax.swing.JTable jTable14;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    private javax.swing.JTable jTable9;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField35;
    private javax.swing.JTextField jTextField36;
    private javax.swing.JTextField jTextField37;
    private javax.swing.JTextField jTextField38;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField40;
    private javax.swing.JTextField jTextField41;
    private javax.swing.JTextField jTextField42;
    private javax.swing.JTextField jTextField45;
    private javax.swing.JTextField jTextField46;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField50;
    private javax.swing.JTextField jTextField51;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JComboBox<String> tcjComboBox2;
    private com.raven.swing.TimePicker timePicker1;
    private com.raven.swing.TimePicker timePicker2;
    // End of variables declaration//GEN-END:variables
}
