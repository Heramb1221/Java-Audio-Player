Java Audio Player
=================

> A desktop audio player built with Java SE, Swing, and the Java Sound API — engineered with explicit attention to EDT thread safety, native audio resource management, and JPMS module configuration.

---

## About The Project
-----------------

This project is a desktop audio player application written entirely in Java SE — no external libraries, no build overhead, no framework dependencies. It uses Java's built-in javax.sound.sampled API for audio playback and javax.swing for the graphical interface.

The engineering focus is not just on making audio play — it is on doing it **correctly** within Java's concurrency and resource model. Specifically:

*   All UI mutations are dispatched on the **Event Dispatch Thread (EDT)** — the foundational contract of Swing's single-threaded UI model, analogous to the main thread in Android or the browser's UI thread in JavaScript.
    
*   javax.swing.Timer is used deliberately over java.util.Timer because it fires callbacks on the EDT, eliminating the need for manual invokeLater calls in the progress update loop.
    
*   The Clip interface is chosen over SourceDataLine for its **instant seek capability** — a deliberate API tradeoff accepting O(n) memory load in exchange for O(1) seek latency.
    
*   The application declares a **JPMS module** (module-info.java) with requires java.desktop, demonstrating awareness of the Java Platform Module System introduced in Java 9.
    

This project serves as an in-depth exploration of Java's audio architecture, Swing's event model, and the native resource lifecycle that underlies both.

---

## Project Status

| Attribute | Detail |
|---|---|
| **Status** | Archived Learning Project |
| **Type** | Learning-Oriented Engineering Prototype |
| **Maturity** | Functional Core Complete |
| **Purpose** | Built to explore Java audio handling, Swing UI architecture, and media playback lifecycle management |
| **Java Version** | Java 11+ (JPMS compatible) |
| **Supported Formats** | WAV, AIFF, AU (PCM-based formats supported natively by Java Sound API) |

> This project successfully served its purpose as a hands-on exploration of desktop application architecture, Java Sound API behavior, event-driven UI patterns, and media state management. The repository is now maintained primarily as a portfolio and learning artifact rather than an actively developed product.

---

## Live Demo
---------

This is a desktop application distributed as a runnable JAR. There is no hosted web demo.


Why I Built This
----------------

Most Java GUI tutorials stop at "Hello, World" with a button. I wanted to go further and answer:

*   How does Java's audio playback actually work at the API level?
    
*   What does "EDT-safe" really mean in practice, and what breaks if you violate it?
    
*   What is the difference between Clip and SourceDataLine, and when does each tradeoff matter?
    
*   How does the Java Platform Module System affect a real application's dependency graph?
    

This project is the result of working through those questions with running code. The known architectural weaknesses (God Class structure, blocking I/O on EDT, resource leaks) are documented honestly — they became the most valuable learning surfaces in the project, each pointing toward a production-grade improvement I am actively implementing.

---

## Features
--------

### Core Playback Features

*   Open and play WAV, AIFF, and AU audio files via JFileChooser
    
*   Stop audio playback at current position
    
*   Reset playback position to the beginning
    
*   Display the currently loaded file name
    

### Engineering Features

*   **EDT-safe progress updates** via javax.swing.Timer (fires on EDT — no manual invokeLater needed)
    
*   **Real-time position display** in M:SS / M:SS format, updated every 100ms
    
*   **JProgressBar** reflecting playback completion percentage
    
*   **Native audio resource cleanup** — previous Clip is closed before a new file is loaded
    
*   **JPMS module declaration** (requires java.desktop) for Java 9+ module path compatibility
    

### UI Features

*   Clean BorderLayout + GridLayout Swing interface
    
*   Status bar feedback for all playback states (Playing, Stopped, Reset, Error)
    
*   File name display on successful load
    
*   Custom application icon via JFrame.setIconImage
    
---

## Tech Stack

| Layer | Technology | Why Used |
|---|---|---|
| **Language** | Java SE (11+) | JDK-native libraries — zero external dependencies |
| **GUI Framework** | Java Swing (`javax.swing`) | Built-in, mature, cross-platform desktop UI toolkit |
| **Audio Engine** | Java Sound API (`javax.sound.sampled`) | Native WAV/AIFF/AU playback — no third-party codec |
| **Timer** | `javax.swing.Timer` | EDT-safe periodic callbacks — thread-safety by design |
| **Module System** | JPMS (`module-info.java`) | Java 9+ strong encapsulation and module graph declaration |
| **Build** | IDE-based (Eclipse / IntelliJ) | Prototype stage — Maven/Gradle migration planned |

---

## Architecture

### Component Interaction

