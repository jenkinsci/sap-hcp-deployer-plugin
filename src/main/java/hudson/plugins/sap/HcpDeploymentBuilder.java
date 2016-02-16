package hudson.plugins.sap;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.Extension;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.plugins.sap.utils.NeoCommandLine;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.util.FormValidation;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class HcpDeploymentBuilder extends Builder {

    private final String host;

    private final String account;

    private final String user;

    private final String password;

    private final String appname;

    private final String packageLocation;

    @DataBoundConstructor
    public HcpDeploymentBuilder(String host, String account, String user, String password, String appname, String packageLocation) {
        this.host = host;
        this.account = account;
        this.user = user;
        this.password = password;
        this.appname = appname;
        this.packageLocation = packageLocation;
    }

    public String getHost() {
        return host;
    }

    public String getAccount() {
        return account;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getAppname() {
        return appname;
    }

    public String getPackageLocation(){
        return packageLocation;
    }

    private String getCurrentJobName(AbstractBuild build, BuildListener listener) {
        try {
            EnvVars envVars = new EnvVars();
            envVars = build.getEnvironment(listener);
            return envVars.get("JOB_NAME");
        } catch (IOException e) {
            listener.getLogger().println(e.getMessage());
        } catch (InterruptedException e) {
            listener.getLogger().println(e.getMessage());
        }
        return null;
    }

    private String defaultWarExtractor(AbstractBuild build, BuildListener listener) {
        return System.getProperty("user.dir") + File.separator + "work" + File.separator + "jobs" + File.separator + getCurrentJobName(build, listener);
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        listener.getLogger().println("deploying application to SAP Hana Cloud Platform");
        listener.getLogger().println("displaying parameters:");
        listener.getLogger().println("hostname : " + host);
        listener.getLogger().println("accountname : " + account);
        listener.getLogger().println("username : " + user);
        listener.getLogger().println("application name : " + appname);
        listener.getLogger().println("war/jar location : " + packageLocation);
        listener.getLogger().println("NEO SDK root directory : " + getDescriptor().neoSdkHome());
        NeoCommandLine commandLine = new NeoCommandLine();

        commandLine.setHost(host);
        commandLine.setAccount(account);
        commandLine.setUser(user);
        commandLine.setPassword(password);
        commandLine.setAppName(appname);

        if (packageLocation != null && !packageLocation.equals(""))
            commandLine.setSourceLocation(packageLocation);
        else {
            listener.getLogger().println("war/jar location is null or empty. So using default location.");
            commandLine.setSourceLocation(defaultWarExtractor(build, listener));
        }
        if (getDescriptor().neoSdkHome() != null && !getDescriptor().neoSdkHome().equals(""))
            commandLine.setNeosdk(getDescriptor().neoSdkHome());
        else {
            listener.getLogger().println("Set NEO_SDK_HOME location correctly");
            return false;
        }

        commandLine.deployApplication(listener.getLogger());
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        private String neo_sdk_home;

        public DescriptorImpl() {
            load();
        }

        public FormValidation doValidateInput(@QueryParameter("host") final String host, @QueryParameter("account") final String account, @QueryParameter("user") final String user,
                                              @QueryParameter("password") final String password, @QueryParameter("appname") final String appname,
                                              @QueryParameter("packageLocation") final String packageLocation) {
            if (host == null || host.equals(""))
                return FormValidation.error("hostname is null or empty");
            if (account == null || account.equals(""))
                return FormValidation.error("accountname is null or empty");
            if (user == null || user.equals(""))
                return FormValidation.error("username is null or empty");
            if (password == null || password.equals(""))
                return FormValidation.error("password is null or empty");
            if (appname == null || appname.equals(""))
                return FormValidation.error("application name is null or empty");

            return FormValidation.ok();
        }

        @Override
        public String getDisplayName() {
            return "SAP Hana Cloud Platform credentials & application details";
        }

        @Override
        public boolean isApplicable(Class type) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest staplerRequest, JSONObject json) throws FormException {
            neo_sdk_home = json.getString("NEO_SDK_HOME");
            save();
            return true;
        }

        public String neoSdkHome() {
            return neo_sdk_home;
        }
    }
}
