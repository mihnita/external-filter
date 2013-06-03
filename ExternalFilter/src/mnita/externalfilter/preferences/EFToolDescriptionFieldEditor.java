package mnita.externalfilter.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class EFToolDescriptionFieldEditor extends FieldEditor {
    EFToolDescriptionFieldEditorImpl theControl;

    public EFToolDescriptionFieldEditor(String name, String labelText, Composite parent) {
        super(name, labelText, parent);
    }

    @Override
    protected void adjustForNumColumns(int arg0) {
    }

    @Override
    protected void doFillIntoGrid(Composite arg0, int arg1) {
    }

    @Override
    protected void doLoad() {
        if (theControl == null)
            return;

        theControl.allTools =  EFPreferenceConverter.getToolDescriptionArray(getPreferenceStore(), getPreferenceName());
        theControl.onListDataChanged();
    }

    @Override
    protected void doLoadDefault() {
        if (theControl == null)
            return;

        theControl.allTools =  EFPreferenceConverter.getDefaultToolDescriptionArray(getPreferenceStore(), getPreferenceName());
        theControl.onListDataChanged();
    }

    @Override
    protected void doStore() {
        if (theControl == null)
            return;

        EFPreferenceConverter.setValue(getPreferenceStore(), getPreferenceName(), theControl.allTools);
    }

    @Override
    public int getNumberOfControls() {
        return 1;
    }

    @Override
    protected void createControl(Composite parent) {
        theControl = new EFToolDescriptionFieldEditorImpl(parent, SWT.NONE);
        theControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        theControl.setLayout(new GridLayout(1, false));
    }

    public void onToolEdited() {
        theControl.onToolEdited();
    }
}
