# This Step Counter application is created within the scope of my internship at IMT Mines Ales on the subject of Burnout Syndrome Detection using Smartphones' data
# Current Version : 1.00

This Step Counter is capable of computing : 
- The number of steps a person has walked
- Walked distance
- Walking time
- Walking average speed
- Record the start time and the end time of each walking period

There are three buttons on the UI : Start, Stop and View Record
- Once clicking the Start button, the app will start to record users' walking data. It can automatically detect when user stop walking, until 3 seconds, the app stop recording data and insert it into the database.
- The Stop button stops the step counter, as well as the walking time's chronometer
- The View Record button is used to display all the recorded data.

# Functionalities by version :
1.00 :
- Steps are detected using smartphone's accelerometer with a sensivity of 50.0
- Distance is computed by multiplying the number of steps with a step's average length of 75cm.
- Maximum delay of stopping walking is 3 seconds
- Speed is calculated by dividing distance to walking time
- End time is detected by comparing the current number of steps with the previous number of steps
- Using SQLite Database
- Data displayed in textual form contained in an alert dialog

# Changelogs :
1.00 : 
- Step Counter's prototype released
