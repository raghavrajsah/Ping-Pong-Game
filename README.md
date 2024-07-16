# JPong: A Java-Based Pong Game

## Overview

JPong is a modern rendition of the classic Pong game, originally released in 1972 by Atari. This version is built using Java and features enhanced AI capabilities, allowing for various levels of difficulty and paddle behaviors. The game is designed to be played between a human and an AI, providing an engaging experience with a mix of strategy and reflexes.

## Game Features

- **Classic Pong Gameplay**: Emulates the original Pong experience with paddles and a ball.
- **Human vs. AI**: The game is played between a human player and an AI-controlled opponent.
- **Multiple Paddle Types**: Includes different types of AI paddles with unique behaviors and difficulty levels.
- **Debug Modes**: Offers debugging options to slow down the game and visualize ball trajectories.

## Gameplay

- The game starts with the ball at the center of the window. The human player launches the ball by pressing the space bar.
- The human player controls the left paddle using the up and down arrow keys.
- The AI controls the right paddle.
- Scores and volley counts are displayed at the top of the game window.
- The game can be exited by pressing the Escape key.

## Provided Codebase

### JPongWindow.java
Contains all GUI code and physics calculations for ball and paddle movements.

### HumanPaddle.java & CPUPaddle.java
Abstract classes representing the human and AI-controlled paddles, respectively.

### Launcher.java
The main class to launch the game. Includes debug options for slower game speed and ball trajectory visualization.

## AI Paddle Types

### CPUPaddle
- Basic AI paddle with simple up and down movement logic based on the ball's position.

### CPUAdvanced
- A more advanced AI paddle that predicts the ball's position using its velocity and adjusts its movement accordingly.

### HumanChallenging
- A challenging human paddle with a dynamic length that changes based on gameplay performance.

### CPUExpert
- The most advanced AI paddle with complex movement logic, accounting for ball bounces and predicting final ball positions accurately.

## How to Run

1. Clone the repository:
   ```bash
   git clone git@github.com:raghavrajsah/Ping-Pong-Game.git
   cd jpong
   ```
2. Compile the Java files:
   ```bash
   javac *.java
   ```
3. Run the game:
   ```bash
   java Launcher
   ```

## Future Enhancements

- Add more paddle types with varying behaviors and difficulty levels.
- Implement a multiplayer mode for human vs. human gameplay.
- Enhance the graphical interface with better animations and effects.
- Add sound effects and background music to improve the gaming experience.

## Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request for any enhancements or bug fixes.
