#!/bin/bash

# Define os caminhos relativos à raiz
SCRIPT_DIR="k6-testing/scripts"
RESULTS_DIR="k6-testing/results"
STRESS_SCRIPT="$SCRIPT_DIR/stress-rupture.js"

# Cria a pasta de resultados se não existir
mkdir -p "$RESULTS_DIR"

# Array com as configurações da Matriz de Execução (Cenário 3)
configs=(
    "03_stress_unprotected false false"
    "03_stress_only_retry true false"
    "03_stress_only_cb false true"
    "03_stress_hybrid true true"
)

echo "### INICIANDO BATERIA DE TESTES: RESILIENCE BENCHMARK ###"

for config in "${configs[@]}"; do
    read -r filename retry_val cb_val <<< "$config"
    
    echo "----------------------------------------------------------"
    echo "Configurando: $filename (Retry=$retry_val, CB=$cb_val)"
    
    # 1. Reinicia os containers (executado da raiz onde está o docker-compose.yml)
    echo "Reiniciando serviços para reset de memória e estado do Circuit Breaker..."
    docker compose restart ms-order-orchestrator ms-payment > /dev/null
    
    # 2. Tempo técnico para estabilização do Eureka e arrefecimento do sistema
    echo "Aguardando 60 segundos de intervalo técnico (Cool down)..."
    sleep 60
    
    # 3. Execução do k6 apontando para o subdiretório
    echo "Disparando k6 para o cenário $filename..."
    k6 run -e RETRY=$retry_val -e CB=$cb_val "$STRESS_SCRIPT" > "$RESULTS_DIR/$filename.txt"
    
    echo "Concluído: $filename salvo em $RESULTS_DIR/"
done

echo "----------------------------------------------------------"
echo "### SUCESSO: Todos os dados da matriz foram coletados. ###"
