#!/bin/bash
set -x

# Run the application with the reasoning task
java -jar hack/target/hack-1.0-SNAPSHOT.jar --provider=mock --api-key=mock code --task "Refactor the Main.java file to have a comment at the top that says '// Hello World'" > reason_test_output.log 2>&1

# The output is in reason_test_output.log
cat reason_test_output.log
