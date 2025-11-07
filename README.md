# 🐍 SnakeSwing

Ein klassisches **Snake-Spiel in purem Java (Swing)** — kein Framework, keine Abhängigkeiten, einfach kompilieren & spielen.  
Ideal für GitHub-Showcase oder kleine Pausen zwischendurch 😄

---

## 🎮 Features

- Komplett **in einer Datei (SnakeSwing.java)**  
- Steuerung mit **WASD oder Pfeiltasten**
- **Pause (Leertaste)** & **Neustart (R)**
- **Score & Highscore**
- **Zunehmende Geschwindigkeit**, je länger du spielst
- Glatte, pixelgenaue Bewegung & Farbverlauf
- Funktioniert auf jedem System mit Java 17+

---

## 🧩 Voraussetzungen

- **Java 17 oder höher**
- Kein JavaFX, kein Build-Tool nötig — reines Swing‑Projekt

---

## ⚙️ Installation & Start

1. Speichere die Datei **SnakeSwing.java** in einen Ordner, z. B.:  
   `C:\Users\user\Desktop\java-programms\snake`

2. Öffne **PowerShell oder CMD** in diesem Ordner

3. Kompiliere das Programm:

   ```powershell
   javac SnakeSwing.java
   ```

4. Starte das Spiel:

   ```powershell
   java SnakeSwing
   ```

---

## 🕹️ Steuerung

| Taste | Funktion |
|:------|:----------|
| ↑ / W | Nach oben |
| ↓ / S | Nach unten |
| ← / A | Nach links |
| → / D | Nach rechts |
| **Leertaste** | Pause / Fortsetzen |
| **R** | Neustart |

---

## 📊 Gameplay

- Du startest mit einer 3‑Segment‑Schlange in der Mitte.  
- Jedes aufgenommene 🍎 (rotes Quadrat) verlängert dich um 1 Segment.  
- Mit jedem Apfel wird das Spiel **leicht schneller**.  
- Wenn du gegen eine Wand oder dich selbst stößt → **Game Over**.  
- Dein **Highscore** bleibt über mehrere Spiele in der Session bestehen.

---

## 💡 Ideen für Erweiterungen

- Highscore-Datei speichern (`highscore.txt`)
- Schwierigkeitsgrade (z. B. Speed, Größe)
- Hindernisse oder Level-System
- Farbmodus „Neon“ oder „Retro“

---

## 📁 Lizenz

MIT License — frei nutzbar & veränderbar.

---

© 2025 Robert Martin
