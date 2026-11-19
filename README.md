# Starten

Voraussetzung: ein JDK (Java 8 oder neuer, getestet mit Java 21).

* Linux/macOS: `./run.sh`
* Windows: `run.bat`

Die Skripte kompilieren `src/` nach `bin/` und starten das Spiel mit `res/` im Classpath. Startoptionen (siehe unten) können einfach angehängt werden, z.B. `./run.sh --debug`.

Steuerung: `WASD` laufen, Pfeiltasten angreifen, `Esc` öffnet einen Bestätigungsdialog (im Spiel: zurück ins Hauptmenü, im Hauptmenü: Spiel beenden). Im Dialog wählen `←`/`→` bzw. `A`/`D`, `Enter` bestätigt, `Esc` oder `N` bricht ab, `Y`/`J` bestätigt direkt; die Buttons sind auch mit der Maus klickbar.

# Texturen

Die Welt-Texturen (Gras, Erde, Stein, Zielfeld, Felsen, Baum, Holzblöcke) werden von `tools/generate_textures.py` erzeugt, Spielfigur, Zombie, Items und Truhe von `tools/generate_sprites.py` (der Oger ist handgezeichnet). Beide Skripte benötigen Python 3 mit Pillow. Nach Änderungen das jeweilige Skript ausführen, z.B. `python3 tools/generate_sprites.py`; `--preview vorschau.png` rendert stattdessen eine Beispielszene.

# Testmodus
Im Hauptmenü startet `TEST MODE` direkt in der Heimatwelt, mit 30 Holzstämmen, 30 Steinen, 10 Samen, je 20 Holz-/Steinböden und -mauern, 3 Kisten und 30 Pfeilen, um Bauen und Crafting auszuprobieren. Oben rechts steht dann `TEST MODE`. Das Zielfeld der Heimatwelt führt ins erste Level; nach dem Zurückkehren ins Hauptmenü startet ein normales Spiel wieder mit leerem Inventar.

# Start Optionen

## --debug
Startet das Spiel im Debug Modus: Hitboxen von Entities werden rot eingefärbt, der Spieler kann durch Wände laufen und von jedem zufällig erzeugten Level wird eine Übersichtskarte als `map.png` gespeichert.

# Zufällige Level
Nach den beiden handgebauten Leveln geht es endlos mit zufällig erzeugten Dungeons weiter (`worlds/DungeonGenerator.java`): Räume, die über Gänge verbunden sind, das Ziel liegt im vom Start am weitesten entfernten Raum. Mit jedem Level werden die Karten größer und es gibt mehr Gegner, ab Level 3 auch Oger. Felsen und Bäume werden so platziert, dass sie nie einen Weg versperren.


# DISCLAIMER: Alles was hier steht ist eine Mischung aus dem momentanen Stand und meiner Interpretation der Zielsetzung. Sollte etwas missfallen, inkorrekt sein oder noch zu konkretisieren sein ergänzt entsprechend dieses README.

# Allgemein

In diesem Spiel bewegt man sich durch Level, besiegt Gegner und sammelt Gegenstände die dann genutzt werden können um die eigene Heimatwelt, zu der man zwischen jedem Level gelangt, auszubauen.

Dabei stehen einem grundsätzlich ein Schwert und ein Bogen zur Verfügung.

# Kisten
Steht man neben einer Kiste, öffnet `E` sie (sonst öffnet `E` das Inventar). Links steht das eigene Inventar, rechts der Inhalt der Kiste: `W`/`S` wählt einen Eintrag, `A`/`D` wechselt die Seite, `Enter` legt ein Stück auf die andere Seite, `Shift+Enter` den ganzen Stapel, ein Mausklick ebenfalls ein Stück. `E` oder `Esc` schließt die Kiste. Jede Kiste hat ihren eigenen Inhalt; wird sie zerstört, fällt alles heraus.

# Crafting
`C` öffnet das Crafting-Menü: links die Rezepte, rechts was man dafür braucht (grün = genug da, rot = fehlt). `W`/`S` wählt ein Rezept, `Enter` craftet einmal, `Shift+Enter` so oft wie möglich (oder per Maus auf CRAFT klicken). `C` oder `Esc` schließt das Menü.

| Rezept | Ergebnis |
|---|---|
| 1 Log | 2 Wood Floor |
| 2 Log | 1 Wood Wall |
| 1 Rock | 2 Stone Floor |
| 2 Rock | 1 Stone Wall |
| 3 Log | 1 Chest |

# Bauen in der Heimatwelt
In der Heimatwelt kann gebaut werden: Inventar öffnen (`E`), einen Gegenstand wählen und `Q` drücken. Eine Vorschau zeigt, wo gebaut wird (grün = möglich, rot = nicht möglich, der Grund steht darunter): die Kachel unter der Maus, wenn sie in Reichweite ist, sonst die Kachel vor dem Spieler. Linksklick oder `R` baut, Rechtsklick oder `X` baut einen Boden oder eine Mauer wieder ab (das Material kommt zurück), `Q` oder `Esc` beendet das Bauen.

* Böden sind begehbar, Mauern nicht. Mauern lassen sich nicht auf Figuren, Objekte oder herumliegende Items bauen.
* Kisten werden genauso platziert.
* Samen werden auf Gras oder Erde gepflanzt und wachsen nach jedem abgeschlossenen Level eine Stufe, insgesamt 5 Stufen bis zum ausgewachsenen Baum. Steht man daneben, wird die Stufe angezeigt. Ab Stufe 3 versperrt der Stamm den Weg. Ein ausgewachsener Baum gibt beim Fällen Holz und Samen, eine junge Pflanze ihren Samen zurück.
* Das Zielfeld in der Heimatwelt führt zum nächsten Level weiter.

# Heimatwelt
Generell soll die Heimatwelt eine Anlaufstelle zwischen den Leveln bieten. Dabei übernimmt sie ähnliche Funktionen wie das Hub in Monster Hunter: durch regelmäßigen Ressourcenzuwachs, NPC Interaktionen und weitere Gimmicks hat der Spieler jedes Mal wenn er in die Heimatwelt zurückkehrt etwas zu tun.

# Ideensammlung
* Farming: In der Heimatwelt können Samen angepflanzt werden die eine bestimmte Zeit brauchen um auszuwachsen und damit erntbare Gegenstände erzeugen. Dabei ist die Zeiteinheit eine Anzahl von Leveln (so wie in Monster Hunter)
* entdeckbare und anheuerbare NPC's: Anstatt jeden kommenden NPC direkt in der Heimatwelt zu platzieren könnte man diese in Leveln verstecken. Dort kann der Spieler den NPC ansprechen und davon überzeugen in seine Heimatwelt zu ziehen und dort seine Dienste anzubieten. (Inspiration PoE)
