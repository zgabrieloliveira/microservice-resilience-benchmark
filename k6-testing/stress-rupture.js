import http from 'k6/http';
import { check, group } from 'k6';

// Infrastructure and Service endpoints
const DEBUG_URL = 'http://localhost:8888/api/v1/payment/debug';
const ORDERS_URL = 'http://localhost:8888/api/v1/orders';

export const options = {
    stages: [
        { duration: '30s', target: 50 },  // Warm-up
        { duration: '1m', target: 300 },  // Stress: Exceeding the 200-thread limit 
        { duration: '30s', target: 0 },   // Cool-down
    ],
};

// Setup: Enable 3s latency before starting the load
export function setup() {
    const res = http.get(`${DEBUG_URL}/latency/on`);
    if (res.status !== 200) throw new Error('Failed to enable latency simulation');
    console.log('--- RUPTURE TEST: 3s Latency ENABLED ---');
}

export default function () {
    group('Rupture Scenario', function () {
        const res = http.post(ORDERS_URL);
        check(res, {
            'is status 200': (r) => r.status === 200,
        });
    });
    // No sleep: Maximizing request pressure to saturate the thread pool [cite: 212]
}

export function teardown() {
    http.get(`${DEBUG_URL}/turn-off`);
    console.log('--- CLEANUP: Simulations DISABLED ---');
}
