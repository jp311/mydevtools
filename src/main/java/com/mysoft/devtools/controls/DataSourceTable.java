package com.mysoft.devtools.controls;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * @author hezd   2023/6/1
 */
public class DataSourceTable extends JBTable {
    private final DataSourceTableModel model;

    public DataSourceTable() {
        model = new DataSourceTableModel();
        model.addColumn("环境类型");
        model.addColumn("别名");
        model.addColumn("厂商");
        model.addColumn("服务器IP");
        model.addColumn("服务器端口");
        model.addColumn("账号");
        model.addColumn("密码");
        model.addColumn("数据库名称");
        model.addColumn("操作");

        this.setModel(model);

        TableColumnModel columnModel = this.getColumnModel();
        ComboBox<String> envTypeComboBox = new ComboBox<>(new String[]{"开发", "测试", "预发", "生产"});
        DefaultCellEditor envTypeEditor = new DefaultCellEditor(envTypeComboBox);
        columnModel.getColumn(0).setCellEditor(envTypeEditor);

        ComboBox<String> dbTypeComboBox = new ComboBox<>(new String[]{"MySql", "达梦"});
        DefaultCellEditor dbTypeEditor = new DefaultCellEditor(dbTypeComboBox);
        columnModel.getColumn(2).setCellEditor(dbTypeEditor);

        JBPasswordField password = new JBPasswordField();
        DefaultCellEditor passwordEditor = new DefaultCellEditor(password);
        columnModel.getColumn(6).setCellEditor(passwordEditor);

        LinkCellRenderer actionLinkCellRenderer = new LinkCellRenderer();
        columnModel.getColumn(8).setCellRenderer(actionLinkCellRenderer);
        addMouseListener(actionLinkCellRenderer);

        this.setColumnSelectionAllowed(false);
        this.setRowSelectionAllowed(false);
        this.setFillsViewportHeight(true);

        setCellAlignment(SwingConstants.CENTER);
        this.setRowHeight(30);


    }

    public void setCellAlignment(int alignment) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(alignment);
        centerRenderer.setVerticalAlignment(alignment);

        JTableHeader tableHeader = getTableHeader();
        tableHeader.setDefaultRenderer(centerRenderer);
        tableHeader.setPreferredSize(new Dimension(50, 30));
        TableColumnModel columnModel = this.getColumnModel();
        for (int index = 0; index < 8; index++) {
            if (index == 6) {
                columnModel.getColumn(index).setCellRenderer(new PasswordRenderer());
            } else {
                columnModel.getColumn(index).setCellRenderer(centerRenderer);
            }

        }
    }


    public void newRow() {
        Vector<String> row = new Vector<>();
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("");
        row.add("测试连接");
        model.addRow(row);
    }

    public void removeSelectedRow() {
        int selectedRow = getSelectedRow();
        if (selectedRow > -1) {
            model.removeRow(selectedRow);
        }
    }

    /**
     * 显示超链接的JTable单元格渲染器
     */
    private static class LinkCellRenderer extends DefaultTableCellRenderer implements MouseInputListener {

        //鼠标事件所在的行
        private int row = -1;
        //鼠标事件所在的列
        private int col = -1;
        //当前监听的Table
        private JTable table = null;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            //恢复默认状态
            this.table = table;
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            this.setText(value.toString());
            this.setHorizontalAlignment(SwingConstants.CENTER);
//            //如果当前需要渲染器的单元格就是鼠标事件所在的单元格
//            if (row == this.row && column == this.col) {
//                //如果是第二列(第二列是显示超链接的列)
//                if (column == 8) {
//                    //改变前景色(文字颜色)
//                    this.setForeground(JBColor.BLUE);
//                    //改变鼠标形状
//                    table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                    //显示超链接样式
//                    this.setText("<html>" + value + "</html>");
//                }
//                setBackground(table.getSelectionBackground());
//            } else if (isSelected) {
//                //如果单元格被选中,则改变前景色和背景色
//                setForeground(table.getSelectionForeground());
//                setBackground(table.getSelectionBackground());
//            } else {
//                //其他情况下恢复默认背景色
//                setBackground(JBColor.WHITE);
//            }

            if (column == 8) {
                //改变前景色(文字颜色)
                this.setForeground(JBColor.BLUE);
                //改变鼠标形状
                table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                //显示超链接样式
                this.setText("<html>" + value + "</html>");
            }
            return this;
        }

        /**
         * 鼠标移出事件
         */
        @Override
        public void mouseExited(MouseEvent e) {
            if (table != null) {
                int oldRow = row;
                int oldCol = col;
                //鼠标移出目标表格后,恢复行列数据到默认值
                row = -1;
                col = -1;
                //当之前的行列数据有效时重画相关区域
                if (oldRow != -1 && oldCol != -1) {
                    Rectangle rect = table.getCellRect(oldRow, oldCol, false);
                    table.repaint(rect);
                }
            }
        }

        /**
         * 鼠标拖动事件
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            mouseMoved(e);
        }

        /**
         * 鼠标移动事件
         */
        @Override
        public void mouseMoved(MouseEvent e) {
            if (table != null) {
                Point p = e.getPoint();
                int oldRow = row;
                int oldCol = col;
                row = table.rowAtPoint(p);
                col = table.columnAtPoint(p);
                //重画原来的区域
                if (oldRow != -1 && oldCol != -1) {
                    Rectangle rect = table.getCellRect(oldRow, oldCol, false);
                    table.repaint(rect);
                }
                //重画新的区域
                if (row != -1 && col != -1) {
                    Rectangle rect = table.getCellRect(row, col, false);
                    table.repaint(rect);
                }
            }
        }

        /**
         * 鼠标单击事件
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            //获取事件所在的行列坐标信息
            Point p = e.getPoint();
            int c = table.columnAtPoint(p);
            if (c != 8) {
                return;
            }
            int r = table.rowAtPoint(p);
            try {
                //取得目标单元格的值,即链接信息
                Object valueAt = table.getValueAt(r, c);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /**
         * 鼠标按下事件
         */
        @Override
        public void mousePressed(MouseEvent e) {
        }

        /**
         * 鼠标释放事件
         */
        @Override
        public void mouseReleased(MouseEvent e) {
        }

        /**
         * 鼠标进入事件
         */
        @Override
        public void mouseEntered(MouseEvent e) {
        }
    }

    private static class PasswordRenderer extends DefaultTableCellRenderer {
        private final JBPasswordField passwordField = new JBPasswordField();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                passwordField.setBackground(table.getSelectionBackground());
                passwordField.setForeground(table.getSelectionForeground());
            } else {
                passwordField.setBackground(table.getBackground());
                passwordField.setForeground(table.getForeground());
            }

            passwordField.setText(String.valueOf(value));
            passwordField.setOpaque(true);
//            passwordField.setBorder(BorderFactory.createEmptyBorder());
//            passwordField.setMargin(JBUI.emptyInsets());
            return passwordField;
        }
    }

    private static class DataSourceTableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 8;
        }
    }
}
