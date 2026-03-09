import http from 'k6/http';
import { check, sleep, group, fail } from 'k6';

// 8888 hospeda o Gateway, que acessa os serviços especialistas
const PAYMENT_DEBUG_URL = 'http://localhost:8888/api/v1/payment/debug';
const BASE_URL = 'http://localhost:8888/api/v1/orders';

export const options = {
    // 5 usuários virtuais: carga leve, apenas para validar
    vus: 5,
    duration: '10s',
    thresholds: {
        // Define o critério de sucesso: 99% das reqs devem ser 200 OK
        'checks{test_type:happy_path}': ['rate>0.99'],
        // Latência p(95) deve ser menor que 200ms
        'http_req_duration{test_type:happy_path}': ['p(95)<200'],
    },
};

// 1. ANTES do teste, garante que as falhas estão desligadas
export function setup() {
    const res = http.get(`${PAYMENT_DEBUG_URL}/turn-off`);
    if (res.status !== 200) {
        fail('Não foi possível DESLIGAR a simulação de falha. Abortando teste.');
    }
    console.log('Ambiente preparado: Falhas DESLIGADAS.');
}

// 2. O TESTE (o que cada usuário virtual faz)
export default function () {
    group('Happy Path Request', function () {
        const res = http.post(BASE_URL);

        check(res, {
            'status é 200 (OK)': (r) => r.status === 200,
        }, { test_type: 'happy_path' }); // Tag para os thresholds
    });
    sleep(1);
}