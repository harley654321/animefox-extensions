# CONTRATO DE EXTENSIONES ANIMEFOX (borrador v0)

> **ESTADO: BORRADOR.** Este contrato se congela (v1) cuando la APK AnimeFox
> exista y su superficie API se vendorice en `api/`. Hasta entonces rige como
> referencia: los nombres definitivos (FuenteAfx, ClavesAnimeFox, VerificadorAfx,
> campos del index) quedan fijados en ese momento. La estructura de fábrica,
> seguridad y automatización ya están vigentes y probadas (herencia de MFX).

## Reglas de oro (vigentes ya)

1. Cada extensión es un plugin `.afx` (ZIP: `manifest.json` + `classes.dex`)
   **firmado ECDSA P-256**. La APK verifica firma + SHA-256 antes de activarlo.
   Un .afx sin firma válida es rechazado.
2. La llave privada de firma vive SOLO en el secret `ANIMEFOX_LLAVE_PRIVADA`
   del repo; la pública en `firmas/llave-publica.pem`, pineada por la APK en
   `ClavesAnimeFox`. **Cadena de confianza independiente de MangaFox**: la llave
   MFX jamás firma .afx ni aparece en este repo, y viceversa.
3. El Kotlin de la fábrica siempre es el mismo que el de la APK AnimeFox,
   jamás mayor (`NoClassDefFoundError`: ver historia de la fábrica MFX).
4. `api/` es la superficie congelada: cambia solo con el CONTRATO, nunca
   unilateralmente. Las extensiones la consumen con `compileOnly`.
5. `apiVersion` congelada: los cambios de contrato suben el número.
6. Leyes de la familia: registro total en logcat, máximo 500 líneas por
   archivo, errores explícitos (nunca fallos mudos), commits pequeños
   `tipo(ámbito): descripción`, main jamás rota.

## Dominio anime (propuesto, a congelar)

La extensión del lector (`FuenteMfx`: manga → capítulos → páginas) se
espeja como `FuenteAfx`: anime → episodios → fuentes de video. Puntos que
el v1 del contrato deberá fijar: búsqueda, populares, detalles del anime,
listado de episodios, resolución del video reproducible (multi-servidor),
subtítulos, rateLimit por servidor y recuperación. La APK pone el reproductor;
la extensión solo entrega URLs de video resolubles.

## index.json: campos por extensión (propuesto)

`id`, `name`, `lang`, `version`, `apiVersion`, `status`, `description`,
`manifest`, `icon`, `baseUrl`, `rateLimit`, `recoveryUrl` y los que llena el
CI al publicar: `urlAfx`, `fileHashAfx` (SHA-256), `firmaAfx` (ECDSA B64),
`versionAfx`.

## manifest.json (vigente, mismo formato MFX)

`id`, `name`, `version` (entero, +1 por cambio), `apiVersion`, `pluginClassName`.

## Checklist para una extensión nueva

1. Carpeta `afx/<id>/` con `build.gradle.kts` (de `plantilla/`), `manifest.json`
   y `src/` que implemente `FuenteAfx`.
2. Entrada en `index.json` con `urlAfx`/`fileHashAfx`/`firmaAfx` vacíos.
3. Push a main: el CI compila, firma, auto-verifica contra la llave pública y
   publica (index en main + `.afx` en la rama builds). Cero edición de CI.
4. Nunca commitear la llave privada (`*.pem` está bloqueado por `.gitignore`).

## PENDIENTE (antes de congelar v1)

- [ ] APK AnimeFox: congelar superficie API y vendorizar en `api/`.
- [ ] Fijar `minSdk` (afecta `--min-api` de d8 en la plantilla).
- [ ] Fijar formato de resolución de video y contratos de subtítulos.
- [ ] Primera extensión piloto de referencia.
