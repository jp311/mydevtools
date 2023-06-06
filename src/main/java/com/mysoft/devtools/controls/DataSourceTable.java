package com.mysoft.devtools.controls;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.table.JBTable;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.DbLinkDTO;
import com.mysoft.devtools.utils.JdbcUtil;
import com.mysoft.devtools.utils.psi.IdeaNotifyUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author hezd   2023/6/1
 */
public class DataSourceTable extends JBTable {
    private final DataSourceTableModel model;
    private final static int ENVIRONMENT_TYPE_COLUMN_INDEX = 0;
    private final static int ALIAS_COLUMN_INDEX = 1;
    private final static int PROVIDER_COLUMN_INDEX = 2;
    private final static int IP_COLUMNS_INDEX = 3;
    private final static int PORT_COLUMN_INDEX = 4;
    private final static int USERNAME_COLUMNS_INDEX = 5;
    private final static int PASSWORD_COLUMN_INDEX = 6;
    private final static int DBNAME_COLUMN_INDEX = 7;
    private final static int OPERATION_COLUMN_INDEX = 8;
    private final static int TOTAL_COLUMN = 8;
    private final static int ROW_HEIGHT = 30;


    public DataSourceTable() {
        model = new DataSourceTableModel();
        String[] columns = LocalBundle.message("devtools.settings.datasource.column.names").split(",");
        for (String column : columns) {
            model.addColumn(column);
        }
        this.setModel(model);

        TableColumnModel columnModel = this.getColumnModel();
        ComboBox<String> envTypeComboBox = new ComboBox<>(LocalBundle.message("devtools.settings.datasource.environment.types").split(","));
        DefaultCellEditor envTypeEditor = new DefaultCellEditor(envTypeComboBox);
        columnModel.getColumn(ENVIRONMENT_TYPE_COLUMN_INDEX).setCellEditor(envTypeEditor);

        ComboBox<String> dbTypeComboBox = new ComboBox<>(LocalBundle.message("devtools.settings.datasource.database.types").split(","));
        DefaultCellEditor dbTypeEditor = new DefaultCellEditor(dbTypeComboBox);
        columnModel.getColumn(PROVIDER_COLUMN_INDEX).setCellEditor(dbTypeEditor);

        JBPasswordField password = new JBPasswordField();
        DefaultCellEditor passwordEditor = new DefaultCellEditor(password);
        columnModel.getColumn(PASSWORD_COLUMN_INDEX).setCellEditor(passwordEditor);

        LinkCellRenderer actionLinkCellRenderer = new LinkCellRenderer(this::testConnect);
        columnModel.getColumn(OPERATION_COLUMN_INDEX).setCellRenderer(actionLinkCellRenderer);
        addMouseListener(actionLinkCellRenderer);

        this.setColumnSelectionAllowed(false);
        this.setRowSelectionAllowed(false);
        this.setFillsViewportHeight(true);

        setCellAlignment(SwingConstants.CENTER);
        this.setRowHeight(ROW_HEIGHT);
    }

    private void testConnect(Vector<String> row) {
        String dbType = row.get(PROVIDER_COLUMN_INDEX);
        String ip = row.get(IP_COLUMNS_INDEX);
        String port = row.get(PORT_COLUMN_INDEX);
        String userName = row.get(USERNAME_COLUMNS_INDEX);
        String password = row.get(PASSWORD_COLUMN_INDEX);
        String dbName = row.get(DBNAME_COLUMN_INDEX);

        boolean isOk = JdbcUtil.test(dbType, ip, port, dbName, userName, password);
        if (isOk) {
            IdeaNotifyUtil.dialogInfo(LocalBundle.message("devtools.settings.datasource.test.success"));
        } else {
            IdeaNotifyUtil.dialogError(LocalBundle.message("devtools.settings.datasource.test.fail"));
        }
    }

