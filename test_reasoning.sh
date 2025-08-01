#!/bin/bash
set -x

# Start the application in the background and save its PID
java -jar hack/target/hack-1.0-SNAPSHOT.jar code --provider=mock --api-key=mock > reason_test_output.log 2>&1 &
APP_PID=$!

# Give the app a moment to start up
sleep 5

# Send the command to the application's standard input
# The interactive flag in CodeCommand is on by default, so we need to send "yes"
echo "/reason \"Refactor the Main.java file to have a comment at the top that says '// Hello World'\"" > /proc/$APP_PID/fd/0
sleep 1
echo "yes" > /proc/$APP_PID/fd/0

# Let the app process the command
sleep 5

# Kill the application
kill $APP_PID

# Wait for the process to terminate
wait $APP_PID 2>/dev/null

# The output is in reason_test_output.log
cat reason_test_output.log
