package mnita.externalfilter.utils;

import java.util.ArrayList;
import java.util.List;

import mnita.externalfilter.preferences.EFToolDescription;

public class ExternalCommand {
    private final EFToolDescription tool;
    private final String stdIn;
    String stdOut = "";
    String stdErr = "";
    List<String> exceptions = new ArrayList<String>();
    int exitCode = -1;

    public ExternalCommand(EFToolDescription tool, String stdIn) {
        this.tool = tool;
        this.stdIn = stdIn;
    }

    public String getCommandLine() {
        return tool.command;
    }

    public String getStdIn() {
        return stdIn;
    }

    public String getStdOut() {
        return stdOut;
    }

    public String getStdErr() {
        return stdErr;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public String getInputCharset() {
        return tool.inputCharset;
    }

    public String getOutputCharset() {
        return tool.outputCharset;
    }

    public String getWorkdir() {
        return tool.workDir;
    }

    public int getExitCode() {
        return exitCode;
    }
}
