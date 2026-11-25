# Regras do ProGuard/R8 (o minify está desligado neste projeto).
# Se ligar o minify, mantenha os models usados pelo Firestore:
-keep class com.techinova.kanban.model.** { *; }
