package mnita.externalfilter.utils;

public enum TextSource {
    fromNothing   ("Nothing"), // Done
    fromClipboard ("From clipboard"), // Done
    fromCrtDoc    ("Current document"), // Done
    fromCrtSel    ("Current selection"), // Done
    fromCrtSelDoc ("Current selection (default to document)"); // Done
//    fromCrtLine   ("Current line"), // Don't do it (just select a line if you want)
//    fromCrtWord   ("Current word"); // Don't do it (just select a word if you want)

    public final String displayName;

    TextSource(String displayName) {
        this.displayName = displayName;
    }

    public static String[] getDisplayNames() {
        String [] result = new String[TextSource.values().length];
        for (int i = 0; i < TextSource.values().length; ++i)
            result[i] = TextSource.values()[i].displayName;
        return result;
    }
}
