package dumb.code.commands.ls;

import dumb.code.Code;
import dumb.code.InMemoryFileManager;
import dumb.code.MessageHandler;
import dumb.code.help.HelpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class LsCommandTest {

    private InMemoryFileManager fileManager;
    private MessageHandler messageHandler;
    private LsCommand lsCommand;

    @BeforeEach
    void setUp() {
        fileManager = new InMemoryFileManager();
        HelpService helpService = mock(HelpService.class);
        Code code = new Code(null, fileManager, null, helpService);
        messageHandler = code.messageHandler;
        lsCommand = new LsCommand(code);
    }

    @Test
    void testExecute() throws java.io.IOException {
        fileManager.writeFile("test1.txt", "content1");
        fileManager.writeFile("test2.txt", "content2");

        lsCommand.execute(new String[]{});

        java.util.List<String> messages = messageHandler.getMessages();
        assertEquals(1, messages.size());
        assertEquals("system: test1.txt\ntest2.txt", messages.get(0));
    }
}
