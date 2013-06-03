package mnita.externalfilter;

import java.util.List;

import mnita.externalfilter.preferences.EFPreferenceConstants;
import mnita.externalfilter.preferences.EFPreferenceConverter;
import mnita.externalfilter.preferences.EFToolDescription;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class EFActivator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "ExternalFilter"; //$NON-NLS-1$

    // The shared instance
    private static EFActivator plugin;

    public EFActivator() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static EFActivator getDefault() {
        return plugin;
    }

    static List<EFToolDescription> allTools = null;
    public static void toolsUpdated() {
        allTools = null;
    }
    public static List<EFToolDescription> getLatestTools() {
        if (null == allTools) {
            IPreferenceStore store = EFActivator.getDefault().getPreferenceStore();
            allTools = EFPreferenceConverter.getToolDescriptionArray(store, EFPreferenceConstants.PREF_ALL_TOOLS);
        }
        return allTools;
    }

    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
