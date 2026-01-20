# EX3 - Pacman Game Project

This project implements a Pacman game solution in Java. It consists of two main components: a custom lightweight game server (visualization) and a smart autonomous algorithm.

## 🚀 Project Overview

The project is divided into two parts:

1.  **MyPacmanGame (Custom Server):**
    A fully functional custom graphic interface built from scratch. It perfectly handles:
    * Map parsing and rendering from text.
    * Graphic display using `StdDraw` with custom images.
    * Game physics: collision detection (walls), cyclic movement (tunnels), and consuming food.
    * *Note: This version focuses on core mechanics and does not include ghosts or multiple levels.*

2.  **Ex3Algo (The "Brain"):**
    A complete autonomous algorithm that implements the `PacmanAlgorithm` interface.
    * It is designed to handle **ghosts, multiple levels, and complex scoring**.
    * It uses BFS (Breadth-First Search) to find the optimal path to targets.
    * It fully supports cyclic worlds (tunneling).
    * *This algorithm runs successfully in the Ex3main.*

## 🎮 How to Run

### Option 1: Run the Custom Visualization
To see the custom graphics and basic mechanics:
1.  Open `MyPacmanGame.java`.
2.  Run the `main()` method.
3.  You will see Pacman automatically navigating the map, eating food, and teleporting through tunnels.

### Option 2: Run the Full Algorithm (With Ghosts)
To test the `Ex3Algo` logic against ghosts and different levels:
1.  Run the official teacher's main class (`Ex3Main`).
2.  The algorithm will demonstrate its full capabilities, including ghost avoidance and level progression.

## 🤖 Algorithm Logic (`Ex3Algo`)
The algorithm uses a **Breadth-First Search (BFS)** strategy:
1.  It scans the map to locate the nearest food source or bonus.
2.  It calculates the shortest path, considering walls and map cyclicity (wrapping around edges).
3.  It determines the immediate next move to approach the target.

Enjoy :)