# EcoRecolecta ECOLIM

Proyecto Android Studio desarrollado para el Trabajo Final del curso
**Diseño y Desarrollo de Aplicaciones Móviles** (SENATI), en base al caso
práctico de la empresa ECOLIM S.A.C.

## Configuración de Firebase (login)
El login (`MainActivity`) usa **Firebase Authentication** (correo/contraseña),
igual que en el proyecto de referencia del curso. Antes de compilar:

1. Crea un proyecto en [Firebase Console](https://console.firebase.google.com).
2. Agrega una app Android con el `applicationId`: `com.senati.ecorecolecta`.
3. Descarga el archivo real `google-services.json` y colócalo en `app/`
   (reemplazando/renombrando la plantilla `app/google-services.json.EXAMPLE`
   incluida solo como referencia de estructura).
4. En **Authentication > Sign-in method**, habilita el proveedor
   **Correo electrónico/contraseña**.
5. Crea al menos un usuario de prueba (Authentication > Users > Add user),
   por ejemplo `operario1@ecolim.com` / `123456`, para poder iniciar sesión.

## Cómo abrir el proyecto
1. Abre Android Studio (Flamingo o superior).
2. **File > Open** y selecciona la carpeta `EcoRecolectaECOLIM`.
3. Sigue los pasos de "Configuración de Firebase" de arriba (sin
   `google-services.json` real, el build falla con
   *"File google-services.json is missing"*).
4. El proyecto no incluye los binarios del Gradle Wrapper (`gradlew`,
   `gradlew.bat`, `gradle-wrapper.jar`) porque se generaron fuera de
   Android Studio. Al abrirlo, Android Studio mostrará un aviso tipo
   *"Gradle wrapper is missing"* — simplemente acepta que lo regenere
   automáticamente (o ve a **File > Sync Project with Gradle Files**).
   Ya se incluye `gradle/wrapper/gradle-wrapper.properties` apuntando a
   Gradle 8.7, que es la versión que Android Studio usará al recrearlo.
5. Espera a que Gradle sincronice (descargará las dependencias declaradas
   en `app/build.gradle`: AppCompat, Material, RecyclerView, CardView,
   Firebase Auth y Retrofit + Gson para el consumo de la API RESTful).
6. Ejecuta en un emulador o dispositivo con API 24+.

## Credenciales de prueba
Usa el correo y contraseña que creaste en Firebase Authentication
(paso 5 de la sección anterior), por ejemplo:
- Correo: `operario1@ecolim.com`
- Contraseña: `123456`

## Estructura
```
app/src/main/java/com/senati/ecorecolecta/
 ├── MainActivity.java             Login con Firebase Authentication
 ├── RegistroResiduoActivity.java  Formulario de registro de residuos
 ├── ListaResiduosActivity.java    Listado (RecyclerView) + botón "Cerrar sesión" (Firebase)
 ├── ReportesActivity.java         Filtros, resumen, compartir y sync API
 ├── DatabaseHelper.java           SQLiteOpenHelper (tabla residuos)
 ├── Residuo.java                  Modelo de datos
 ├── ResiduoAdapter.java           Adapter del RecyclerView
 ├── ApiService.java               Contrato Retrofit (API RESTful)
 └── RetrofitClient.java           Cliente Retrofit
```

La API RESTful (`RetrofitClient.URL_BASE`) apunta a un endpoint simulado;
para producción debe reemplazarse por la URL real del backend de ECOLIM.
