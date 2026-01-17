var map = L.map('map').setView([43.515, 16.45], 13);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
}).addTo(map);

const MOVE_THRESHOLD_METERS = 5;
const MOVING_HOLD_MS = 60000;

var markers = {};

let activeRouteLayer = null;
let activeStopMarkers = [];
let lastClickedLineNum = null;
let activeBusId = null;

const lastVariantByLine = {};
const HYSTERESIS_METERS = 120;

let activeRouteStops = null;
let activeRouteLine = null;

const MAX_DIST_TO_ROUTE_METERS = 250;

function clearRoute() {
    if (activeRouteLayer) {
        map.removeLayer(activeRouteLayer);
        activeRouteLayer = null;
    }
    activeStopMarkers.forEach(m => map.removeLayer(m));
    activeStopMarkers = [];

    activeRouteStops = null;
    activeRouteLine = null;
}

function angleDiff(a, b) {
    let d = Math.abs(a - b) % 360;
    return d > 180 ? 360 - d : d;
}

function findNearestStopIndex(stopsOrdered, busLatLng) {
    let bestIdx = -1;
    let bestDist = Infinity;

    stopsOrdered.forEach((s, i) => {
        if (s.lat == null || s.lng == null) return;
        const d = busLatLng.distanceTo(L.latLng(s.lat, s.lng));
        if (d < bestDist) {
            bestDist = d;
            bestIdx = i;
        }
    });

    return bestIdx;
}

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

function lockPopupInnerScroll(popup) {
    if (!popup) return;
    const el = popup.getElement();
    if (!el) return;
    L.DomEvent.disableClickPropagation(el);
    L.DomEvent.disableScrollPropagation(el);
    const boxes = el.querySelectorAll(".route-info");
    boxes.forEach(box => {
        box.addEventListener("wheel", (e) => e.stopPropagation(), { passive: true });
        box.addEventListener("touchmove", (e) => e.stopPropagation(), { passive: true });
    });
}

function buildPopupContent(lineNum, lineName, gbr, reg) {
    const safeLine = String(lineNum ?? "").trim();
    const safeId = String(gbr).trim();

    return `
        <div class="popup-center">
            <h3 class="popup-title">Linija ${safeLine}</h3>
            <small>${lineName ?? ""}</small><br>
            <hr class="popup-hr">
            <b>Garažni br:</b> ${gbr ?? ""}<br>
            <b>Tablica:</b> ${reg ?? ""}<br><br>

            <button class="popup-btn" onclick="showRouteAuto('${safeId}', '${safeLine}')">🗺 View route</button>

            <details class="route-details" open>
                <summary>🕒 Kad je na kojoj stanici</summary>
                <div id="route-info-${safeId}" class="route-info">
                    Klikni <b>View route</b> da se učita ruta.
                </div>
            </details>
        </div>
    `;
}

async function fetchStops(lineNum, variant) {
    try {
        const safeLine = encodeURIComponent(String(lineNum).trim());
        const safeVar = encodeURIComponent(String(variant).trim());

        const res = await fetch(`/api/lines/${safeLine}/route?variant=${safeVar}`);
        if (!res.ok) return [];

        const stops = await res.json();
        return (stops ?? []).slice().sort((a, b) => (a.orderNumber ?? 0) - (b.orderNumber ?? 0));
    } catch (err) {
        console.warn("fetchStops failed:", lineNum, variant, err);
        return [];
    }
}

function minDistanceToStopsMeters(stopsOrdered, busLatLng) {
    let best = Infinity;
    for (const s of stopsOrdered) {
        if (s.lat == null || s.lng == null) continue;
        const d = busLatLng.distanceTo(L.latLng(s.lat, s.lng));
        if (d < best) best = d;
    }
    return best;
}

function metersPerDegLat() { return 111320; }
function metersPerDegLng(lat) { return 111320 * Math.cos(lat * Math.PI / 180); }

