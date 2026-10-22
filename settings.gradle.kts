// Fabrica AFX: compilará las extensiones .afx contra la
// superficie API congelada de la futura app AnimeFox.
//
// AUTO-DIFICUBRIMIENTO de modulos: toda carpeta afx/<id>/ con su
// build.gradle.kts es una extension y se incluye sola. Crear una
// extension nueva = crear la carpeta (build.gradle.kts +
// manifest.json + src) y agregar su entrada a index.json: NI este
// archivo NI el workflow del CI se tocan. La tarea raiz
// construirTodas (build.gradle.kts) compila, firma y publica
// todas; el CI la llama sin listar modulos.
rootProject.name = "animefox-extensions"

include(":api")

File(rootDir, "afx").listFiles()
    ?.filter { it.isDirectory && File(it, "build.gradle.kts").isFile }
    ?.sortedBy { it.name }
    ?.forEach { dir ->
        val id = dir.name
        include(":afx-$id")
        project(":afx-$id").projectDir = dir
    }
