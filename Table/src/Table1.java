import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Table1 extends JFrame {

  public Table1() {
    super("Click on MongoUID to load Map");
    LoadDB db = new LoadDB();

    int i = 1;

    Object[][] tempStore = db.GetVector();

    DefaultTableModel dm = new DefaultTableModel();

    dm.setDataVector(tempStore, new Object[]{"Mongo UID", "Date"});


    JTable table = new JTable(dm);
    table.getColumn("Mongo UID").setCellRenderer(new ButtonRenderer());
    table.getColumn("Mongo UID").setCellEditor(
            new ButtonEditor(new JCheckBox()));
    JScrollPane scroll = new JScrollPane(table);
    getContentPane().add(scroll);
    setSize(600, 500);
    setVisible(true);
  }

  class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
      setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
      if (isSelected) {
        setForeground(table.getSelectionForeground());
        setBackground(table.getSelectionBackground());
      } else {
        setForeground(table.getForeground());
        setBackground(UIManager.getColor("Button.background"));
      }
      setText((value == null) ? "see" : value.toString());
      return this;
    }
  }


  class ButtonEditor extends DefaultCellEditor {
    protected JButton button;

    private String label;

    private boolean isPushed;

    public ButtonEditor(JCheckBox checkBox) {
      super(checkBox);
      button = new JButton();
      button.setOpaque(true);
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fireEditingStopped();
        }
      });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
      if (isSelected) {
        button.setForeground(table.getSelectionForeground());
        button.setBackground(table.getSelectionBackground());
      } else {
        button.setForeground(table.getForeground());
        button.setBackground(table.getBackground());
      }
      label = (value == null) ? "" : value.toString();
      button.setText(label);
      isPushed = true;
      return button;
    }

    public Object getCellEditorValue() {
      LoadDB db = new LoadDB();

      if (isPushed) {
        JOptionPane.showMessageDialog(button, label + " Loaded!");
      }

      isPushed = false;
      db.setMap((String) label);

      return new String(label);
    }

    public boolean stopCellEditing() {
      isPushed = false;
      return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
      super.fireEditingStopped();
    }
  }
}