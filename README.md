# Math Alarm

Math Alarm is an Android alarm clock app that requires the user to solve math questions before the alarm can be dismissed.

## Features

- Create multiple alarms
- Enable and disable alarms
- Delete alarms
- Edit existing alarms
- Repeat alarms on selected weekdays
- Show next alarm time on the home screen
- Custom ringtone support
- Vibration option
- Math challenge dismiss system
- Multiple difficulty levels: Easy, Medium, Hard
- Full-screen alarm screen
- Lock-screen alarm support
- Persistent alarm storage using Room database
- Modern UI built with Jetpack Compose

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Room Database
- Hilt Dependency Injection
- MVVM Architecture
- Clean Architecture basics
- AlarmManager
- BroadcastReceiver
- Notification full-screen intent

## How It Works

The user creates an alarm and selects the alarm time, repeat days, ringtone, vibration option, math difficulty, and number of questions.

When the alarm rings, the app opens a full-screen alarm screen. The alarm sound and vibration continue until the user correctly solves all required math questions.

## Project Structure

```text
app/src/main/java/com/example/mathalarm

alarm/
    AlarmReceiver
    AlarmScheduler
    AndroidAlarmScheduler
    AlarmNotificationHelper

audio/
    AlarmSoundPlayer
    AlarmVibrator

core/
    designsystem/
    navigation/

data/
    local/
    mapper/
    repository/

di/
    AppModule

domain/
    model/
    repository/
    usecase/

presentation/
    addalarm/
    editalarm/
    home/
    alarmring/
