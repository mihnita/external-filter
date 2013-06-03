package mnita.externalfilter.preferences;

import mnita.externalfilter.utils.TextSource;
import mnita.externalfilter.utils.TextTarget;

public class EFToolDescription {
    public String name = "";
    public String command = "";
//script    public String script = "";
    public TextSource inputMode = TextSource.fromNothing;
    public TextTarget outputMode = TextTarget.toNothing;

    public EFToolDescription(String name) {
        this.name = name;
    }

    boolean looksValid() {
        if (null == name)
            return false;
        if (name.isEmpty())
            return false;
//script        if ((null == command) && (null == script))
//script            return false;

        return true;
    }
}
