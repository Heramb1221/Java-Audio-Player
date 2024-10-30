
# Heramb's Audio Player

A brief description of what this project does and who it's for


## Screenshots

![Audio Player](https://github.com/Heramb1221/Java-Audio-Player/blob/main/Audio%20Player.png)


## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Usage](#usage)
- [Code Structure](#code-structure)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgments](#acknowledgments)
## Project Overview
Heramb's Audio Player is a simple yet functional audio player application built using Java. It allows users to play, stop, and reset audio files while providing real-time feedback on playback position and status. The user interface is designed with Swing, offering an intuitive layout for easy interaction.
## Features
- **Play Audio**: Users can select and play audio files.
- **Stop and Reset**: Control playback with stop and reset functionalities.
- **Progress Bar**: Displays the current position of the audio track.
- **File Name Display**: Shows the currently playing audio file's name.
- **Real-time Position Updates**: Updates the playback position and total duration dynamically.
## Technologies Used
- **Java**: The primary programming language used for development.
- **Java Swing**: For building the graphical user interface (GUI).
- **Java Sound API**: For audio playback and management.

To run the project locally, follow these steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/Heramb1221/Responsive-Movie-Website.git
   ```
2. Navigate to the project directory:
    ```bash
   cd Responsive-Movie-Website
   ```
3. Compile the Java file (assuming you have JDK installed):
    ```bash
   javac Player.java
   ```
4. Run the application:
    ```bash
   java Player
   ```
Make sure you have audio files available on your system to test the player functionality.
## Usage
- Upon launching the application, users can select an audio file by clicking the "Play" button.
- The application will display the file name and begin playback.
- Use the "Stop" button to pause the audio and "Reset" to return to the beginning.
- The progress bar updates in real-time to reflect the current playback position.


## Code Structure
```bash
Player.java           # Main Java file containing the Player class
AudioPlayer.png       # Application icon displayed on the JFrame
```


## Contributing
Contributions are greatly appreciated! To contribute to this project, please follow these guidelines:

1. Fork the repository on GitHub.
2. Create a new branch for your feature:
   ```bash
   git checkout -b feature/YourFeature
   ```
3. Make your changes and commit them:
   ```bash
   git commit -m "Description of your changes"
   ```
4. Push your changes to your forked repository
   ```bash
   git push origin feature/YourFeature
   ```
5. Create a pull request detailing your changes for review.

## License
This project is licensed under the MIT License. See the [LICENSE](https://choosealicense.com/licenses/mit/) file for details.


## Acknowledgements

 - Special thanks to the Java Sound API documentation for providing insights on audio playback.
 - Inspiration from various Java projects and Swing tutorials.
