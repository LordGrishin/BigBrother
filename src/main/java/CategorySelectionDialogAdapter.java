import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CategorySelectionDialogAdapter extends WindowAdapter {
    public void windowClosing(WindowEvent e)
    {
        System.out.println("Dialog: closed");
        CategorySelectionDialog.getInstance().setVisible(false);
        //отослать storage на гитхаб
    }
    public void windowOpened(WindowEvent e) {
        System.out.println("Dialog: opened");
    }
    public void windowIconified(WindowEvent e) {
        System.out.println("Dialog: iconified");
    }

    public void windowDeiconified(WindowEvent e) {
        System.out.println("Dialog: de-iconified");
    }
}
