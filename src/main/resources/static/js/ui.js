// src/main/resources/static/js/ui.js

export class UI {
    static switchTab(tabId, element) {
        document.querySelectorAll('.content-section').forEach(sec => sec.classList.remove('active'));
        document.querySelectorAll('.sidebar-item').forEach(item => item.classList.remove('active'));
        document.getElementById('section-' + tabId).classList.add('active');
        if (element) element.classList.add('active');
    }

    static filterTable(inputId, tableId) {
        const filter = document.getElementById(inputId).value.toLowerCase();
        const tr = document.getElementById(tableId).getElementsByTagName("tr");
        for (let i = 1; i < tr.length; i++) {
            let rowText = tr[i].textContent || tr[i].innerText;
            tr[i].style.display = rowText.toLowerCase().indexOf(filter) > -1 ? "" : "none";
        }
    }

    static renderHandovers(data) {
        const listDiv = document.getElementById('handoverList');
        listDiv.innerHTML = ''; 
        if (data.length === 0) {
            listDiv.innerHTML = '<i>Még nincs bejegyzés a naplóban.</i>';
            return;
        }
        data.forEach(item => {
            const dateStr = new Date(item.timestamp).toLocaleString('hu-HU');
            const card = document.createElement('div');
            card.className = 'handover-card';
            card.innerHTML = `
                <div class="handover-header">
                    <span class="handover-author">👤 ${item.author}</span>
                    <span>🕒 ${dateStr}</span>
                </div>
                <div class="handover-text">${item.message}</div>
            `;
            listDiv.appendChild(card);
        });
    }

    static renderDevices(devices) {
        const tableBody = document.getElementById('deviceTableBody');
        tableBody.innerHTML = ''; 
        devices.forEach(device => {
            const row = document.createElement('tr');
            const statusClass = device.alive ? 'status-online' : 'status-offline';
            const statusText = device.alive ? 'Online' : 'Offline';
            const lastChecked = device.lastChecked ? new Date(device.lastChecked).toLocaleTimeString('hu-HU') : 'Folyamatban...';

            row.innerHTML = `
                <td>${device.name}</td>
                <td>${device.ipAddress}</td>
                <td class="${statusClass}">${statusText}</td>
                <td>${lastChecked}</td>
                <td><button class="ping-btn" onclick="forcePing(${device.id}, this)">Kézi Ping</button></td>
            `;
            tableBody.appendChild(row);
        });
        UI.filterTable('deviceSearchInput', 'deviceTable');
    }

    static renderOnCall(data, dateInput) {
        const resultDiv = document.getElementById('onCallResult');
        let html = `<h3 style="color: #107c10; margin-bottom: 15px;">✅ Dátum: ${dateInput}</h3>`;
        data.forEach(oc => {
            html += `
            <div style="background: #ffffff; padding: 15px; border: 1px solid #ddd; border-left: 5px solid #0078d7; margin-bottom: 12px; border-radius: 5px; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
                <h4 style="margin-bottom: 8px; color: #333; border-bottom: 1px solid #eee; padding-bottom: 5px;">🏢 Részleg: ${oc.department}</h4>
                <strong>Ügyeletes(ek):</strong> <span style="font-size:1.1em; color: #222;">${oc.names}</span><br><br>
                <strong>Elérhetőség:</strong> ${oc.phones}
            </div>`;
        });
        resultDiv.innerHTML = html;
    }

    static renderOnCallError(dateInput) {
        document.getElementById('onCallResult').innerHTML = `<h3 style="color: #d13438;">❌ Erre a napra (${dateInput}) nincs rögzítve készenlétes.</h3>`;
    }

    static renderPowerSupplies(data) {
        const tableBody = document.getElementById('psTableBody');
        tableBody.innerHTML = ''; 
        data.forEach(ps => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td><strong>${ps.location}</strong></td>
                <td style="color: #555;">${ps.exactLocation}</td>
                <td>${ps.measurementDate}</td>
                <td>${ps.measuredBy}</td>
                <td>${ps.chargerType}</td>
                <td>${ps.batteryType}</td>
                <td><b>${ps.groupVoltage}</b></td>
                <td>${ps.capacity}</td>
                <td>${ps.installedDate}</td> <td>${ps.temperature}</td>
            `;
            tableBody.appendChild(row);
        });
    }
}