function toXY(lat, lng, refLat) {
    return { x: lng * metersPerDegLng(refLat), y: lat * metersPerDegLat() };
}

function pointToSegmentDistAndT(pLat, pLng, aLat, aLng, bLat, bLng) {
    const refLat = (aLat + bLat) / 2;

    const P = toXY(pLat, pLng, refLat);
    const A = toXY(aLat, aLng, refLat);
    const B = toXY(bLat, bLng, refLat);

    const ABx = B.x - A.x, ABy = B.y - A.y;
    const APx = P.x - A.x, APy = P.y - A.y;

    const ab2 = ABx * ABx + ABy * ABy;
    if (ab2 === 0) {
        const dx = P.x - A.x, dy = P.y - A.y;
        return { distMeters: Math.sqrt(dx * dx + dy * dy), t: 0 };
    }

    let t = (APx * ABx + APy * ABy) / ab2;
    t = Math.max(0, Math.min(1, t));

    const Cx = A.x + t * ABx;
    const Cy = A.y + t * ABy;

    const dx = P.x - Cx, dy = P.y - Cy;
    return { distMeters: Math.sqrt(dx * dx + dy * dy), t };
}

function estimateProgressMinutesFromStart(orderedStops, busLatLng) {
    if (!orderedStops || orderedStops.length < 2) return null;

    let best = { idx: -1, dist: Infinity, t: 0 };

    for (let i = 0; i < orderedStops.length - 1; i++) {
        const a = orderedStops[i];
        const b = orderedStops[i + 1];
        if ([a.lat, a.lng, b.lat, b.lng].some(v => v == null)) continue;

        const r = pointToSegmentDistAndT(
            busLatLng.lat, busLatLng.lng,
            a.lat, a.lng,
            b.lat, b.lng
        );

        if (r.distMeters < best.dist) {
            best = { idx: i, dist: r.distMeters, t: r.t };
        }
    }

    if (best.idx < 0) return null;

    const aMin = Number(orderedStops[best.idx]?.minutesFromStart);
    const bMin = Number(orderedStops[best.idx + 1]?.minutesFromStart);
    if (!Number.isFinite(aMin) || !Number.isFinite(bMin)) return null;

    const progressMin = aMin + best.t * (bMin - aMin);

    return { progressMin, segIdx: best.idx, distMeters: best.dist };
}

function pad2(n) { return String(n).padStart(2, "0"); }

function formatHHMM(dateObj) {
    return `${pad2(dateObj.getHours())}:${pad2(dateObj.getMinutes())}`;
}

function renderRouteInfo(orderedStops, busId, nearestIdx, etaBase) {
    const box = document.getElementById(`route-info-${String(busId).trim()}`);
    if (!box) return;

    if (!orderedStops || !orderedStops.length) {
        box.innerHTML = `<div class="route-info-empty">Nema stanica za rutu.</div>`;
        return;
    }

    box.innerHTML = orderedStops.map((s, i) => {
        const isPassed = nearestIdx >= 0 && i <= nearestIdx;
        const isNext = nearestIdx >= 0 && i === nearestIdx + 1;

        const cls = isNext ? "next" : (isPassed ? "passed" : "future");

        let timeTxt = `<span class="route-stop-time route-stop-time-empty">--:--</span>`;
        const mm = Number(s.minutesFromStart);

        if (etaBase && Number.isFinite(mm)) {
            const eta = new Date(etaBase.getTime() + mm * 60000);
            timeTxt = `<span class="route-stop-time">${formatHHMM(eta)}</span>`;
        }

        const name = s.name ?? "Stanica";
        const num = (s.orderNumber != null) ? s.orderNumber : (i + 1);

        return `
            <div class="route-stop ${cls}">
                <span class="route-stop-idx">${num}.</span>
                <span class="route-stop-name">${name}</span>
                ${timeTxt}
            </div>
        `;
    }).join("");
}