    public void setCellAlignment(int alignment) {
        MyDefaultTableCellRenderer centerRenderer = new MyDefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(alignment);
        centerRenderer.setVerticalAlignment(alignment);

        JTableHeader tableHeader = getTableHeader();
        tableHeader.setDefaultRenderer(centerRenderer);
        tableHeader.setPreferredSize(new Dimension(50, ROW_HEIGHT));
        TableColumnModel columnModel = this.getColumnModel();
        for (int index = 0; index < TOTAL_COLUMN; index++) {
            if (index == PASSWORD_COLUMN_INDEX) {
                columnModel.getColumn(index).setCellRenderer(new PasswordRenderer());
            } else {
                columnModel.getColumn(index).setCellRenderer(centerRenderer);
            }
        }
    }


    public void newRow() {
        Vector<String> row = new Vector<>();
        for (int index = 0; index < TOTAL_COLUMN; index++) {
            row.add("");
        }
        row.add(LocalBundle.message("devtools.settings.datasource.column.operation.name"));
        model.addRow(row);
    }

    public void newRow(DbLinkDTO item) {
        Vector<String> row = new Vector<>();
        row.add(item.getEvnType());
        row.add(item.getAlias());
        row.add(item.getProvider());
        row.add(item.getServerIp());
        row.add(item.getServerPort());
        row.add(item.getUserName());
        row.add(item.getPassword());
        row.add(item.getDbName());
        row.add(LocalBundle.message("devtools.settings.datasource.column.operation.name"));
        model.addRow(row);
    }

    public void removeSelectedRow() {
        stopCellEditing();
        int selectedRow = getSelectedRow();
        if (selectedRow > -1) {
            MyDefaultTableCellRenderer cellRenderer = (MyDefaultTableCellRenderer) this.getCellRenderer(selectedRow, 1);
            cellRenderer.removeErrorRow(selectedRow);
            model.removeRow(selectedRow);
        }
    }

    public boolean validateData() {
        stopCellEditing();
        boolean hasError = false;
        int rowCount = getRowCount();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            for (int colIndex = 0; colIndex < TOTAL_COLUMN; colIndex++) {
                MyDefaultTableCellRenderer cellRenderer = (MyDefaultTableCellRenderer) getCellRenderer(rowIndex, colIndex);
                cellRenderer.removeErrorCell(rowIndex, colIndex);
                String value = String.valueOf(getValueAt(rowIndex, colIndex));
                if (value.isBlank()) {
                    cellRenderer.addErrorCell(rowIndex, colIndex);
                    repaint();
                    hasError = true;
                }
            }
        }
        return !hasError;
    }

    public List<DbLinkDTO> getData() {
        List<DbLinkDTO> datas = new ArrayList<>();
        int rowCount = getRowCount();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            DbLinkDTO item = new DbLinkDTO();
            item.setAlias(String.valueOf(getValueAt(rowIndex, ALIAS_COLUMN_INDEX)));
            item.setProvider(String.valueOf(getValueAt(rowIndex, PROVIDER_COLUMN_INDEX)));
            item.setPassword(String.valueOf(getValueAt(rowIndex, PASSWORD_COLUMN_INDEX)));
            item.setEvnType(String.valueOf(getValueAt(rowIndex, ENVIRONMENT_TYPE_COLUMN_INDEX)));
            item.setDbName(String.valueOf(getValueAt(rowIndex, DBNAME_COLUMN_INDEX)));
            item.setServerIp(String.valueOf(getValueAt(rowIndex, IP_COLUMNS_INDEX)));
            item.setServerPort(String.valueOf(getValueAt(rowIndex, PORT_COLUMN_INDEX)));
            item.setUserName(String.valueOf(getValueAt(rowIndex, USERNAME_COLUMNS_INDEX)));
            datas.add(item);
        }

        return datas;
    }

    public void setData(List<DbLinkDTO> data) {
        model.setRowCount(0);
        if (data != null) {
            data.forEach(this::newRow);
        }
    }

    public void stopCellEditing() {
        if (getCellEditor() == null) {
            return;
        }
        getCellEditor().stopCellEditing();
    }

    private static class DataSourceTableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != OPERATION_COLUMN_INDEX;
        }
    }
}
