# Azelux Client — Minecraft Java 26.2 build on Termux

Minecraft Java 26.2/Fabric requires **JDK 25**. The project is deliberately set up as a single client-only source set so it is easier to build on Android/Termux.

## 1. Check Java

```bash
java -version
javac -version
```

Both must report **25**.

## 2. Install basic tools

```bash
pkg update -y
pkg install -y git unzip wget tar
```

If your Termux repository provides OpenJDK 25, install it with:

```bash
pkg search openjdk
pkg install -y openjdk-25
```

If `openjdk-25` is not available, do not substitute Java 21. Use a JDK 25 Linux/aarch64 distribution inside your existing Debian/Ubuntu proot environment instead.

## 3. Enter the project

```bash
cd ~/Azelux-Java-26.2
```

## 4. Get Gradle

Check first:

```bash
gradle --version
```

If Gradle is available, create the project wrapper:

```bash
gradle wrapper --gradle-version latest
```

Then build using the wrapper:

```bash
./gradlew build --stacktrace
```

If the wrapper already exists, skip the `gradle wrapper` command.

## 5. Find the finished mod

```bash
ls -lh build/libs/
```

Use the JAR with the shortest filename that is not a `-sources.jar`.

## 6. If the build fails

Run:

```bash
./gradlew build --stacktrace --info
```

Then send the **first `error:` line and the next ~30 lines**. That is the useful compiler information; the whole cache/download log is usually unnecessary.

## Important

Do not run the build with Java 21. If `java -version` reports anything other than 25, fix Java first.