function renderRouteOnMap(orderedStops, lineNum) {
    clearRoute();

    activeRouteStops = orderedStops;
    activeRouteLine = String(lineNum).trim();

    const coords = orderedStops
        .filter(s => s.lat != null && s.lng != null)
        .map(s => [s.lat, s.lng]);

    if (!coords.length) {
        alert("Ruta nema koordinate za liniju " + lineNum);
        return;
    }

    activeRouteLayer = L.polyline(coords).addTo(map);
    map.fitBounds(activeRouteLayer.getBounds(), { padding: [30, 30] });

    refreshActiveRouteProgress();
}

function refreshActiveRouteProgress() {
    if (!activeRouteStops || !activeRouteLine || !activeBusId) return;

    const lineNum = activeRouteLine;

    activeStopMarkers.forEach(m => map.removeLayer(m));
    activeStopMarkers = [];

    const busMarker = markers[activeBusId];

    let nearestIdx = -1;
    let etaBase = null;

    if (busMarker) {
        const busLatLng = busMarker.getLatLng();
        const prog = estimateProgressMinutesFromStart(activeRouteStops, busLatLng);

        if (prog && prog.distMeters <= MAX_DIST_TO_ROUTE_METERS) {
            nearestIdx = prog.segIdx;
            etaBase = new Date(Date.now() - prog.progressMin * 60000);
        } else {
            nearestIdx = findNearestStopIndex(activeRouteStops, busLatLng);
            etaBase = null;
        }
    }

    renderRouteInfo(activeRouteStops, activeBusId, nearestIdx, etaBase);

    activeRouteStops.forEach((s, i) => {
        if (s.lat == null || s.lng == null) return;

        const isPassed = nearestIdx >= 0 && i <= nearestIdx;
        const isNext = nearestIdx >= 0 && i === nearestIdx + 1;

        const color = isNext ? "#f1c40f" : (isPassed ? "#2d3436" : "#2980b9");

        const marker = L.circleMarker([s.lat, s.lng], {
            radius: 7,
            weight: 2,
            opacity: 1,
            fillOpacity: 0.9,
            color: color,
            fillColor: color
        }).addTo(map);

        marker.bindTooltip(`${s.orderNumber}. ${s.name}`, { permanent: false });
        activeStopMarkers.push(marker);
    });
}

window.showRoute = async function (busId, lineNum, variant = "A") {
    try {
        lineNum = String(lineNum).trim();
        busId = String(busId).trim();
        variant = (variant === "B") ? "B" : "A";

        lastClickedLineNum = lineNum;
        activeBusId = busId;

        const orderedStops = await fetchStops(lineNum, variant);
        if (!orderedStops.length) {
            alert(`Nema rute ${variant} za liniju ${lineNum}`);
            return;
        }

        lastVariantByLine[lineNum] = variant;
        renderRouteOnMap(orderedStops, lineNum);
    } catch (e) {
        console.error(e);
        alert("Greška kod prikaza rute.");
    }
};

