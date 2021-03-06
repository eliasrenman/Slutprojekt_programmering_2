package com.sqlcom.MainGui;

import com.sqlcom.MainGui.listeners.*;
import com.sqlcom.databases.Database;
import com.sqlcom.databases.DatabaseMYSQL;
import com.sqlcom.utilities.FileUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * This is the main controller for handling the GUI.
 */
public class Controller {
    /**
     * The GUI.
     */
    private Gui gui;

    /**
     * The array list of all the database connections.
     */
    private ArrayList<Database> databaseList;

    /**
     * This is the active database connection.
     */
    private Database activeDBConn;

    /**
     * Constructor for making the controller object.
     *
     * @param gui The gui.
     */
    public Controller(final Gui gui) {
        this.gui = gui;
        createEventListeners();


        try {
            databaseList = FileUtil.loadDatabases();
            activeDBConn = databaseList.get(0);
        } catch (NullPointerException e) {
            databaseList = new ArrayList<>();
        }


        // Sets JTables column names
        DefaultTableModel model = (DefaultTableModel) gui.getDatabaseTable().getModel();
        model.setColumnIdentifiers(new Object[] {"Type", "Host", "Db Name", "Username", "Password", "uniqueid"});
        //Hides the unique id field.
        gui.getDatabaseTable().getColumnModel().getColumn(5).setMinWidth(0);
        gui.getDatabaseTable().getColumnModel().getColumn(5).setMaxWidth(0);
        gui.getDatabaseTable().getColumnModel().getColumn(5).setWidth(0);

        //Adds event listener that triggers on edits.
        model.addTableModelListener(new TableModelEventListener(this));

        //creates a dropdown menu in the database type field.
        JComboBox comboBox = new JComboBox<>(new String[] {formatDbClassName(DatabaseMYSQL.class)});
        gui.getDatabaseTable().getColumn("Type").setCellEditor(new DefaultCellEditor(comboBox));

        for (Database db : databaseList) {
            // Adds dbs to comboBox.
            gui.getDbComboBox().addItem(db);
            // Adds dbs to JTable.
            model.addRow(db.toRow());
        }
    }

    /**
     * Formats the class name of a database to give the actual name
     * @param database
     * @return String name
     */
    public static String formatDbClassName(Class database) {
        String dbType = database.getSimpleName().replace("Database", "");
        return dbType.substring(0, 1) + dbType.substring(1).toLowerCase();
    }

    /**
     * This creates all the Event listeners for the different components of the gui.
     */
    private void createEventListeners() {

        /* IO Tab */
        gui.getRun().addActionListener(new RunEventListener(this));
        gui.getClearOutputButton().addActionListener(new ClearOutputEventListener(this));

        /* Database Tab */
        gui.getSyncRadioButton().addActionListener(new SyncEventListener(this));
        gui.getStopRadioButton().addActionListener(new StopEventListener(this));

        gui.getNewDb().addActionListener(new NewDBEventListener(this));

        gui.getDbComboBox().addActionListener(new DbComboBoxEventListener(this));
        //TODO create a event listener for the table that updates the Database list if a change is detected on a table row.
    }

    /**
     * @return returns the database arrayList.
     */
    public ArrayList<Database> getDatabaseList() {
        return databaseList;
    }

    /**
     * @return The gui.
     */
    public Gui getGui() {
        return gui;
    }

    /**
     * @return the active database connection.
     */
    public Database getActiveDBConn() {
        return activeDBConn;
    }

    /**
     * @param activeDBConn the new active database connection.
     */
    public void setActiveDBConn(final Database activeDBConn) {
        this.activeDBConn = activeDBConn;
    }

    /**
     * This appends html content to the output pane.
     *
     * @param text the text to append.
     */
    public void appendOutputTextPane(final String text) {
        JTextPane jTextPane = getGui().getOutputTextPane();
        jTextPane.setContentType("text/html");

        String content = jTextPane.getText();
        content = content.substring(content.indexOf("<body>") + 6, content.indexOf("</body>"));
        jTextPane.setText("<html><head></head><body>" + content + text + "</body></html>");
    }
}
