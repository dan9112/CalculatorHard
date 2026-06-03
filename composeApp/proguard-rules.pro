# Игнорируем опциональные зависимости sqlite-jdbc и skiko (чтобы починить ошибку сборки Desktop)
-dontwarn org.slf4j.**
-dontwarn org.sqlite.**
-dontwarn org.jetbrains.skiko.**
-dontwarn com.jetbrains.**
