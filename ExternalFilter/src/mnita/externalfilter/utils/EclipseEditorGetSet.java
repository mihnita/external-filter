package mnita.externalfilter.utils;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BlockTextSelection;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class EclipseEditorGetSet {

    public static ITextSelection getText(IWorkbenchWindow window, TextSource from) {
        if (from == TextSource.fromNothing)
            return null;

        if (from == TextSource.fromClipboard) {
            ClipboardSelection cselection = new ClipboardSelection();
            return cselection;
        }

        if (from == TextSource.fromCrtDoc)
            return getFullDocumentText(window);
        
        ISelection selection = window.getSelectionService().getSelection();
        if (null == selection || selection.isEmpty()) {
            // nothing selected, default to full document, if that was the option
            if (from == TextSource.fromCrtSelDoc)
                return getFullDocumentText(window);
            else
                return null;
        }

        if (!(selection instanceof TextSelection))
            return null;

        return (TextSelection) selection;
    }
    
    static TextSelection getFullDocumentText(IWorkbenchWindow window) {
        IEditorPart page = window.getActivePage().getActiveEditor();
        if (null == page)
            return null;
        if (!(page instanceof AbstractDecoratedTextEditor))
            return null;

        IDocumentProvider docProvider = ((AbstractDecoratedTextEditor) page).getDocumentProvider();
        IDocument document = docProvider.getDocument(page.getEditorInput());
        TextSelection selection = new TextSelection(document, 0, document.getLength());
        return selection;
    }

    private static void replaceCurrentSelection(Document document, ITextSelection originalSelection, String newText) {
        try {
            if (originalSelection instanceof BlockTextSelection) {
                //ToDo ((BlockTextSelection)originalSelection).getRegions()[0];
            } else
                document.replace(originalSelection.getOffset(), originalSelection.getLength(), newText);
                // document.replace(originalSelection.getOffset(), newText.length(), newText);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private static void insertInDocumentAtPosition(Document document, int position, ITextSelection originalSelection, String newText) {
        int insertionPoint = document.getLength() - 1;
        if (position < insertionPoint)
            insertionPoint = position;
        try {
            if (originalSelection instanceof BlockTextSelection) {
                // ((BlockTextSelection)originalSelection).getRegions()[0];
            } else
                document.replace(insertionPoint, 0, newText);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private static void appendToCurrentDocument(Document document, ITextSelection originalSelection, String newText) {
        insertInDocumentAtPosition(document, document.getLength(), originalSelection, newText);
    }

    public static void setText(IWorkbenchWindow window, String newText, ITextSelection originalSelection, TextTarget to) {
        if (to == TextTarget.toNothing)
            return;

        if (to == TextTarget.toConsole) {
            outputToConsole(window, newText);
            return;
        }

        if (to == TextTarget.toMessageBox) {
            MessageDialog.openInformation(window.getShell(), "External Filter", newText);
            return;
        }
        
        if (to == TextTarget.toClipboard) {
            ClipboardSelection.toClipboard(newText);
            return;
        }

        IEditorPart page = window.getActivePage().getActiveEditor();
        if (null == page)
            return;
        if (!(page instanceof AbstractDecoratedTextEditor))
            return;

        IDocumentProvider docProvider = ((AbstractDecoratedTextEditor) page).getDocumentProvider();
//        System.out.println(docProvider); // org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitDocumentProvider

        IDocument document = docProvider.getDocument(page.getEditorInput()); // org.eclipse.core.internal.filebuffers.SynchronizableDocument
        if (document instanceof Document) {
            if (to == TextTarget.replaceCrtDoc)
                ((Document) document).set(newText);
            else if (to == TextTarget.replaceCrtSel)
                replaceCurrentSelection((Document) document, originalSelection, newText);
            else if (to == TextTarget.appendToCrtDoc)
                appendToCurrentDocument((Document) document, originalSelection, newText);
//            else if (to == TextTarget.insertAtCursorPos) {
//                currentPos = ((AbstractTextEditor)page).getEditorInput().get
//              // This is really the end of selection, not current cursor position
//              // And what happens if the original selection was clipboard?
//                insertInDocumentAtPosition((Document) document,
//                        originalSelection.getOffset() + originalSelection.getLength(),
//                        originalSelection, newText);
//            }
        }
    }

    private static MessageConsole findConsole(String name) {
        IConsoleManager conMan = ConsolePlugin.getDefault().getConsoleManager();
        IConsole[] existing = conMan.getConsoles();
        for (int i = 0; i < existing.length; i++)
            if (name.equals(existing[i].getName()))
                return (MessageConsole) existing[i];

        // no console found, so create a new one
        MessageConsole myConsole = new MessageConsole(name, null);
        conMan.addConsoles(new IConsole[] { myConsole });
        return myConsole;
    }

    public static void outputToConsole(IWorkbenchWindow window, String text) {
        if (null == window || null == text)
            return;

        final String CONSOLE_NAME = "External Filter";
        MessageConsole efConsole = findConsole(CONSOLE_NAME);
        try {
            MessageConsoleStream outConsole = efConsole.newMessageStream();
            outConsole.println(text);
            outConsole.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            IConsoleView view = (IConsoleView) window.getActivePage().showView(IConsoleConstants.ID_CONSOLE_VIEW);
            view.display(efConsole);
        } catch (PartInitException e) {
            e.printStackTrace();
        }
    }
}
