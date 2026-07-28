// src/main/resources/static/js/api.js

export class Api {
    static async getHandovers() {
        const res = await fetch('/api/handover'); return res.json();
    }
    
    static async saveHandover(author, message) {
        return fetch('/api/handover', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ author, message })
        });
    }

    static async getDevices() {
        const res = await fetch('/api/devices'); return res.json();
    }

    static async saveDevice(name, ipAddress) {
        return fetch('/api/devices', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, ipAddress })
        });
    }

    static async pingDevice(id) {
        return fetch(`/api/devices/${id}/ping`, { method: 'POST' });
    }

    static async getOnCall(date) {
        const res = await fetch(`/api/oncall/${date}`);
        if (!res.ok) throw new Error("Nincs adat");
        return res.json();
    }

    static async getPowerSupplies() {
        const res = await fetch('/api/powersupply'); return res.json();
    }
}