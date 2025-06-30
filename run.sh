#!/bin/bash

# Configuración
PORT=${PORT:-4000}
ACTIVE_PROFILE=${ACTIVE_PROFILE:-dev}
JAR_PATH="app.jar" # Ruta dentro del contenedor

# Función para mostrar mensajes de error y salir
function error_exit {
    echo "[ERROR] $1" >&2
    exit 1
}

# Función para ejecutar la aplicación
function run_app {
    echo "[INFO] Iniciando aplicación Spring Boot..."
    echo "[DEBUG] Perfil activo: $ACTIVE_PROFILE"
    echo "[DEBUG] Puerto: $PORT"
    java -jar -Dserver.port=$PORT -Dspring.profiles.active=$ACTIVE_PROFILE $JAR_PATH || error_exit "Falló al iniciar la aplicación"
}

# Solo ejecutar (la compilación se hizo en la etapa builder)
run_app
