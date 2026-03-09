import http from 'k6/http';
import { check, sleep, group, fail } from 'k6';

// 8888 hospeda o Gateway, que acessa os serviços especialistas
const PAYMENT_DEBUG_URL = 'http://localhost:8888/api/v1/payment/debug';
const BASE_URL = 'http://localhost:8888/api/v1/orders';

export const options = {
    // 50 usuários virtuais: carga mais pesada para o experimento
    vus: 50,
    duration: '30s',
};

// 1. ANTES do teste, LIGA a simulação de latência
export function setup() {
    const res = http.get(`${PAYMENT_DEBUG_URL}/latency/on`);
    if (res.status !== 200) {
        fail('Não foi possível LIGAR a simulação de latência. Abortando teste.');
    }
    console.log('Ambiente preparado: Simulação de LATÊNCIA LIGADA.');
}

// 2. O TESTE
export default function () {
    group('Latency Scenario Request', function () {
        const res = http.post(BASE_URL);

        // Este check vai falhar (propositalmente) no seu teste
        check(res, {
            'status é 200 (OK)': (r) => r.status === 200,
        });
    });
    sleep(1);
}

// 3. DEPOIS do teste, desliga a falha
export function teardown() {
    http.get(`${PAYMENT_DEBUG_URL}/turn-off`);
    console.log('Limpeza concluída: Falhas DESLIGADAS.');
}