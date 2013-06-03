package mnita.externalfilter.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/* TODO: improve executor
 * - expand variables in command line (paths, home folder, environment (see Eclipse, VS itself)
 * - use the script field, maybe even recognize #!
 * - specify code page for input/output files
 * - new lines
 * - erorr reporting (colored output, message box instead of console)
 */
class ProcessResultReader extends Thread {
    final InputStream   is;
    final StringBuilder sb = new StringBuilder();
    String exceptionMsg = null;

    ProcessResultReader(final InputStream is) {
        this.is = is;
    }

    @Override
    public void run() {
        try {
            final InputStreamReader isr = new InputStreamReader(is, "utf-8");
            final BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while (null != line) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
        } catch (final IOException ioe) {
            exceptionMsg = ioe.getMessage();
        }
    }

    public String getOutputString() {
        return sb.toString();
    }
}

class ProcessInputWriter extends Thread {
    final OutputStream os;
    final String[]     stdIn;
    String exceptionMsg = null;

    ProcessInputWriter(final OutputStream is, String inputText) {
        this.os = is;
        // TODO Should fix all this \n stuff in input and output, pass things around untouched
        if (null != inputText)
            stdIn = inputText.split("\n");
        else
            stdIn = new String[]{};
    }

    @Override
    public void run() {
        try {
            final OutputStreamWriter osr = new OutputStreamWriter(os, "utf-8");
            for (String line : stdIn)
                osr.write(line + "\n");
            osr.close();
            os.close();
        } catch (final IOException e) {
            exceptionMsg = e.getMessage();
        }
    }
}

public class Executor {
    public static void execute(ExternalCommand cmd) {
        try {
            final Process p = Runtime.getRuntime().exec(cmd.getCommandLine());
            final ProcessResultReader stderr = new ProcessResultReader(p.getErrorStream());
            final ProcessResultReader stdout = new ProcessResultReader(p.getInputStream());
            final ProcessInputWriter stdin = new ProcessInputWriter(p.getOutputStream(), cmd.stdIn);
            stderr.start();
            stdout.start();
            stdin.start();

            cmd.exitCode = p.waitFor();

            /* Collect the various results. We assume (for now) that stdout is the right thing,
             * and everything in stderr is an error.
             * This is not true for some cases (for instance some applications send the help to stderr
             * The launcher should be able to specify that, but not in version 1.0
            */
            cmd.stdOut = stdout.getOutputString();
            cmd.stdErr = stderr.getOutputString();

            if (null != stdin.exceptionMsg)
                cmd.exceptions.add(stdin.exceptionMsg);
            if (null != stdout.exceptionMsg)
                cmd.exceptions.add(stdout.exceptionMsg);
            if (null != stderr.exceptionMsg)
                cmd.exceptions.add(stderr.exceptionMsg);
        } catch (final IOException e) {
            cmd.exceptions.add(e.getMessage());
        } catch (final InterruptedException e) {
            cmd.exceptions.add(e.getMessage());
        }
    }
}
