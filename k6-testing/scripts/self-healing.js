import http from 'k6/http';
import { check, group, fail } from 'k6';

const ORCHESTRATOR_SETUP_URL = 'http://localhost:8888/api/v1/setup/resilience';
const DEBUG_URL = 'http://localhost:8888/api/v1/payment/debug';
const ORDERS_URL = 'http://localhost:8888/api/v1/orders';

export const options = {
    vus: 50,
    duration: '2m',
};

export function setup() {
    // 1. Force Hybrid Architecture (CB + Retry)
    const archRes = http.post(`${ORCHESTRATOR_SETUP_URL}?retry=true&cb=true`);
    if (archRes.status !== 200) fail(`Architecture setup failed. Status: ${archRes.status}`);

    // 2. Start the test with the system in a failure state
    const res = http.get(`${DEBUG_URL}/latency/on`);
    if (res.status !== 200) fail('Failed to enable latency');

    console.log('--- SELF-HEALING TEST: System starting in FAILURE state | Hybrid Arch ---');
    console.log('!!! ACTION REQUIRED: Run "turn-off" cURL at the 01:00 mark !!!');
}

export default function () {
    group('Recovery Scenario', function () {
        const res = http.post(ORDERS_URL);
        check(res, {
            'is status 200': (r) => r.status === 200,
        });
    });
}

export function teardown() {
    http.get(`${DEBUG_URL}/turn-off`);
    console.log('--- CLEANUP: Faults DISABLED ---');
}