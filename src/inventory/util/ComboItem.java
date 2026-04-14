package inventory.util;

/**
 * Wrapper used in JComboBox to store an ID alongside a display label.
 */
public class ComboItem {

    private final int id;
    private final String label;

    public ComboItem(int id, String label) {
        this.id    = id;
        this.label = label;
    }

    public int getId()      { return id; }
    public String getLabel() { return label; }

    @Override
    public String toString() {
        return label;          // displayed in the combo box
    }
}
