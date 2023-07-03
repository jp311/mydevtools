package com.mysoft.devtools.views.users;

import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.dtos.GenerateDialogDTO;
import com.mysoft.devtools.dtos.MyVector;
import com.mysoft.devtools.utils.psi.IdeaNotifyUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a href="https://plugins.jetbrains.com/docs/intellij/popups.html">Dialog</a>
 *
 * @author hezd 2023/4/27
 */
public class GenerateCodeDialog extends BaseDialogComponent {
    private JPanel contentPanel;
    private JBTable table;
    private JBTextField txtFilter;
    GenerateDialogDTO generateDialogDTO;

    public GenerateCodeDialog(GenerateDialogDTO generateDialogDTO) {
        this.generateDialogDTO = generateDialogDTO;
        setTitle(generateDialogDTO.getDialogTitle());
        init();

        contentPanel.setPreferredSize(new Dimension(960, 600));
    }


    public MyVector<MyVector<Object>> getSelected() {
        MyVector<MyVector<Object>> myVector = new MyVector<>();
        CheckTableModel model = (CheckTableModel) table.getModel();
        model.getDataVector().forEach(x -> {
            if (x.get(0).equals(true)) {
                myVector.add((MyVector<Object>) x);
            }
        });
        return myVector;
    }

    private void createUIComponents() {
        txtFilter = new JBTextField();

        table = new JBTable();
        CheckTableModel tableModel = new CheckTableModel(txtFilter, generateDialogDTO.getDataSource(), generateDialogDTO.getHeaders());
        table.setModel(tableModel);

        JTableHeader tableHeader = table.getTableHeader();
        CheckHeaderCellRenderer checkHeaderCellRenderer = new CheckHeaderCellRenderer(table);
        tableHeader.setDefaultRenderer(checkHeaderCellRenderer);
        tableHeader.setPreferredSize(new Dimension(50, 40));

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(80);

        table.setRowHeight(40);

        //排序
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        // 为文本框添加监听器，每当文本框内容发生变化时，过滤行
        txtFilter.setTextToTriggerEmptyTextStatus("输入关键字可对数据过滤");
        txtFilter.getDocument().addDocumentListener(new DocumentListener() {
            private final Timer timer = new Timer(500, evt -> newFilter());

            @Override
            public void changedUpdate(DocumentEvent e) {
                restartTimer();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                restartTimer();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                restartTimer();
            }

            private void restartTimer() {
                if (timer.isRunning()) {
                    timer.stop();
                }
                timer.start();

                if (table.getSelectedRows().length != table.getRowCount()) {
                    checkHeaderCellRenderer.selectBox.setSelected(false);
                }
            }

            private void newFilter() {
                RowFilter<TableModel, Object> rowFilter = null;
                try {
                    String text = txtFilter.getText();
                    rowFilter = RowFilter.regexFilter("(?i)" + text);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                sorter.setRowFilter(rowFilter);
            }
        });

    }

    @Override
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

    @Override
    protected void doOKAction() {
        Function<MyVector<MyVector<Object>>, Boolean> doOkAction = generateDialogDTO.getDoOKAction();
        if (doOkAction == null) {
            return;
        }
        MyVector<MyVector<Object>> selected = getSelected();
        if (selected.size() == 0) {
            IdeaNotifyUtil.dialogError(LocalBundle.message("devtools.userview.generate.noselected.message"));
            return;
        }
        doOkAction.apply(selected);
    }

    private static final class CheckHeaderCellRenderer implements TableCellRenderer {
        CheckTableModel tableModel;
        JTableHeader tableHeader;
        final JCheckBox selectBox;

        public CheckHeaderCellRenderer(JBTable table) {
            this.tableModel = (CheckTableModel) table.getModel();
            this.tableHeader = table.getTableHeader();
            selectBox = new JCheckBox(tableModel.getColumnName(0));
            selectBox.setSelected(false);
            tableHeader.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 0) {
                        //获得选中列
                        int selectColumn = tableHeader.columnAtPoint(e.getPoint());
                        if (selectColumn == 0) {
                            boolean value = !selectBox.isSelected();
                            selectBox.setSelected(value);
                            tableModel.selectAllOrNull(value);
                            tableHeader.repaint();
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String valueStr = (String) value;
            JLabel label = new JLabel(valueStr);
            label.setHorizontalAlignment(SwingConstants.CENTER); // 表头标签剧中
            selectBox.setHorizontalAlignment(SwingConstants.CENTER);// 表头标签剧中
            selectBox.setBorderPainted(true);
            JComponent component = (column == 0) ? selectBox : label;
            component.setForeground(tableHeader.getForeground());
            component.setBackground(tableHeader.getBackground());
            component.setFont(tableHeader.getFont());
            component.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

//            // 检查当前列是否为排序列
//            JTableHeader header = table.getTableHeader();
//            if (header != null) {
//                TableColumnModel model = table.getColumnModel();
//                int index = model.getColumnIndexAtX(header.getDraggedDistance());
//                if (index == column && table.getRowSorter() != null) {
//                    // 如果当前列是排序列，则设置相应的排序图标
//                    switch (table.getRowSorter().getSortKeys().get(0).getSortOrder()) {
//                        case ASCENDING:
////                            label.setIcon(new ImageIcon("path/to/asc_icon.png"));
//                            label.setText("升序");
//                            break;
//                        case DESCENDING:
////                            label.setIcon(new ImageIcon("path/to/desc_icon.png"));
//                            label.setText("降序");
//                            break;
//                        default:
//                            label.setIcon(null);
//                    }
//                } else {
//                    // 如果当前列不是排序列，则清除图标
//                    label.setIcon(null);
//                }
//            }
            return component;
        }
    }

    private static final class CheckTableModel extends DefaultTableModel {
        private JBTextField txtFilter;

        public CheckTableModel(JBTextField txtFilter, MyVector data, MyVector columnNames) {
            super(data, columnNames);
            this.txtFilter = txtFilter;
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public void selectAllOrNull(boolean value) {
            String filterText = txtFilter.getText() == null || "".equals(txtFilter.getText()) ? "" : txtFilter.getText();

            for (int i = 0; i < getRowCount(); i++) {
                if (Objects.equals(filterText, "")) {
                    this.setValueAt(value, i, 0);
                } else {
                    String text = String.valueOf(this.getValueAt(i, 1)) + String.valueOf(this.getValueAt(i, 2));
                    Pattern regex = Pattern.compile("(?i)" + filterText);
                    Matcher matcher = regex.matcher(text);
                    if (matcher.find()) {
                        this.setValueAt(value, i, 0);
                    }
                }
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0;
        }
    }
}
