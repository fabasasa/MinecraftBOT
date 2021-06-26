package re.alwyn974.minecraft.bot.entity;

import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.packetlib.BuiltinFlags;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import re.alwyn974.minecraft.bot.MinecraftBOT;

import java.net.Proxy;

public class EntityBOT {

    private final String host;
    private final int port;
    private final Proxy proxy;
    private final String username;
    private final String password;
    private final boolean debug;
    private Session client = null;
    private BlockPos connectPos;
    private BlockPos disconnectPos;

    public EntityBOT(String username, String password)  {
        this("localhost", username, password, false);
    }

    public EntityBOT(String username, String password, boolean debug)  {
        this("localhost", username, password, debug);
    }

    public EntityBOT(String host, String username, String password) {
        this(host, 25565, username, password, false);
    }

    public EntityBOT(String host, String username, String password, boolean debug) {
        this(host, 25565, username, password, debug);
    }

    public EntityBOT(String host, int port, String username, String password) {
        this(host, port, Proxy.NO_PROXY, username, password, false);
    }

    public EntityBOT(String host, int port, String username, String password, boolean debug) {
        this(host, port, Proxy.NO_PROXY, username, password, debug);
    }

    /**
     * Instanciate the EntityBOT
     * @param host the minecraft server address
     * @param port the minecraft server port
     * @param proxy the proxy
     * @param username the email of the premium account <br><strong>ONLY MOJANG ACCOUNT</strong>
     * @param password the password of the premium account
     * @param debug activate debug mode
     */
    public EntityBOT(String host, int port, Proxy proxy, String username, String password, Boolean debug) {
        this.host = host;
        this.port = port;
        this.proxy = proxy;
        this.username = username;
        this.password = password;
        this.debug = debug;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isDebug() {
        return debug;
    }

    public Session getClient() {
        return client;
    }

    public BlockPos getConnectPos() {
        return connectPos;
    }

    public BlockPos getDisconnectPos() {
        return disconnectPos;
    }

    public void setConnectPos(BlockPos connectPos) {
        this.connectPos = connectPos;
    }

    public void setDisconnectPos(BlockPos disconnectPos) {
        this.disconnectPos = disconnectPos;
    }

    public void connect() throws RequestException {
        AuthenticationService authService = new AuthenticationService();
        authService.setUsername(this.getUsername());
        authService.setPassword(this.getPassword());
        authService.setProxy(this.getProxy());
        if (isDebug())
            MinecraftBOT.getLogger().debug("Authenticating with account [%s]", this.getUsername());
        authService.login();
        if (isDebug())
            MinecraftBOT.getLogger().debug("Successfully authenticated [%s]", authService.getSelectedProfile().getName());

        MinecraftProtocol protocol = new MinecraftProtocol(authService.getSelectedProfile(), authService.getAccessToken());

        SessionService sessionService = new SessionService();
        sessionService.setProxy(this.getProxy());

        client = new TcpClientSession(this.getHost(), this.getPort(), protocol);
        client.setFlag(BuiltinFlags.PRINT_DEBUG, this.isDebug());
        client.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
        client.addListener(new MCBOTSessionAdapter(this));
        if (isDebug()) {
            MinecraftBOT.getLogger().debug("Connecting to Minecraft server: %s:%d", this.getHost(), this.getPort());
            if (this.getProxy() != Proxy.NO_PROXY)
                MinecraftBOT.getLogger().debug("Connecting with a Proxy: %s", this.getProxy().toString());
        }
        client.connect();
    }

}
