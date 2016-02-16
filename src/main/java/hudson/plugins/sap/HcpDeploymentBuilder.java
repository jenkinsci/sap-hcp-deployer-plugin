package hudson.plugins.sap;

import hudson.Launcher;
import hudson.Extension;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.plugins.sap.utils.NeoCommandLine;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import net.sf.json.JSONObject;

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

        if (host != null && !host.equals(""))
            commandLine.setHost(host);
        else {
            listener.getLogger().println("hostname is null or empty");
            return false;
        }
        if (account != null && !account.equals(""))
            commandLine.setAccount(account);
        else {
            listener.getLogger().println("accountname is null or empty");
            return false;
        }
        if (user != null && !user.equals(""))
            commandLine.setUser(user);
        else {
            listener.getLogger().println("username is null or empty");
            return false;
        }
        if (password != null && !password.equals(""))
            commandLine.setPassword(password);
        else {
            listener.getLogger().println("password is null or empty");
            return false;
        }
        if (appname != null && !appname.equals(""))
            commandLine.setAppName(appname);
        else {
            listener.getLogger().println("application name is null or empty");
            return false;
        }
        if (packageLocation != null && !packageLocation.equals(""))
            commandLine.setSourceLocation(packageLocation);
        else {
            listener.getLogger().println("war/jar location is null or empty");
            return false;
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
