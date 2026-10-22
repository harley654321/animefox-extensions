# Repo de extensiones AnimeFox (sistema .afx)

Fábrica hermana de [mangafox-extensions](https://github.com/harley654321/mangafox-extensions)
para la futura app **AnimeFox** (visor de animes). Mismo sistema probado de la
familia: kernel estable + plugins firmados, pero ecosistema **100% separado**:
llave de firma propia, API propia, index propio. Las APK nunca mezclan
extensiones: MangaFox lee el index de su repo, AnimeFox leerá el de este.

## Estado actual: FÁBRICA EN ESPERA

El pipeline ya está instalado, blindado y probado, pero **sin extensiones**
hasta que la APK AnimeFox exista y congele su superficie API (ver `api/README.md`
y `CONTRATO-ANIMEFOX-EXTENSIONES.md`). Con cero extensiones, el CI termina en
verde y no publica nada; la primera `afx/<id>/` despierta la fábrica sola.

## Estructura

```
index.json                        ← catálogo: una entrada por extensión .afx
afx/<id>/                         ← código fuente del plugin (cuando exista)
plantilla/                        ← build.gradle.kts + manifest de referencia
api/                              ← superficie API pública (PENDIENTE: se
                                     vendoriza de la APK AnimeFox)
firmas/llave-publica.pem          ← llave pública ECDSA P-256 (la APK la
                                     pineará en ClavesAnimeFox)
CONTRATO-ANIMEFOX-EXTENSIONES.md  ← contrato (borrador v0)
builds (rama)                     ← artefactos .afx publicados
```

## Seguridad (igual que la fábrica MFX, cadena de confianza propia)

- La llave privada vive SOLO como secret `ANIMEFOX_LLAVE_PRIVADA` de este repo
  (GitHub jamás muestra valores de secrets: son solo-escritura).
- Cada `.afx` se firma ECDSA P-256 y el CI **auto-verifica** contra
  `firmas/llave-publica.pem` antes de publicar: si no verifica, no se publica.
- Actions pineadas por SHA oficial, permisos mínimos (`contents: write`),
  timeout de 20 min, `*.pem` bloqueado por `.gitignore` (solo la pública).
- Respaldo cifrado bajo demanda: workflow `respaldo-llave` (artefacto AES-256
  que caduca en 1 día; el password viaja en secret temporal que se borra).

## Agregar la primera (o la n-ésima) extensión

1. Crear `afx/<id>/`: `build.gradle.kts` (copiar de `plantilla/`, ajustar el
   `id` en el nombre de tarea y description), `manifest.json` (`id`, `version`,
   `apiVersion`, `pluginClassName`) y `src/` (FuenteAfx + Plugin).
2. Entrada en `index.json` con `"urlAfx": ""`, `"fileHashAfx": ""`,
   `"firmaAfx": ""`: el CI los rellena al publicar.
3. Push a main. El CI la descubre, compila, firma, auto-verifica y publica en
   la rama `builds`: settings y workflow NO se tocan.

Compilado local (cuando la API exista):

```
./gradlew construirTodas -PsdkAndroid=$ANDROID_HOME -PllaveAfx=/ruta/llave-pkcs8.pem
```
