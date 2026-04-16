import http from 'k6/http';
import { check, group, fail } from 'k6';

const ORCHESTRATOR_SETUP_URL = 'http://localhost:8888/api/v1/setup/resilience';
const DEBUG_URL = 'http://localhost:8888/api/v1/payment/debug';
const ORDERS_URL = 'http://localhost:8888/api/v1/orders';

export const options = {
    stages: [
        { duration: '30s', target: 50 },  // Warm-up
        { duration: '1m', target: 300 },  // Stress: Exceeding the 200-thread Tomcat limit
        { duration: '30s', target: 0 },   // Cool-down
    ],
};

export function setup() {
    // 1. Read environment variables (default to false)
    const retry = __ENV.RETRY === 'true';
    const cb = __ENV.CB === 'true';

    // 2. Configure Orchestrator Architecture
    const archRes = http.post(`${ORCHESTRATOR_SETUP_URL}?retry=${retry}&cb=${cb}`);
    if (archRes.status !== 200) fail(`Architecture setup failed. Status: ${archRes.status}`);
    console.log(`--- ARCHITECTURE SETUP: Retry=${retry} | CircuitBreaker=${cb} ---`);

    // 3. Enable 3s Latency Fault
    const faultRes = http.get(`${DEBUG_URL}/latency/on`);
    if (faultRes.status !== 200) fail('Failed to enable latency simulation');
    console.log('--- RUPTURE TEST: 3s Latency ENABLED ---');
}

export default function () {
    group('Rupture Scenario', function () {
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