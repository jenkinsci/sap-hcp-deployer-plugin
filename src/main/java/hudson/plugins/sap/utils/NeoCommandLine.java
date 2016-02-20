package hudson.plugins.sap.utils;

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.WriterStreamConsumer;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class NeoCommandLine {
    private String host;

    private String account;

    private String user;

    private String password;

    private String appName;

    private String sourceLocation;

    private String neosdk;

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setNeosdk(String neosdk) {
        this.neosdk = neosdk;
    }

    public String getNeosdk() {
        return neosdk;
    }

    public void deployApplication(PrintStream logger) {
        Commandline commandline = new Commandline();

        if (System.getProperty("os.name").startsWith("Windows"))
            commandline.setExecutable(neosdk + File.separator + "tools" + File.separator + "neo.bat");
        else
            commandline.setExecutable(neosdk + File.separator + "tools" + File.separator + "neo.sh");

        commandline.setWorkingDirectory(neosdk + File.separator + "tools");
        commandline.addArguments(new String[]{"deploy"});
        commandline.addArguments(new String[]{"--host", host});
        commandline.addArguments(new String[]{"--account", account});
        commandline.addArguments(new String[]{"--user", user});
        commandline.addArguments(new String[]{"--password", password});
        commandline.addArguments(new String[]{"--application", appName});
        commandline.addArguments(new String[]{"--source", sourceLocation});

        logger.println("Executing command : ");

        StringBuffer commandForm = new StringBuffer();

        boolean passwordFlag = false;
        for (String command: commandline.getCommandline()) {
            if (passwordFlag) {
                commandForm = commandForm.append("****").append(" ");
                passwordFlag = false;
            }
            else
                commandForm = commandForm.append(command).append(" ");

            if (command.startsWith("--password"))
                passwordFlag = true;
        }
        logger.println(commandForm.toString());

        try {
            WriterStreamConsumer systemOut = new WriterStreamConsumer(new OutputStreamWriter(System.out, "UTF-8"));
            CommandLineUtils.executeCommandLine(commandline, systemOut, systemOut);
            logger.println("command successfully executed");
        } catch (UnsupportedEncodingException e) {
            logger.println(e.getMessage());
        }
          catch (CommandLineException e) {
            logger.println(e.getMessage());
        }
    }
}