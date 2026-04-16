import http from 'k6/http';
import { check, group, fail } from 'k6';

const ORCHESTRATOR_SETUP_URL = 'http://localhost:8888/api/v1/setup/resilience';
const DEBUG_URL = 'http://localhost:8888/api/v1/payment/debug';
const ORDERS_URL = 'http://localhost:8888/api/v1/orders';

export const options = {
    vus: 50,
    duration: '1m',
};

export function setup() {
    // 1. Force Hybrid Architecture (CB + Retry)
    const archRes = http.post(`${ORCHESTRATOR_SETUP_URL}?retry=true&cb=true`);
    if (archRes.status !== 200) fail(`Architecture setup failed. Status: ${archRes.status}`);

    // 2. Enable immediate 503 errors
    const faultRes = http.get(`${DEBUG_URL}/error/on`);
    if (faultRes.status !== 200) fail('Failed to enable error simulation');

    console.log('--- FAST FAILURE TEST: 503 Errors ENABLED | Hybrid Architecture ---');
}

export default function () {
    group('Error Scenario', function () {
        const res = http.post(ORDERS_URL);
        check(res, {
            'is status 200': (r) => r.status === 200,
        });
    });
}

export function teardown() {
    http.get(`${DEBUG_URL}/turn-off`);
    console.log('--- CLEANUP: Simulations DISABLED ---');
}