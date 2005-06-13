/**********************************************************************************************
 * Informationsvisualisierung VO+UE SS2005, TU-Wien/VRVIS
 * Oliver H�rbinger - 0225192
 * Harald Meyer		- 0225448
 * Tobias Schleser	- 0225349
 * Document: readme.txt
 *********************************************************************************************/
 
# Anforderungen:
 - Java 1.5
 - MySQL 3.23.* oder 4.0.*
 
# Starten des Programms (Windows)
 - ins Verzeichnis "bin" wechseln, und "start.bat" ausf�hren.
 - Nach dem Ausf�hren des obigen Kommandos erscheint ein Fenster, wo die Datenbankdaten 
   eingegeben werden k�nnen (falls die Datenbank nicht automatisch gefunden wurde).
 
# Programmstruktur

|+ sys.main.SysCore: Kern, der alle Komponenten verbindet, und die Kommunikation untereinander 
   erlaubt
  |+ sys.db.*: Datenbankmanager und -Speicherobjekt 
   |- sys.sql.*
   |- sys.sql.managers.*: Klassen die mit der DB kommunizieren
   |- sys.sql.queries.*: SQL-Queries
  |- sys.helpers.*: Diverse Hilfsobjekte und Hilfsklassen
 |- gui.main.*: Hauptfenster, CSV-Import, Datenbankeinstellungsfenster
 |- gui.components.*: Inner Windows
 |- gui.mosaic.*: Klassen f�r den Mosaicplot
 |- gui.scatterplot.*: Klassen f�r den Scatterplot
 |- org.jfree.chart.*: Freie Chartlibrary, die f�r den Performancegraph verwendet wird
 
