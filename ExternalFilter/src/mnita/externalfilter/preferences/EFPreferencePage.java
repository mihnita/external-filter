package mnita.externalfilter.preferences;

import mnita.externalfilter.EFActivator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class EFPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    EFToolDescriptionFieldEditor toolEditor = null;

    public EFPreferencePage() {
        super(GRID);
        setPreferenceStore(EFActivator.getDefault().getPreferenceStore());
        setDescription("Preferences for External Filter");
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected void createFieldEditors() {
        toolEditor = new EFToolDescriptionFieldEditor(
                EFPreferenceConstants.PREF_ALL_TOOLS,
                "ExternalToolsEditor",
                getFieldEditorParent());
        this.addField(toolEditor);
    }

    @Override
    public void performApply() {
        toolEditor.onToolEdited();
        EFActivator.toolsUpdated();
        super.performApply();
    }

    @Override
    public boolean performOk() {
        toolEditor.onToolEdited();
        EFActivator.toolsUpdated();
        return super.performOk();
    }
}