```text
┌─────────────────────────────────────────────────────┐
│                 JFrame (Player)                    │
│                                                     │
│  ┌──────────────────────────────────────────────┐   │
│  │        topPanel (BorderLayout.NORTH)         │   │
│  │  ┌────────────────────────────────────────┐  │   │
│  │  │ fileLabel                              │  │   │
│  │  │ "File Name: track.wav"                 │  │   │
│  │  ├────────────────────────────────────────┤  │   │
│  │  │ progressBarPanel                       │  │   │
│  │  │  position "1:23 / 3:45"                │  │   │
│  │  │  progressBar [████████░░░░░░░░░░░░]    │  │   │
│  │  └────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────┘   │
│                                                     │
│  ┌──────────────────────────────────────────────┐   │
│  │      buttonPanel (BorderLayout.CENTER)       │   │
│  │                                              │   │
│  │     [ Play ] [ Stop ] [ Reset ]              │   │
│  └──────────────────────────────────────────────┘   │
│                                                     │
│  status: "Playing..." / "Stopped." / "Error..."    │
└─────────────────────────────────────────────────────┘
```

---

### Audio Playback Lifecycle

```text
User clicks "Play"
        │
        ▼ (EDT)

JFileChooser dialog opens
        │
        ▼

File selected
        │
        ▼

playAudio(File) called
        │
        ├── AudioSystem.getAudioInputStream(file)
        │      Reads file header
        │      Detects PCM format
        │
        ├── clip.close()
        │      Releases previous native audio line
        │      Frees heap buffer
        │
        ├── AudioSystem.getClip()
        │      Acquires Clip from OS mixer
        │
        │      Linux   → ALSA
        │      macOS   → CoreAudio
        │      Windows → DirectSound
        │
        ├── clip.open(audioStream)
        │      Buffers entire PCM stream into heap
        │      Complexity: O(file_size)
        │
        ├── clip.start()
        │      Internal daemon thread begins playback
        │
        └── timer.start()
               │
               ▼
       javax.swing.Timer fires every 100ms on EDT
               │
               ▼
       clip.getMicrosecondPosition()
               │
               ▼
       Format playback position
               │
               ▼
       Update progress bar + labels
```


## Folder Structure

```text
java-audio-player/
├── src/
│   ├── module-info.java
│   │   # JPMS module declaration
│   │   # requires java.desktop
│   │
│   └── player/
│       ├── Main.java
│       │   # Application entry point
│       │   # EDT bootstrap via SwingUtilities.invokeLater()
│       │
│       └── Player.java
│           # Core application class
│           # GUI layout
│           # Audio playback logic
│           # Event handling
│
├── AudioPlayer.png
│   # JFrame window icon
│
├── Audio Player.png
│   # Application screenshot
│
└── README.md
```

> **Note:**  
> The current architecture uses a single-class design (`Player.java`) where UI rendering, audio management, and event handling are tightly coupled.
>
> An MVC-style refactor is planned to separate:
>
> - UI rendering
> - Audio service logic
> - Playback state management
> - Event coordination

---

## Installation
------------

### Prerequisites

