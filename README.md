# Microsistem plăți – Bancă & ATM-uri (JADE)

Proiect Java care simulează o bancă servită de mai multe ATM-uri, implementate ca **agenți JADE**.
Utilizatorul se autentifică pe baza unui **PIN**, poate deschide cont, depune bani și retrage bani de la ATM (soldul este actualizat corect în bancă).

## Cerințe
- **Java JDK 8** (în proiect s-a folosit `JavaSE-1.8`)
- JADE inclus în repo: `lib/jade.jar`
- (Opțional) Eclipse IDE for Java Developers

## Structura proiectului
- `src/proiect/` – surse (`ATMAgent.java`, `BankAgent.java`, `UserGuiAgent.java`)
- `bin/` – clase compilate (generate de Eclipse)
- `lib/jade.jar` – librăria JADE

---

## Import în Eclipse (recomandat)
1. Clonează repo-ul:
   ```bash
   git clone https://github.com/CodeWizard444/Proiect_SI_LacatusEduard
   cd Proiect_SI_LacatusEduard
2. În Eclipse:
File → Import...
General → Existing Projects into Workspace
Selectează folderul proiectului
Finish

3. Verifică Java 8 (dacă e nevoie):
Click dreapta pe proiect → Properties
Java Compiler / Java Build Path → setează pe Java 8

## Rulare din consolă (Windows)
1) Intră în folderul proiectului
   cd Proiect_SI_LacatusEduard
2) (Opțional) Compilează sursele (dacă nu ai deja bin/)
   mkdir bin
javac -cp "lib\jade.jar" -d bin src\proiect\*.java
3) Pornește agenții (configurația folosită în proiect)
   java -cp "bin;lib/jade.jar" jade.Boot -gui "Banca:proiect.BankAgent;ATM1:proiect.ATMAgent;ATM2:proiect.ATMAgent;Utilizator:proiect.UserGuiAgent"

## Rulare din consolă (Linux / macOS)
1) Intră în folderul proiectului
   cd Proiect_SI_LacatusEduard
2) (Opțional) Compilează sursele
   mkdir -p bin
javac -cp "lib/jade.jar" -d bin src/proiect/*.java
3) Pornește agenții
Pe Linux/macOS separatorul de classpath este : (nu ;)
java -cp "bin:lib/jade.jar" jade.Boot -gui "Banca:proiect.BankAgent;ATM1:proiect.ATMAgent;ATM2:proiect.ATMAgent;Utilizator:proiect.UserGuiAgent"
