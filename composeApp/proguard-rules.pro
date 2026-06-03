# Игнорируем отсутствующие классы SLF4J (опциональная зависимость SQLite)
-dontwarn org.slf4j.**

# Игнорируем отсутствующие классы JetBrains Runtime (есть только в JBR, нет в Temurin)
-dontwarn com.jetbrains.**

# (Опционально) Убирает спам в логах про дубликаты META-INF и другие Note
-dontnote **