*   Java 11 or higher ([Download JDK](https://adoptium.net/))
    
*   An IDE (IntelliJ IDEA / Eclipse) or command-line javac
    

### Running from Source
## Installation & Running

### Prerequisites

- Java 11 or higher

### Clone the Repository

```bash
git clone https://github.com/Heramb1221/java-audio-player.git
cd java-audio-player
```

### Compile

```bash
javac -d out --module-source-path src $(find src -name "*.java")
```

### Run

```bash
java --module-path out -m AudioPlayer/player.Main
``` 

### Running from IDE

1.  Import as a Java project in IntelliJ IDEA or Eclipse
    
2.  Ensure the module source root is set to src/
    
3.  Run player.Main as the entry point
    
4.  Ensure AudioPlayer.png is in the working directory (project root) for the icon to load

---

## Usage
-----

| Action | How |
|---|---|
| **Load & Play audio** | Click **Play** → select a WAV/AIFF/AU file → playback starts |
| **Stop playback** | Click **Stop** — pauses at the current playback position |
| **Reset to start** | Click **Reset** — returns playback to `0:00` |
| **View progress** | Progress bar and playback timer update every `100ms` during playback |
| **View file name** | Currently loaded file name is displayed in the top panel |

---

## Screenshots
-----------
![Audio Player](https://github.com/Heramb1221/Java-Audio-Player/blob/main/Audio%20Player.png)

---

## Known Issues
------------

| Issue | Severity | Status |
|---|---|---|
| `AudioInputStream` not closed after `clip.open()` — potential file handle leak | Medium | Fix planned (`try-with-resources`) |
| `clip.open()` blocks EDT — UI freeze on large audio files | High | Fix planned (`SwingWorker`) |
| Division by zero possible if audio duration is `0ms` | Medium | Fix planned (guard clause) |
| Natural playback completion not detected — no `"Finished"` state | Medium | Fix planned (`LineListener`) |
| Icon loaded via relative file path — breaks in packaged JAR distribution | Medium | Fix planned (classpath resource loading) |
| Variable named `File File` — collides semantically with `java.io.File` | Low | Rename to `currentFile` planned |
| `catch (Exception ex)` too broad — masks programming/runtime errors | Low | Split into specific exception types |
| Reset action does not update position label when timer is stopped | Low | Fix planned |

---

## Technical Debt

| Debt Item | Impact | Resolution Path |
|---|---|---|
| God Class (`Player.java`) — UI, playback logic, and event handling tightly coupled | High — difficult to test and maintain | MVC refactor: `AudioModel`, `PlayerView`, `AudioController` |
| No build tool (Maven / Gradle) | Medium — non-reproducible builds and manual compilation | Migrate to Gradle using the `application` plugin |
| No unit tests | High — no regression protection | Extract utilities such as `TimeFormatter` and `FileValidator`; add `JUnit 5` |
| Anonymous `ActionListener` classes (pre-Java 8 style) | Low — verbose and harder to read | Replace with lambda expressions |
| `printStackTrace()` used for error reporting | Low — poor production diagnostics | Replace with `java.util.logging` or SLF4J |

---

## Challenges Faced
----------------

**1\. Thread Safety in Swing**Understanding that Swing's rendering model is fundamentally single-threaded — and that violating the EDT contract produces non-deterministic bugs rather than immediate crashes — was the most important engineering insight from this project.

**2\. javax.swing.Timer vs. java.util.Timer**Both look identical at first glance. The distinction — that javax.swing.Timer posts callbacks to the EDT while java.util.Timer fires on a background thread — is not obvious from the API surface and required reading the Swing concurrency documentation carefully.

**3\. Clip vs. SourceDataLine**The Java Sound API offers two playback approaches with non-obvious tradeoffs. Understanding when Clip's memory cost is acceptable (short files, random seek) versus when SourceDataLine is required (streaming, large files) required hands-on experimentation.

**4\. JPMS Module Configuration**Java 9's module system requires explicit dependency declaration. Getting requires java.desktop correct — and understanding why javax.sound.sampled lives inside java.desktop rather than a separate module — required studying the JDK module graph.

**5\. Native Resource Lifecycle**Java's GC handles heap memory but not native OS resources (audio lines, file descriptors). Discovering that Clip holds a native audio line that must be explicitly released — and that AudioInputStream holds a file descriptor that stays open until closed — was a practical lesson in the limits of garbage collection.

---

## What I Learned
--------------

*   **Swing's EDT model** is analogous to Android's UI thread, the browser's main thread, and iOS's main queue — Java's implementation of the universal "single-threaded UI" pattern.
    
*   **Native resource management** in Java is fundamentally different from memory management — GC cannot reclaim file handles or OS audio lines.
    
*   **API selection matters** — Clip and SourceDataLine solve the same problem differently; choosing the wrong one has real runtime consequences.
    
*   **JPMS module declarations** are more than boilerplate — they define the module graph, control encapsulation, and determine what reflective access is permitted at runtime.
    
*   **God Classes** feel fine at 200 lines but become unextendable immediately when a feature like volume control or playlists is added — the MVC pressure is real.
    
*   **Polling vs. event-driven** are not just philosophical choices — LineListener physically cannot provide playback position events, making a polling timer the only viable approach for progress bars in the Java Sound API.
    
---

## Future Scope
------------

*   Refactor to MVC — AudioModel, PlayerView, PlayerController
    
*   SwingWorker for non-blocking file load
    
*   try-with-resources for AudioInputStream
    
*   FileNameExtensionFilter on JFileChooser
    
*   LineListener for natural playback end detection
    
*   Guard clause for zero-duration audio
    
*   Fix icon to load from classpath
    
*   Gradle build + application plugin
    
*   JUnit 5 unit tests for utility classes
    
---

## Repository Philosophy
---------------------

This repository is built on **honest iterative engineering** — not polished completeness. The v0.1 code works, and its architectural weaknesses are documented openly. Each known issue is a concrete engineering problem with a specific solution path.

The goal is not to present a finished product but to demonstrate the engineering process: making deliberate tradeoffs, understanding why they are tradeoffs, and knowing exactly what the next iteration requires.

---

## Contributing

Contributions, improvements, and refactoring suggestions are welcome.

```bash
# Fork the repository

# Create a feature branch
git checkout -b feature/your-feature-name

# Commit changes
git commit -m "feat: description of change"

# Push branch
git push origin feature/your-feature-name

# Open a Pull Request
```

### Areas Open for Contribution

- MVC architecture refactor
- MP3 / FLAC support via Java Sound SPI
- `JUnit 5` test coverage
- Gradle build configuration
- JavaFX migration exploration
    
---

## License
-------

Distributed under the MIT License. See LICENSE for details.

---

## Contact

**Heramb Chaudhari**

[![GitHub](https://img.shields.io/badge/GitHub-Heramb1221-black?style=for-the-badge&logo=github)](https://github.com/Heramb1221)

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Heramb%20Chaudhari-blue?style=for-the-badge&logo=linkedin)](https://www.linkedin.com/in/heramb-chaudhari)

[![Email](https://img.shields.io/badge/Email-hchaudhari1221%40gmail.com-red?style=for-the-badge&logo=gmail)](mailto:hchaudhari1221@gmail.com)
