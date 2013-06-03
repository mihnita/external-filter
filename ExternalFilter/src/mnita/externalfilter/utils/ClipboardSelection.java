package mnita.externalfilter.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.eclipse.jface.text.ITextSelection;

class ClipboardSelection implements ITextSelection, ClipboardOwner {
    String text;

    ClipboardSelection() {
        text = fromClipboard();
    }

    public static String fromClipboard() {
        String result = null;
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            Object data = clipboard.getData(DataFlavor.stringFlavor);
            if (data instanceof String)
                result = (String) data;
        } catch (UnsupportedFlavorException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        }

        return result;
    }

    public static void toClipboard(String str) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(str);
        clipboard.setContents(stringSelection, new ClipboardSelection());
    }

    @Override
    public boolean isEmpty() {
        return text.isEmpty();
    }

    @Override
    public int getEndLine() {
        return 0;
    }

    @Override
    public int getLength() {
        return text.length();
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public int getStartLine() {
        return 0;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}