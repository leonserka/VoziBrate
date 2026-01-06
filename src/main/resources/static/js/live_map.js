
var map = L.map('map').setView([43.515, 16.45], 13);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
}).addTo(map);


const MOVE_THRESHOLD_METERS = 25;
const MOVING_HOLD_MS = 60000;

var markers = {};

function calculateBearing(from, to) {
    const lat1 = from.lat * Math.PI / 180;
    const lat2 = to.lat * Math.PI / 180;
    const dLng = (to.lng - from.lng) * Math.PI / 180;

    const y = Math.sin(dLng) * Math.cos(lat2);
    const x = Math.cos(lat1) * Math.sin(lat2) -
        Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng);

    let brng = Math.atan2(y, x) * 180 / Math.PI;
    return (brng + 360) % 360;
}

function setMarkerHeading(marker, bearingDeg) {
    const el = marker.getElement();
    if (!el) return;
    const arrow = el.querySelector('.bus-arrow');
    if (!arrow) return;
    arrow.style.transform = `rotate(${bearingDeg}deg)`;
}

function buildPopupContent(lineNum, lineName, gbr, reg) {
    return `
        <div class="popup-center">
            <h3 class="popup-title">Linija ${lineNum}</h3>
            <small>${lineName}</small><br>
            <hr class="popup-hr">
            <b>Garažni br:</b> ${gbr}<br>
            <b>Tablica:</b> ${reg}<br>
            <button class="popup-btn" onclick="showRoute('${lineNum}')">🗺️ Prikaži rutu</button>
        </div>
    `;
}

window.showRoute = function (lineNum) {
    alert("Prikaz rute za liniju " + lineNum + " stiže uskoro!");
};

function updateBuses() {
    fetch('/api/positions/current')
        .then(res => res.json())
        .then(data => {
            const now = Date.now();

            data.forEach(pos => {
                if (!pos || !pos.bus || !pos.bus.busNumber) return;

                const id = pos.bus.busNumber;
                const reg = pos.bus.registration;

                let displayText = id;
                let lineName = "Nepoznata";

                if (pos.routeShortName && pos.routeShortName.trim() !== "") {
                    displayText = pos.routeShortName;
                    lineName = "Prometko";
                } else if (pos.bus.line && pos.bus.line.lineNumber) {
                    displayText = pos.bus.line.lineNumber;
                    lineName = pos.bus.line.name;
                }

                const newLatLng = L.latLng(pos.gpsLat, pos.gpsLng);

                if (markers[id]) {
                    const marker = markers[id];
                    const oldLatLng = marker.getLatLng();
                    const distance = oldLatLng.distanceTo(newLatLng);

                    marker.setLatLng(newLatLng);

                    if (!marker.lastMoveTime) marker.lastMoveTime = now;
                    if (marker._isMoving === undefined) marker._isMoving = true;

                    if (distance > MOVE_THRESHOLD_METERS) {
                        marker.lastMoveTime = now;
                        marker._heading = calculateBearing(oldLatLng, newLatLng);
                        setMarkerHeading(marker, marker._heading);
                    }

                    const isActive = (now - marker.lastMoveTime) < MOVING_HOLD_MS;

                    if (marker._isMoving !== isActive) {
                        marker._isMoving = isActive;
                        updateMarkerStyle(marker, displayText, isActive);

                        if (marker._heading !== undefined) {
                            setMarkerHeading(marker, marker._heading);
                        }
                    } else {
                        updateMarkerText(marker, displayText);
                    }

                    marker.setPopupContent(buildPopupContent(displayText, lineName, id, reg));

                } else {
                    const icon = L.divIcon({
                        className: 'custom-div-icon',
                        html: `<div class="bus-marker moving">${displayText}<div class="bus-arrow"></div></div>`,
                        iconSize: [32, 32],
                        iconAnchor: [16, 16]
                    });

                    const marker = L.marker(newLatLng, { icon }).addTo(map);
                    marker.lastMoveTime = now;
                    marker._isMoving = true;
                    marker._heading = 0;

                    marker.bindPopup(buildPopupContent(displayText, lineName, id, reg));

                    markers[id] = marker;
                    setTimeout(() => setMarkerHeading(marker, 0), 0);
                }
            });
        })
        .catch(err => console.error("Greška:", err));
}

function updateMarkerText(marker, text) {
    const el = marker.getElement();
    if (!el) return;
    const div = el.querySelector('.bus-marker');
    if (!div) return;

    if (div.firstChild && div.firstChild.nodeType === Node.TEXT_NODE) {
        div.firstChild.textContent = text;
    }
}

function updateMarkerStyle(marker, text, isActive) {
    const el = marker.getElement();
    if (!el) return;
    const div = el.querySelector('.bus-marker');
    if (!div) return;

    if (div.firstChild && div.firstChild.nodeType === Node.TEXT_NODE) {
        div.firstChild.textContent = text;
    }

    div.classList.toggle('moving', isActive);
    div.classList.toggle('stopped', !isActive);
}

setInterval(updateBuses, 2000);
updateBuses();
