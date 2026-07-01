Math Alarm is an Android alarm clock app that requires the user to solve math questions before the alarm can be dismissed.

## App

<img width="108" height="240" alt="Screenshot_20260701_100705" src="https://github.com/user-attachments/assets/c2851e2e-d31b-47f1-becc-a1ac094d4054" />

<img width="108" height="240" alt="Screenshot_20260701_100818" src="https://github.com/user-attachments/assets/9bd3b848-58b3-4daf-9ca5-98f4adfebd08" />

<img width="108" height="240" alt="Screenshot_20260701_100833" src="https://github.com/user-attachments/assets/463be946-d172-479a-83ac-a865d8864808" />

<img width="108" height="240" alt="Screenshot_20260701_101105" src="https://github.com/user-attachments/assets/715058c5-09f6-4c97-b0d7-b9ad014771c3" />

<img width="108" height="240" alt="Screenshot_20260701_100849" src="https://github.com/user-attachments/assets/874faaf9-2dde-46a9-8e92-6b994f6066b2" />


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
