import http from 'k6/http';
import { check, group } from 'k6';

const DEBUG_URL = 'http://localhost:8888/api/v1/payment/debug';
const ORDERS_URL = 'http://localhost:8888/api/v1/orders';

export const options = {
    vus: 50,
    duration: '2m',
};

// Setup: Start the test with the system in a failure state
export function setup() {
    const res = http.get(`${DEBUG_URL}/latency/on`);
    if (res.status !== 200) throw new Error('Failed to enable latency');
    console.log('--- SELF-HEALING TEST: System starting in FAILURE state ---');
    console.log('!!! ACTION REQUIRED: Run "turn-off" cURL at 01:00 mark !!!');
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
}
