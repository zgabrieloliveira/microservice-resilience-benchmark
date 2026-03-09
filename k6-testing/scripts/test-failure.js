import http from 'k6/http';
import { check, group } from 'k6';

const DEBUG_URL = 'http://localhost:8888/api/v1/payment/debug';
const ORDERS_URL = 'http://localhost:8888/api/v1/orders';

export const options = {
    vus: 50,
    duration: '1m',
};

// Setup: Enable immediate 503 errors
export function setup() {
    const res = http.get(`${DEBUG_URL}/error/on`);
    if (res.status !== 200) throw new Error('Failed to enable error simulation');
    console.log('--- FAST FAILURE TEST: 503 Errors ENABLED ---');
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
}
