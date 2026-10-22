import org.gradle.api.GradleException
import java.util.Properties

// Fabrica AFX (AnimeFox). REGLA INVARIANTABLE: Kotlin de la
// fabrica == Kotlin de la APK AnimeFox, JAMAS mayor (cuando esta
// exista; heredamos 2.2.10 como referencia de la familia
// MangaFox/AnimeFox). Bytecode mas viejo que el runtime siempre
// es seguro; al reves, no (NoClassDefFoundError, ver historia de
// la fabrica MFX).
plugins { kotlin("jvm") version "2.2.10" apply false }

allprojects {
    repositories { mavenCentral() }
}

// ============================================================
// RESOLUCION DEL SDK ANDROID (para android.jar y d8).
// Orden de busqueda, sin adivinar:
//   1. -PandroidJar=/ruta/android.jar y -Pd8=/ruta/d8 (explicito)
//   2. Variable de entorno ANDROID_HOME
//   3. local.properties del propio repo (sdk.dir)
// Los modulos la heredan via rootProject.extra. Errores claros,
// nunca fallos mudos.
// ============================================================
val sdkAndroid: File = run {
    val explicita = providers.gradleProperty("sdkAndroid").orNull
    val porEnv = System.getenv("ANDROID_HOME")
    val porLocalProperties = File(rootDir, "local.properties").let { f ->
        if (f.isFile) {
            Properties().apply { f.inputStream().use { load(it) } }
                .getProperty("sdk.dir")
        } else null
    }
    File(explicita ?: porEnv ?: porLocalProperties ?: throw GradleException(
        "No se encontro el SDK de Android. Pasa -PsdkAndroid=/ruta/sdk, " +
            "define ANDROID_HOME, o crea local.properties con sdk.dir=..."
    ))
}.let { f ->
    if (!f.isDirectory) throw GradleException("El SDK no existe o es directorio: $f")
    f
}

val androidJarResuelto: File = providers.gradleProperty("androidJar").orNull?.let { File(it) } ?: run {
    val plataformas = File(sdkAndroid, "platforms").listFiles()
        ?.filter { it.isDirectory && it.name.startsWith("android-") }
        ?: emptyList()
    val masAlta = plataformas.maxByOrNull {
        it.name.removePrefix("android-").substringBefore('.').toIntOrNull() ?: 0
    } ?: throw GradleException(
        "No hay plataformas en ${File(sdkAndroid, "platforms")}. " +
            "Instala un platform desde el SDK manager."
    )
    val jar = File(masAlta, "android.jar")
    if (!jar.isFile) throw GradleException("Falta android.jar en $masAlta")
    jar
}

// Herencia para :api y los :afx-* (evaluado en la raiz ANTES de
// que cualquier modulo se configure: Gradle evalua la raiz
// primero).
allprojects {
    extra["sdkAndroid"] = sdkAndroid
    extra["androidJar"] = androidJarResuelto
}

// ============================================================
// TAREA RAIZ: construirTodas. Auto-descubre TODOS los modulos
// :afx-* que settings.gradle.kts incluye (toda carpeta
// afx/<id>/ con build.gradle.kts) y corre su construirAfx.
// El CI llama SOLO a esta tarea: agregar una extension nueva
// JAMAS obliga a editar el workflow.
// MODO ESPERA: hoy la fabrica no tiene extensiones (la APK
// AnimeFox todavia no existe); con cero modulos la tarea termina
// en verde con aviso claro en vez de fallar. En cuanto exista el
// primer afx/<id>/, compila, firma y publica automaticamente.
// ============================================================
val modulosAfx = subprojects.filter { it.name.startsWith("afx-") }

tasks.register("construirTodas") {
    group = "afx"
    description = "Compila, firma y auto-verifica TODAS las extensiones .afx del repo"
    doFirst {
        if (modulosAfx.isEmpty()) {
            logger.lifecycle(
                "construirTodas: fabrica en espera, sin extensiones afx todavia " +
                    "(cada extension vive en afx/<id>/ con build.gradle.kts + " +
                    "manifest.json; ver plantilla/ y CONTRATO)."
            )
            return@doFirst
        }
        logger.lifecycle(
            "construirTodas: " + modulosAfx.joinToString(", ") {
                it.name.removePrefix("afx-")
            }
        )
    }
    // Rutas de tarea (resolucion perezosa): la raiz se evalua
    // antes que los modulos, y los nombres se resuelven al
    // ejecutar.
    dependsOn(modulosAfx.map { "${it.path}:construirAfx" })
}
