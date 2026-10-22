# Plantilla de extension .afx

Referencia para crear `afx/<id>/` cuando la APK AnimeFox congele
su superficie API (la plantilla hereda el pipeline probado de la
fabrica MFX; el formato de FuenteAfx se define en el CONTRATO v1):

1. Copiar `build.gradle.kts` aqui a `afx/<id>/` y ajustar el id en
   el nombre de tarea (`construirAfx` ya es generico: solo cambia
   la description del modulo).
2. Escribir `manifest.json` con el id real (entero version, +1 por
   cambio) y el pluginClassName del paquete
   `com.harley.animefox.afx.<id>.Plugin<Id>`.
3. Crear `src/main/kotlin/` con la implementacion de FuenteAfx
   (dominio anime: episodios + resolucion de video, ver CONTRATO).
4. Entrada en index.json con urlAfx/fileHashAfx/firmaAfx vacios.
5. Push a main: el CI compila, firma, auto-verifica y publica.
