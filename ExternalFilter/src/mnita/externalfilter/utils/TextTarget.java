package mnita.externalfilter.utils;

public enum TextTarget {
    toNothing         ("Nothing"), // Done
    toConsole         ("Display in console"), // Done
    toMessageBox      ("Display a message box"), // Done
//    toNewDoc          ("Create new document"), // Don't do it, a bit of a problem with "where"
    toClipboard       ("Put into clipboard"), // Done
    appendToCrtDoc    ("Append to current document"), // Done
    replaceCrtDoc     ("Replace current document"), // Done
    replaceCrtSel     ("Replace current selection"); // Done, need block replacement
//    insertAtCursorPos ("Insert at cursor position (to do)");

    public final String displayName;

    TextTarget(String displayName) {
        this.displayName = displayName;
    }

    public static String[] getDisplayNames() {
        String [] result = new String[TextTarget.values().length];
        for (int i = 0; i < TextTarget.values().length; ++i)
            result[i] = TextTarget.values()[i].displayName;
        return result;
    }
}
