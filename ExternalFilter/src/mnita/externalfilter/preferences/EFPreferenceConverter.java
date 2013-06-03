package mnita.externalfilter.preferences;

import java.util.ArrayList;
import java.util.List;

import mnita.externalfilter.utils.TextSource;
import mnita.externalfilter.utils.TextTarget;

import org.eclipse.jface.preference.IPreferenceStore;

public class EFPreferenceConverter {
    static final String ARRAY_SEPARATOR = "\u0001";
    static final String FIELD_SEPARATOR = "\u0002";
    static final String VALUE_SEPARATOR = "\u0003";

    /** The default-default value for all the ToolDescription preferences. */
    static final List<EFToolDescription> FONTDATA_ARRAY_DEFAULT_DEFAULT = new ArrayList<EFToolDescription>();

    /** Helper method to construct a ToolDescription from the given string. */
    static EFToolDescription readToolDescription(String value) {
        EFToolDescription result = new EFToolDescription(null);

        for (String keyValue : value.split(FIELD_SEPARATOR)) {
            String [] kv = keyValue.split(VALUE_SEPARATOR);
            if (kv.length == 2) {
                if ("name".equals(kv[0]))
                    result.name = kv[1];
                else if ("command".equals(kv[0]))
                    result.command = kv[1];
//script                else if ("script".equals(kv[0]))
//script                    result.script = kv[1];
                else if ("inputMode".equals(kv[0]))
                    result.inputMode = TextSource.valueOf(kv[1]);
                else if ("outputMode".equals(kv[0]))
                    result.outputMode = TextTarget.valueOf(kv[1]);
            }
        }

        return result.looksValid() ? result : null;
    }

    /** Returns the stored representation of the given ToolDescription object. */
    static String getToolRepresentation(EFToolDescription tool) {
        StringBuffer sb = new StringBuffer();
        sb.append("name").append(VALUE_SEPARATOR).append(tool.name);
        sb.append(FIELD_SEPARATOR);
        sb.append("command").append(VALUE_SEPARATOR).append(tool.command);
        sb.append(FIELD_SEPARATOR);
//script        sb.append("script").append(VALUE_SEPARATOR).append(tool.script);
//script        sb.append(FIELD_SEPARATOR);
        sb.append("inputMode").append(VALUE_SEPARATOR).append(tool.inputMode.toString());
        sb.append(FIELD_SEPARATOR);
        sb.append("outputMode").append(VALUE_SEPARATOR).append(tool.outputMode.toString());
        return sb.toString();
    }

    /** Reads the supplied string and returns its corresponding ToolDescription array. */
    static List<EFToolDescription> readAllToolsDescription(String toolsDataValue) {
        List<EFToolDescription> result = new ArrayList<EFToolDescription>();
        for (String toolDesc : toolsDataValue.split(ARRAY_SEPARATOR)) {
            EFToolDescription tool = readToolDescription(toolDesc);
            if (null != tool)
                result.add(tool);
        }

        return result;
    }

    /* Returns the stored representation of the given list of ToolDescription objects. */
    public static String getAllToolsRepresentation(List<EFToolDescription> value) {
        StringBuffer result = new StringBuffer();
        String separator = "";

        for (EFToolDescription toolDesc : value) {
            String temp = getToolRepresentation(toolDesc);
            result.append(separator).append(temp);
            separator = ARRAY_SEPARATOR;
        }

        return result.toString();
    }

    /* Returns the default value array for the ToolDescription preference with the given name in the given preference store. */
    public static List<EFToolDescription> getDefaultToolDescriptionArray(IPreferenceStore store, String name) {
        String value = store.getString(name);
        return readAllToolsDescription(value);
    }

    /* Returns the current value of the ToolDescription preference with the given name in the given preference store. */
    public static List<EFToolDescription> getToolDescriptionArray(IPreferenceStore store, String name) {
        String value = store.getString(name);
        return readAllToolsDescription(value);
    }

    /* Sets the current value of the preference with the given name in the given preference store. */
    public static void putValue(IPreferenceStore store, String name, List<EFToolDescription> value) {
        String temp = getAllToolsRepresentation(value);
        store.putValue(name, temp);
    }

    /* Sets the default value of the preference with the given name in the given preference store. */
    public static void setDefault(IPreferenceStore store, String name, List<EFToolDescription> value) {
        String temp = getAllToolsRepresentation(value);
        store.setDefault(name, temp);
    }

    /* Sets the current value of the preference with the given name in the given preference store. */
    public static void setValue(IPreferenceStore store, String name, List<EFToolDescription> value) {
        String temp = getAllToolsRepresentation(value);
        store.setValue(name, temp);
    }
}
