// src/main/resources/static/js/app.js

import { Api } from './api.js';
import { UI } from './ui.js';

// Ezeket elérhetővé tesszük a HTML számára (mert a gombok onclick-jei ezeket keresik)
window.switchTab = (tabId, element) => UI.switchTab(tabId, element);
window.filterTable = (inputId, tableId) => UI.filterTable(inputId, tableId);

window.addNewHandover = async () => {
    const authorInput = document.getElementById('handoverAuthor');
    const messageInput = document.getElementById('handoverMessage');
    if (!authorInput.value || !messageInput.value) { alert("Kérlek, töltsd ki a neved és az üzenetet is!"); return; }
    
    try {
        const res = await Api.saveHandover(authorInput.value.trim(), messageInput.value.trim());
        if (res.ok) { messageInput.value = ''; loadHandovers(); }
    } catch (e) { console.error(e); }
};

window.addNewDevice = async () => {
    const nameInput = document.getElementById('newName');
    const ipInput = document.getElementById('newIp');
    if (!nameInput.value || !ipInput.value) { alert("Töltsd ki az adatokat!"); return; }
    
    try {
        const res = await Api.saveDevice(nameInput.value.trim(), ipInput.value.trim());
        if (res.ok) { nameInput.value = ''; ipInput.value = ''; loadDevices(); }
    } catch (e) { console.error(e); }
};

window.forcePing = async (id, btn) => {
    btn.disabled = true; btn.innerText = "Ping...";
    try {
        await Api.pingDevice(id);
        await loadDevices();
    } catch (e) { console.error(e); } finally {
        btn.disabled = false; btn.innerText = "Kézi Ping";
    }
};

window.checkOnCall = async () => {
    const dateInput = document.getElementById('onCallDate').value;
    if (!dateInput) return;
    try {
        const data = await Api.getOnCall(dateInput);
        UI.renderOnCall(data, dateInput);
    } catch (e) {
        UI.renderOnCallError(dateInput);
    }
};

// Adatok betöltése a szerverről a felületre
async function loadHandovers() {
    try { const data = await Api.getHandovers(); UI.renderHandovers(data); } 
    catch (e) { console.error(e); }
}

async function loadDevices() {
    try { const data = await Api.getDevices(); UI.renderDevices(data); } 
    catch (e) { console.error(e); }
}

async function loadPowerSupplies() {
    try { const data = await Api.getPowerSupplies(); UI.renderPowerSupplies(data); } 
    catch (e) { console.error(e); }
}

// Amikor az oldal betöltött, elindítjuk a folyamatokat
document.addEventListener('DOMContentLoaded', () => {
    loadHandovers();
    loadDevices();
    loadPowerSupplies();
    setInterval(loadDevices, 10000); // 10 másodpercenként IP ellenőrzés
});