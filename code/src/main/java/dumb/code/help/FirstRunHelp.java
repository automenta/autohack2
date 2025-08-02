package dumb.code.help;

public class FirstRunHelp extends AbstractHelp {

    public FirstRunHelp() {
        super();
        this.steps.add("Let's learn the basics of AutoHack! 🤓");
        this.steps.add("To see the files in the current directory, type: /ls 📂");
        this.steps.add("Great! To see all available commands, type: /help 🆘");
        this.steps.add("You're doing great! You can stop this help any time by typing: /help stop 👋");
    }

    @Override
    public String getWelcomeMessage() {
        return "Welcome to the AutoHack interactive help! Let's get you started. 🚀";
    }

    @Override
    public void processInput(String input) {
        if (isFinished()) {
            return;
        }

        // A simple way to check for command completion.
        // This could be made more robust in the future.
        String command = input.trim().split(" ")[0];

        if (currentStep == 1 && command.equals("/ls")) {
            nextStep();
        } else if (currentStep == 2 && command.equals("/help")) {
            nextStep();
        }
    }
}
