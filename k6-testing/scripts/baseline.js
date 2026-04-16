import http from 'k6/http';
import { check, sleep, group, fail } from 'k6';

const ORCHESTRATOR_SETUP_URL = 'http://localhost:8888/api/v1/setup/resilience';
const PAYMENT_DEBUG_URL = 'http://localhost:8888/api/v1/payment/debug';
const BASE_URL = 'http://localhost:8888/api/v1/orders';

export const options = {
    vus: 5,
    duration: '10s',
    thresholds: {
        'checks{test_type:happy_path}': ['rate>0.99'],
        'http_req_duration{test_type:happy_path}': ['p(95)<200'],
    },
};

export function setup() {
    // 1. Force Unprotected Architecture (Baseline constraint)
    const archRes = http.post(`${ORCHESTRATOR_SETUP_URL}?retry=false&cb=false`);
    if (archRes.status !== 200) fail(`Architecture setup failed. Status: ${archRes.status}`);

    // 2. Ensure all faults are OFF
    const res = http.get(`${PAYMENT_DEBUG_URL}/turn-off`);
    if (res.status !== 200) fail('Failed to disable faults. Aborting test.');

    console.log('--- BASELINE SETUP: Architecture UNPROTECTED | Faults OFF ---');
}

export default function () {
    group('Happy Path Request', function () {
        const res = http.post(BASE_URL);
        check(res, {
            'status is 200 (OK)': (r) => r.status === 200,
        }, { test_type: 'happy_path' });
    });
    sleep(1);
}