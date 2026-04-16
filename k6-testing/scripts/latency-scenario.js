import http from 'k6/http';
import { check, sleep, group, fail } from 'k6';

const ORCHESTRATOR_SETUP_URL = 'http://localhost:8888/api/v1/setup/resilience';
const PAYMENT_DEBUG_URL = 'http://localhost:8888/api/v1/payment/debug';
const BASE_URL = 'http://localhost:8888/api/v1/orders';

export const options = {
    vus: 50,
    duration: '30s',
};

export function setup() {
    // 1. Read environment variables (default to false if not provided)
    const retry = __ENV.RETRY === 'true';
    const cb = __ENV.CB === 'true';

    // 2. Configure Orchestrator Architecture
    const archRes = http.post(`${ORCHESTRATOR_SETUP_URL}?retry=${retry}&cb=${cb}`);
    if (archRes.status !== 200) fail(`Architecture setup failed. Status: ${archRes.status}`);
    console.log(`--- ARCHITECTURE SETUP: Retry=${retry} | CircuitBreaker=${cb} ---`);

    // 3. Enable Latency Fault
    const res = http.get(`${PAYMENT_DEBUG_URL}/latency/on`);
    if (res.status !== 200) fail('Failed to enable latency. Aborting test.');

    console.log('--- LATENCY SCENARIO: 3s Latency ENABLED ---');
}

export default function () {
    group('Latency Scenario Request', function () {
        const res = http.post(BASE_URL);
        check(res, {
            'status is 200 (OK)': (r) => r.status === 200,
        });
    });
    sleep(1);
}

export function teardown() {
    http.get(`${PAYMENT_DEBUG_URL}/turn-off`);
    console.log('--- CLEANUP: Faults DISABLED ---');
}