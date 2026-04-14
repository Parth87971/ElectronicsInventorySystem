@echo off
echo ===== Electronics Inventory Management System =====
echo Compiling...
cd /d D:\ElectronicsInventorySystem
if exist out rmdir /s /q out
mkdir out
javac -encoding UTF-8 -cp "lib\*" -d out src\inventory\Main.java src\inventory\config\DBConnection.java src\inventory\model\*.java src\inventory\dao\*.java src\inventory\service\*.java src\inventory\util\*.java src\inventory\ui\*.java
if %errorlevel% neq 0 (
    echo COMPILATION FAILED!
    pause
    exit /b 1
)
echo Compilation successful!
echo Launching application...
start javaw -cp "out;lib\*" inventory.Main
echo Application launched. You can close this window.
timeout /t 3
