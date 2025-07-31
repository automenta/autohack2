package dumb.hack;

import dumb.hack.tools.HackToolProvider;
import dumb.mcr.MCR;
import dumb.mcr.ReasoningResult;
import dumb.mcr.Session;

import java.util.Properties;

public class Agent {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -jar hack-1.0-SNAPSHOT.jar <task>");
            System.exit(1);
        }

        String task = String.join(" ", args);

        String apiKey = System.getenv("OPENAI_API_KEY");
        Properties config = new Properties();
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("OPENAI_API_KEY not found. Using mock LLM.");
            config.setProperty("llm.provider", "mock");
        } else {
            config.setProperty("llm.provider", "openai");
            config.setProperty("llm.apiKey", apiKey);
        }
        config.setProperty("llm.model", "gpt-4o-mini");

        MCR mcr = new MCR(config);
        HackToolProvider toolProvider = new HackToolProvider();
        Session session = mcr.createSession(toolProvider);

        System.out.println("Executing task: " + task);
        ReasoningResult result = session.reason(task);

        System.out.println("Task finished.");
        System.out.println("Final Answer: " + result.answer());
    }
}
