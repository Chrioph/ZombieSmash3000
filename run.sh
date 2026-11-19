#!/usr/bin/env sh
# Kompiliert und startet ZombieSmash3000 (benötigt JDK 8+).
# Weitere Argumente (z.B. --debug) werden an das Spiel durchgereicht.
set -e
cd "$(dirname "$0")"
rm -rf bin && mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name '*.java')
exec java -cp "bin:res" dev.codenmore.tilegame.Launcher "$@"
