# Deepak PDF Reader

A professional, feature-rich PDF Reader Desktop Application with a modern JavaFX UI. Built for Windows deployment.

## Core Features
1. **PDF Viewing:** Smooth, high-quality rendering powered by Apache PDFBox.
2. **Modern UI:** Professional layout with a clean sidebar, toolbar, and dark/light themes.
3. **Search engine:** Easily find content inside documents dynamically.
4. **Zoom Controls:** Precise zooming for readability.
5. **Interactive UI:** Smooth page navigation and sidebar access to internal bookmarks.

## Tech Stack
- **Language:** Java 17
- **Framework:** JavaFX 21
- **PDF Engine:** Apache PDFBox 2.0.30
- **Build tool:** Maven

## Build Instructions
First, ensure you have **Java 17** and **Maven** installed.

Build the Fat JAR:
```bash
mvn clean package
```

Run directly via built-in plugin:
```bash
mvn clean javafx:run
```

Run via Jar:
```bash
java -jar target/DeepakPDFReader-1.0.0.jar
```

## JPackage Installation Generation (Windows)

Use the JDK's built-in `jpackage` to build an MSI/EXE installer.
Run the following inside the project root directory:

```bash
jpackage --input target --name "Deepak PDF Reader" --main-jar DeepakPDFReader-1.0.0.jar --main-class com.deepak.pdfreader.AppLauncher --type exe --win-dir-chooser --win-menu --win-shortcut
```
*(If you want an icon, add `--icon src/main/resources/icons/icon.ico`)*

This produces `Deepak PDF Reader-1.0.0.exe` in the root folder.

## GitHub Releases Instructions
1. Upload this codebase to a new GitHub repository.
2. Verify the project builds properly locally and the `Deepak PDF Reader-1.0.0.exe` is generated via the above command.
3. On GitHub, navigate to **Releases** -> **Draft a new release**.
4. Tag it as `v1.0.0`, provide release notes, and drag-and-drop the generated `.exe` file into the attachment area.
5. Publish the release. Users can then download the installer!
