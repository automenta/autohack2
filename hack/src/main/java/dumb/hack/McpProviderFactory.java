package dumb.hack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;

import java.time.Duration;
import java.util.Collections;

public class McpProviderFactory {

    public static McpToolProvider create(McpConfig config) {
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(config.getJsonConfig(), JsonObject.class);
        String type = json.get("type").getAsString();

        McpTransport transport = switch (type.toLowerCase()) {
            case "stdio" -> {
                String command = json.get("command").getAsString();
                yield new StdioMcpTransport.Builder()
                        .command(Collections.singletonList(command))
                        .logEvents(true)
                        .build();
            }
            case "http" -> {
                String httpEndpoint = json.get("endpoint").getAsString();
                yield new HttpMcpTransport.Builder()
                        .sseUrl(httpEndpoint)
                        .logRequests(true)
                        .logResponses(true)
                        .build();
            }
            default -> throw new IllegalArgumentException("Unknown or unsupported MCP transport type: " + type);
        };

        McpClient client = new DefaultMcpClient.Builder()
                .transport(transport)
                .toolExecutionTimeout(Duration.ofSeconds(4))
                .build();

        return McpToolProvider.builder()
                .mcpClients(Collections.singletonList(client))
                .build();
    }
}
