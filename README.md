# Secure Client-Server System for Logistics Simulation (Dakar Rally-Inspired)

Sistema cliente-servidor para simulación de rutas geolocalizadas, desarrollado como ejercicio de diseño de arquitectura segura aplicada a un dominio logístico. El caso de uso — seguimiento de posición y rutas de vehículos en condiciones de campo abierto — está inspirado en el Rally Dakar, pero el problema de fondo es genérico a cualquier sistema de tracking logístico: cómo exponer datos de localización a clientes sin comprometer la integridad ni la confidencialidad del sistema.

Proyecto concebido originalmente como trabajo colaborativo; desarrollado íntegramente de forma individual, incluyendo arquitectura, lógica de backend, modelado de datos y estrategia de seguridad.

## Motivación

Los sistemas de tracking logístico (flotas, rally raids, cadenas de suministro con GPS) comparten un mismo perfil de riesgo: múltiples clientes consultando datos de posición en tiempo real contra un backend que no siempre puede permitirse controles de acceso enterprise-grade. Este proyecto explora ese problema a escala reducida, priorizando el diseño del modelo de seguridad por encima de la complejidad de la simulación geoespacial en sí.

## Arquitectura

**Cliente**
Simula la interacción de un usuario/vehículo consultando datos de ruta y posición. Genera las solicitudes que el servidor debe validar y resolver.

**Servidor**
Punto central de lógica de negocio. Responsabilidades:
- Validación de solicitudes entrantes
- Control de acceso
- Enrutamiento de queries hacia la capa correcta de datos (real o señuelo)

**Base de datos**
Almacena datos reales de ruta/posición junto con tablas sintéticas (decoy) estructuralmente idénticas a las reales.

## Modelo de seguridad (3 capas)

**1. Control de acceso**
Validación básica de la identidad/legitimidad de cada solicitud antes de que llegue a la capa de datos.

**2. Gestión de queries**
Las consultas no tienen acceso directo ni arbitrario a la base de datos. El servidor media cada interacción, limitando el alcance de lo que un cliente puede solicitar.

**3. Datos señuelo (decoy tables) — componente central del proyecto**
Tablas sintéticas que replican la estructura de las tablas reales pero no contienen datos operativos válidos. Su función:
- Actuar como honeypot ante patrones de acceso no autorizados o anómalos
- Desviar intentos de exploración/exfiltración lejos de los datos reales
- Servir como señal temprana de actividad sospechosa, sin depender de un IDS externo

La idea de fondo: usar datos sintéticos no como insumo de entrenamiento de modelos (su uso más común), sino como mecanismo activo de defensa a nivel de sistema.

## Stack técnico

- **Python** — lógica de cliente, servidor y generación de datos
- **Comunicación cliente-servidor** — sockets / API (especificar según implementación final)
- **Base de datos** — SQLite / PostgreSQL (especificar)
- Modelado y simulación de datos de geolocalización

## Decisiones de diseño y trade-offs

Bajo restricciones de tiempo y desarrollo individual, se priorizó:

| Decisión | Justificación |
|---|---|
| Entrega funcional sobre escalabilidad | El objetivo era validar el modelo de seguridad, no soportar carga productiva |
| Interacción cliente-servidor simplificada | Evitar sobre-ingeniería en una capa que no era el foco del proyecto |
| Seguridad conceptual sobre infraestructura enterprise | Demostrar el razonamiento de diseño (honeypots, segmentación de acceso) sin depender de herramientas de terceros |

Esto no es un sistema production-ready — es una demostración de razonamiento de arquitectura bajo restricciones reales, con ownership completo de cada capa (arquitectura, backend, modelado de datos, estrategia de seguridad).

## Retos técnicos principales

- Mantener consistencia en el flujo de comunicación cliente-servidor sin una capa de red robusta
- Diseñar un modelo de simulación geoespacial coherente con datos de rally reales
- Estructurar la base de datos para sostener dos capas paralelas (real y señuelo) sin que la distinción sea trivialmente detectable
- Garantizar separación efectiva entre la capa segura y la capa señuelo a nivel de lógica de acceso

## Mejoras futuras

- Detección de anomalías vía modelos de ML sobre patrones de acceso
- Generación dinámica de datos sintéticos con IA generativa (en vez de tablas señuelo estáticas)
- Visualización de rutas en tiempo real
- Despliegue en entorno cloud
- Sistema de detección de intrusiones más avanzado (IDS real vs. honeypot simple)

## Aprendizajes

Diseñar bajo restricciones obliga a decidir qué simplificar sin comprometer el objetivo central del sistema. Este proyecto reforzó particularmente el valor de introducir seguridad desde las etapas tempranas de diseño (no como capa añadida al final), y abrió una perspectiva sobre datos sintéticos como herramienta de seguridad de sistemas — no solo como insumo de entrenamiento de modelos, que es su aplicación más discutida en el contexto de IA.
ration of engineering thinking, system design, and creative problem-solving under real constraints.
