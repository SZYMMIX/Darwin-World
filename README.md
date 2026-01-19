# PO_2025_WT1645_CIMOCHOWSKI_OTTE

---

## 🧬 Wariant Projektu: E (Trucizny i Odporność)

Zrealizowaliśmy wariant **E**, który wprowadza do symulacji mechanikę zatruć:
*   Niektóre rośliny na mapie są trujące.
*   Zjedzenie trującej rośliny odejmuje energię zamiast ją dodawać.
*   Zwierzęta mogą posiadać genetyczną odporność na truciznę (określoną przez dopasowanie ich genotypu do wzorca).
*   Użytkownik może włączyć/wyłączyć ten tryb w konfiguracji oraz sterować prawdopodobieństwem wystąpienia trucizny.

---

## 🚀 Zrealizowane Rozszerzenia (Extensions)

W projekcie zaimplementowano szereg dodatkowych funkcjonalności wykraczających poza wersję podstawową.

### 🟢 Interfejs i Konfiguracja
- [x] **Wielowątkowość:** Aplikacja pozwala na uruchomienie wielu niezależnych symulacji w osobnych oknach jednocześnie.
- [x] **Zapis/Odczyt Konfiguracji:** Możliwość zapisywania ulubionych ustawień parametrów do plików i wczytywania ich (system presetów).
- [x] **Skalowanie Mapy:** Wizualizacja mapy automatycznie dostosowuje rozmiar komórek do wielkości okna aplikacji.

### 🟢 Analityka i Dane
- [x] **Wizualizacja Energii:** Kolor zwierzęcia zmienia się płynnie od czerwonego (niska energia) do brązowego (wysoka energia), co pozwala na szybką ocenę stanu populacji.
- [x] **Wykresy na Żywo:** Interaktywny wykres liniowy aktualizowany w czasie rzeczywistym, prezentujący:
    - Liczebność populacji (zwierzęta/rośliny).
    - Średnią energię.
    - Średnią długość życia.
    - Średnią liczbę dzieci.
- [x] **Eksport do CSV:** Po zakończeniu symulacji możliwe jest zapisanie pełnej historii statystyk do pliku `.csv`.

### 🟢 Interakcja ze Zwierzętami
- [x] **Inspektor Zwierzaka:** Po kliknięciu w zwierzę na mapie, w panelu bocznym wyświetlane są jego szczegółowe statystyki (genom, aktywny gen, energia, liczba dzieci, wiek, data śmierci).
- [x] **Podświetlanie Rodziny:** Opcja wizualnego wyróżnienia dzieci oraz wszystkich potomków śledzonego zwierzęcia.
- [x] **Dominujący Genotyp:** Możliwość kliknięcia w najpopularniejszy genotyp na liście statystyk, co powoduje wyróżnienie wszystkich posiadających go zwierząt na mapie.

### 🟢 Kontrola Czasu
- [x] **Oś Czasu:** Możliwość zatrzymania symulacji i przewijania jej "krok po kroku" w przód i w tył, aby przeanalizować historię zdarzeń.

---
