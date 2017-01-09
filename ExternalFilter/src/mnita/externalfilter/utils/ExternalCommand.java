package mnita.externalfilter.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import mnita.externalfilter.preferences.EFToolDescription;

public class ExternalCommand {
    private final EFToolDescription tool;
    private final String workDir;
    private final String stdIn;
    private final String inputCharset = StandardCharsets.UTF_8.name();
    private final String outputCharset = StandardCharsets.UTF_8.name();
    String stdOut = "";
    String stdErr = "";
    List<String> exceptions = new ArrayList<String>();
    int exitCode = -1;

    public ExternalCommand(EFToolDescription tool, String stdIn) {
        this.tool = tool;
        this.stdIn = stdIn;
        this.workDir = null;
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
        return inputCharset;
    }

    public String getOutputCharset() {
        return outputCharset;
    }

    public String getWorkdir() {
        return workDir;
    }

    public int getExitCode() {
        return exitCode;
    }
}
