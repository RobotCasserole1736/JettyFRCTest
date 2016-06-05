package websocketTest;

import javax.servlet.annotation.WebServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
@WebServlet(name = "Casserole Data Streamer Servlet", urlPatterns = { "/datastream" })
public class CasseroleDataStreamerServlet extends WebSocketServlet {
	 
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(10000);
        factory.register(CasseroleDataStreamerSocket.class);
    }
}
