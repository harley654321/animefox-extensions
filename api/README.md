# api/ — superficie congelada de la APK AnimeFox (PENDIENTE)

Esta carpeta se llena SOLO cuando exista la APK AnimeFox: se
vendoriza (copia verbatim) su superficie publica de API, igual
que la fabrica MFX hizo con MangaFox: modelos del dominio anime,
parser, ClienteWeb, AnimeFoxLog y la interfaz `FuenteAfx` que
toda extension implementa.

Reglas (heredadas del ecosistema MangaFox):

- `api/` cambia SOLO con el CONTRATO, nunca unilateralmente.
- Las extensiones la consumen con `compileOnly`: la APK resuelve
  el resto en runtime (classloader padre), estilo Cloudstream.
- El Kotlin de esta fabrica siempre es el mismo que el de la APK,
  jamas mayor (NoClassDefFoundError: ver historia MFX).
- Cuando la APK congele su API: copiar aqui el jar fuente, crear
  `build.gradle.kts` (kotlin jvm, jvmToolchain 17) y la primera
  extension en `afx/<id>/` siguiendo `plantilla/`.

Mientras esto no ocurra, la fabrica esta EN ESPERA: construirTodas
termina en verde sin publicar nada.
