package mnita.externalfilter.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

/* TODO: improve executor
 * - expand variables in command line (paths, home folder, environment (see Eclipse, VS itself)
 * - use the script field, maybe even recognize #!
 * - specify code page for input/output files
 * - new lines
 * - error reporting (colored output, message box instead of console)
 */
public class Executor {
	private final static String CHARSET = "utf-8"; 

    public static void execute(final ExternalCommand cmd) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

            final CommandLine commandline = CommandLine.parse(cmd.getCommandLine());
            final DefaultExecutor exec = new DefaultExecutor();
            
            PumpStreamHandler streamHandler; 
            final String stdIn = cmd.getStdIn();
            if (stdIn == null) {
                streamHandler = new PumpStreamHandler(outputStream, errorStream);
            } else {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(stdIn.getBytes(CHARSET));
                streamHandler = new PumpStreamHandler(outputStream, errorStream, inputStream);
            }
            exec.setStreamHandler(streamHandler);

            final Map<String, String> environment = System.getenv();
            cmd.exitCode = exec.execute(commandline, environment);
            cmd.stdOut = outputStream.toString(CHARSET);
            cmd.stdErr = errorStream.toString(CHARSET);
        } catch (final ExecuteException e) {
            cmd.exceptions.add(e.getMessage());
        } catch (final IOException e) {
            cmd.exceptions.add(e.getMessage());
        }
    }
}