window.showRouteAuto = async function (busId, lineNum) {
    try {
        lineNum = String(lineNum).trim();
        busId = String(busId).trim();

        lastClickedLineNum = lineNum;
        activeBusId = busId;

        const busMarker = markers[busId];
        if (!busMarker) {
            return window.showRoute(busId, lineNum, "A");
        }

        const busLatLng = busMarker.getLatLng();

        const results = await Promise.allSettled([
            fetchStops(lineNum, "A"),
            fetchStops(lineNum, "B")
        ]);

        const aStops = (results[0].status === "fulfilled") ? results[0].value : [];
        const bStops = (results[1].status === "fulfilled") ? results[1].value : [];

        if (!bStops.length && aStops.length) return window.showRoute(busId, lineNum, "A");
        if (!aStops.length && bStops.length) return window.showRoute(busId, lineNum, "B");
        if (!aStops.length && !bStops.length) {
            alert("Nema rute za liniju " + lineNum);
            return;
        }

        const progA = estimateProgressMinutesFromStart(aStops, busLatLng);
        const progB = estimateProgressMinutesFromStart(bStops, busLatLng);

        const distA = progA ? progA.distMeters : minDistanceToStopsMeters(aStops, busLatLng);
        const distB = progB ? progB.distMeters : minDistanceToStopsMeters(bStops, busLatLng);

        let scoreA = distA;
        let scoreB = distB;

        if (busMarker && typeof busMarker._heading === 'number' && busMarker._heading !== null) {
            const busHeading = busMarker._heading;

            if (progA && aStops[progA.segIdx] && aStops[progA.segIdx + 1]) {
                const bearingA = calculateBearing(aStops[progA.segIdx], aStops[progA.segIdx + 1]);
                const diffA = angleDiff(busHeading, bearingA);
                if (diffA > 90) scoreA += 1000;
                else scoreA += diffA;
            }

            if (progB && bStops[progB.segIdx] && bStops[progB.segIdx + 1]) {
                const bearingB = calculateBearing(bStops[progB.segIdx], bStops[progB.segIdx + 1]);
                const diffB = angleDiff(busHeading, bearingB);
                if (diffB > 90) scoreB += 1000;
                else scoreB += diffB;
            }
        }

        let chosen = (scoreA <= scoreB) ? "A" : "B";

        const prev = lastVariantByLine[lineNum];
        if (prev && Math.abs(scoreA - scoreB) < HYSTERESIS_METERS) {
            chosen = prev;
        }

        lastVariantByLine[lineNum] = chosen;

        const chosenStops = (chosen === "A") ? aStops : bStops;
        renderRouteOnMap(chosenStops, lineNum);

    } catch (e) {
        console.error(e);
        alert("Greška kod AUTO rute.");
    }
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
                let routeKey = null;

                if (pos.bus.line && pos.bus.line.lineNumber) {
                    routeKey = String(pos.bus.line.lineNumber).trim();
                    displayText = routeKey;
                    lineName = pos.bus.line.name ?? "Nepoznata";
                }

                if (pos.routeShortName && pos.routeShortName.trim() !== "") {
                    displayText = pos.routeShortName.trim();
                    if (!routeKey) routeKey = displayText;
                    lineName = "Prometko";
                }

                if (!routeKey) routeKey = displayText;

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

                        if (marker._heading !== undefined && marker._heading !== null) {
                            setMarkerHeading(marker, marker._heading);
                        }
                    } else {
                        updateMarkerText(marker, displayText);
                    }

                    const popup = marker.getPopup();
                    const isOpen = (popup && marker.isPopupOpen && marker.isPopupOpen());
                    if (!isOpen) {
                        marker.setPopupContent(buildPopupContent(routeKey, lineName, id, reg));
                    }

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
                    marker._heading = null;

                    marker.bindPopup(buildPopupContent(routeKey, lineName, id, reg));

                    marker.on("popupopen", (e) => {
                        lockPopupInnerScroll(e.popup);
                    });

                    markers[id] = marker;
                }
            });

            if (activeRouteLayer && activeRouteStops && activeRouteLine) {
                refreshActiveRouteProgress();
            }
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
    } else {
        div.textContent = text;
        if (!div.querySelector('.bus-arrow')) {
            const arrow = document.createElement('div');
            arrow.className = 'bus-arrow';
            div.appendChild(arrow);
        }
    }
}

function updateMarkerStyle(marker, text, isActive) {
    const el = marker.getElement();
    if (!el) return;
    const div = el.querySelector('.bus-marker');
    if (!div) return;

    if (div.firstChild && div.firstChild.nodeType === Node.TEXT_NODE) {
        div.firstChild.textContent = text;
    } else {
        div.textContent = text;
        if (!div.querySelector('.bus-arrow')) {
            const arrow = document.createElement('div');
            arrow.className = 'bus-arrow';
            div.appendChild(arrow);
        }
    }

    div.classList.toggle('moving', isActive);
    div.classList.toggle('stopped', !isActive);
}

setInterval(updateBuses, 2000);
updateBuses();