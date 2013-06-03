package mnita.externalfilter.contributions;

import java.util.List;
import java.util.Map.Entry;

import mnita.externalfilter.EFActivator;
import mnita.externalfilter.preferences.EFPreferencePage;
import mnita.externalfilter.preferences.EFToolDescription;
import mnita.externalfilter.utils.EclipseEditorGetSet;
import mnita.externalfilter.utils.Executor;
import mnita.externalfilter.utils.ExternalCommand;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;

public class EFToolsDynamicMenu extends ContributionItem {

    @Override
    public boolean isDynamic() {
        return true;
    }

/* TODO: find a way to create the menu with commands and all the goodies
 * Solution from http://www.sigasi.com/content/dynamic-menu-items-eclipse
 * Although it works, it does not follow the standard way of doing things
 * (with commands that can be bound to tool bar and hot keys, etc.)
 */
    @Override
    public void fill(Menu menu, int index) {
        List<EFToolDescription> allTools = EFActivator.getLatestTools();

        int toolIndex = 0;
        for (EFToolDescription tool : allTools)
            addMenuItem(menu, toolIndex++, tool.name, true);

        MenuItem mItem = new MenuItem(menu, SWT.SEPARATOR);
        mItem = addMenuItem(menu, -1, "Configure external tools...", false);
        mItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showConfigDialog();
            }
        });
/*
        m = addMenuItem(menu, -1, "Spy info", false);
        m.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                spyAndDebugInfo();
            }
        });
*/
    }

    static MenuItem addMenuItem(Menu menu, int id, String text, boolean setToolListener) {
        MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
        menuItem.setText(text);

        if (setToolListener) {
            menuItem.setID(id);
            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (e.widget instanceof MenuItem)
                        toolMenuSelected(((MenuItem) e.widget).getID());
                }
            });
        }

        return menuItem;
    }

    static String processText(IWorkbenchWindow window, ITextSelection selection, EFToolDescription tool) {
        ExternalCommand cmd = new ExternalCommand(tool, null == selection ? null : selection.getText());
        Executor.execute(cmd);

        if (0 != cmd.getExitCode())
            EclipseEditorGetSet.outputToConsole(window, "=== EXIT CODE ===\n" + Integer.toString(cmd.getExitCode()));
        if (!cmd.getStdErr().isEmpty())
            EclipseEditorGetSet.outputToConsole(window, "=== STDERR ===\n" + cmd.getStdErr());
        if (!cmd.getException().isEmpty()) {
            EclipseEditorGetSet.outputToConsole(window, "=== EXCEPTIONS ===\n");
            for (String exception : cmd.getException())
                EclipseEditorGetSet.outputToConsole(window, exception + "\n");
        }

        return cmd.getStdOut();
    }

    static void toolMenuSelected(int toolNumber) {
        IWorkbenchWindow window = EFActivator.getDefault().getWorkbench().getActiveWorkbenchWindow();

        List<EFToolDescription> allTools = EFActivator.getLatestTools();
        if (allTools.isEmpty())
            return;

        if (toolNumber < 0 && toolNumber >= allTools.size())
            return;

        EFToolDescription toolToRun = allTools.get(toolNumber);

        ITextSelection selection = EclipseEditorGetSet.getText(window, toolToRun.inputMode);
        String result = processText(window, selection, toolToRun);
        if (null != result)
            EclipseEditorGetSet.setText(window, result, selection, toolToRun.outputMode);
    }

    static void showConfigDialog() {
        IWorkbenchWindow window = EFActivator.getDefault().getWorkbench().getActiveWorkbenchWindow();
        IPreferencePage page = new EFPreferencePage();
        PreferenceManager mgr = window.getWorkbench().getPreferenceManager();

        Shell shell = EFActivator.getDefault().getWorkbench().getDisplay().getActiveShell();
        PreferenceDialog dialog = new PreferenceDialog(shell, mgr);
        dialog.setSelectedNode("Externalfilter.preferences.ExternalFilterPreferencePage");
        dialog.create();
        dialog.setMessage(page.getTitle());
        dialog.open();
    }

    static String getVarX(IStringVariableManager x, String varName) {
        try {
            return x.performStringSubstitution("${" + varName + "}", false);
        } catch (CoreException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    static void spyAndDebugInfo() {
        IStringVariableManager x = org.eclipse.core.variables.VariablesPlugin.getDefault().getStringVariableManager();
        System.out.println("========== Variables ==========");
        for (IStringVariable var: x.getVariables()) {
            System.out.println(var.getName());
            System.out.println("    descript : " + var.getDescription());
            System.out.println("    subst    : " + getVarX(x, var.getName()));
        }

        System.out.println("========== ValueVariables ==========");
        for (IValueVariable varVar: x.getValueVariables()) {
            System.out.println(varVar.getName());
            System.out.println("    value    : " + varVar.getValue());
            System.out.println("    contrib  : " + varVar.isContributed());
            System.out.println("    read-only: " + varVar.isReadOnly());
            System.out.println("    descript : " + varVar.getDescription());
        }

        System.out.println("========== DynamicVariables ==========");
        for (IDynamicVariable dynVar: x.getDynamicVariables()) {
            System.out.println(dynVar.getName());
            System.out.println("    with arg : " + dynVar.supportsArgument());
            System.out.println("    descript : " + dynVar.getDescription());
        }

        System.out.println("========== Environment ==========");
        for (Entry<String, String> entry : System.getenv().entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        System.out.println("========== Java properties ==========");
        for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}
