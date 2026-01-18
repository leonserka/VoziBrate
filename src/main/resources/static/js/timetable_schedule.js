(() => {
  const root = document.getElementById("timetableSchedulePage");
  if (!root) return;

  const lineNumber = String(root.dataset.line || "").trim();
  const scheduleDay = parseInt(root.dataset.scheduleDay || "0", 10);
  const depTime = String(root.dataset.dep || "00:00");
  const arrTime = String(root.dataset.arr || "00:00");
  const arrivalNextDay = String(root.dataset.arrivalNextday) === "true";

  let lockedBusId = null;
  let lastSegIdx = null;
  let lastUpdateTs = 0;

  function todayAppDay() {
    const js = new Date().getDay();
    return (js === 0) ? 7 : js;
  }

  function parseHHMMToMin(t) {
    const [h, m] = String(t).split(":").map(n => parseInt(n, 10));
    return (h || 0) * 60 + (m || 0);
  }

  function nowMin() {
    const d = new Date();
    return d.getHours() * 60 + d.getMinutes();
  }

  function isScheduleActiveNow() {
    const today = todayAppDay();
    const dep = parseHHMMToMin(depTime);
    const arr = parseHHMMToMin(arrTime);
    const n = nowMin();

    if (!arrivalNextDay) {
      return (today === scheduleDay) && (n >= dep) && (n <= arr);
    }

    if (today === scheduleDay) return n >= dep;
    const nextDay = (scheduleDay % 7) + 1;
    if (today === nextDay) return n <= arr;
    return false;
  }

  function stopTimeToMin(row) {
    const t = row.dataset.time;
    const isNext = String(row.dataset.nextday) === "true";
    let m = parseHHMMToMin(t);
    if (isNext) m += 24 * 60;
    return m;
  }

  function nowMinAdjustedForSchedule() {
    const n = nowMin();
    if (!arrivalNextDay) return n;

    const nextDay = (scheduleDay % 7) + 1;
    if (todayAppDay() === nextDay) return n + 24 * 60;
    return n;
  }

  // === geometry helpers (same base as before) ===
  function metersPerDegLat() { return 111320; }
  function metersPerDegLng(lat) { return 111320 * Math.cos(lat * Math.PI / 180); }

  function toXY(lat, lng, refLat) {
    return { x: lng * metersPerDegLng(refLat), y: lat * metersPerDegLat() };
  }

  // UPDATED: return both dist AND t so we can interpolate time/progress
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

  // Keep bestSegmentIndex for marking passed/next (segIdx)
  function bestSegmentIndex(rows, busLat, busLng) {
    let bestIdx = -1;
    let bestDist = Infinity;

    for (let i = 0; i < rows.length - 1; i++) {
      const aLat = parseFloat(rows[i].dataset.lat);
      const aLng = parseFloat(rows[i].dataset.lng);
      const bLat = parseFloat(rows[i + 1].dataset.lat);
      const bLng = parseFloat(rows[i + 1].dataset.lng);
      if ([aLat, aLng, bLat, bLng].some(x => Number.isNaN(x))) continue;

      const r = pointToSegmentDistAndT(busLat, busLng, aLat, aLng, bLat, bLng);
      if (r.distMeters < bestDist) {
        bestDist = r.distMeters;
        bestIdx = i;
      }
    }

    return { idx: bestIdx, dist: bestDist };
  }

  // NEW: compute "progress minutes since departure" by interpolating between stop times
  function progressMinFromRows(rows, busLat, busLng, depMinAdjusted) {
    if (!rows || rows.length < 2) return null;

    let best = { idx: -1, dist: Infinity, t: 0 };

    for (let i = 0; i < rows.length - 1; i++) {
      const aLat = parseFloat(rows[i].dataset.lat);
      const aLng = parseFloat(rows[i].dataset.lng);
      const bLat = parseFloat(rows[i + 1].dataset.lat);
      const bLng = parseFloat(rows[i + 1].dataset.lng);
      if ([aLat, aLng, bLat, bLng].some(x => Number.isNaN(x))) continue;

      const r = pointToSegmentDistAndT(busLat, busLng, aLat, aLng, bLat, bLng);
      if (r.distMeters < best.dist) {
        best = { idx: i, dist: r.distMeters, t: r.t };
      }
    }

    if (best.idx < 0) return null;

    const aStop = stopTimeToMin(rows[best.idx]);       // already adjusted by nextday
    const bStop = stopTimeToMin(rows[best.idx + 1]);
    if (!Number.isFinite(aStop) || !Number.isFinite(bStop)) return null;

    const aMin = aStop - depMinAdjusted;
    const bMin = bStop - depMinAdjusted;
    if (!Number.isFinite(aMin) || !Number.isFinite(bMin)) return null;

    const progressMin = aMin + best.t * (bMin - aMin);
    return { progressMin, segIdx: best.idx, distMeters: best.dist };
  }

  function clearMarks(rows) {
    rows.forEach(r => r.classList.remove("passed", "next"));
  }

  function applyProgress(rows, segIdx) {
    rows.forEach((r, i) => {
      r.classList.remove("passed", "next");
      if (segIdx >= 0 && i <= segIdx) r.classList.add("passed");
      if (segIdx >= 0 && i === segIdx + 1) r.classList.add("next");
    });
  }

  function canAcceptSeg(newIdx) {
    const now = Date.now();

    if (lastSegIdx == null) {
      lastSegIdx = newIdx;
      lastUpdateTs = now;
      return true;
    }

    const dt = (now - lastUpdateTs) / 1000;
    const maxJump = (dt <= 20) ? 3 : 6;

    if (Math.abs(newIdx - lastSegIdx) > maxJump) {
      return false;
    }

    lastSegIdx = newIdx;
    lastUpdateTs = now;
    return true;
  }

  async function paintStopsProgress() {
    const rows = Array.from(document.querySelectorAll(".stop-row"));
    if (!rows.length) return;

    if (!isScheduleActiveNow()) {
      clearMarks(rows);
      lockedBusId = null;
      lastSegIdx = null;
      return;
    }

    // NEW: elapsed minutes since this schedule's departure (adjusted for next-day)
    const depMin = parseHHMMToMin(depTime);
    const depMinAdjusted = (!arrivalNextDay) ? depMin : depMin; // dep is always on scheduleDay "base"
    const elapsedMin = nowMinAdjustedForSchedule() - depMinAdjusted;

    if (!Number.isFinite(elapsedMin) || elapsedMin < -10) {
      clearMarks(rows);
      lockedBusId = null;
      lastSegIdx = null;
      return;
    }

    let positions = [];
    try {
      const posRes = await fetch("/api/positions/current");
      positions = await posRes.json();
    } catch (e) {
      console.error(e);
      return;
    }

    // UPDATED: accept candidates either by routeShortName OR by bus.line.lineNumber (like live map)
    const candidates = (positions || []).filter(p => {
      const rsn = String(p?.routeShortName ?? "").trim();
      const ln = String(p?.bus?.line?.lineNumber ?? "").trim();
      return rsn === lineNumber || ln === lineNumber;
    });

    if (!candidates.length) {
      clearMarks(rows);
      lockedBusId = null;
      lastSegIdx = null;
      return;
    }

    const MAX_DIST_METERS = 250;
    const MAX_TIME_DIFF_MIN = 45; // tolerancija kašnjenja/žurbe; po potrebi smanji/povećaj

    // If locked, try keep it (BUT validate by timeDiff rather than expIdx)
    if (lockedBusId != null) {
      const locked = candidates.find(p => p?.bus?.id === lockedBusId);
      if (locked) {
        const pr = progressMinFromRows(rows, locked.gpsLat, locked.gpsLng, depMinAdjusted);
        if (pr && pr.distMeters <= MAX_DIST_METERS) {
          const timeDiff = Math.abs(pr.progressMin - elapsedMin);
          if (timeDiff <= MAX_TIME_DIFF_MIN) {
            if (canAcceptSeg(pr.segIdx)) applyProgress(rows, pr.segIdx);
            return;
          }
        }
      }
      lockedBusId = null;
    }

    // Choose best bus for THIS schedule by minimizing |progressMin - elapsedMin|
    let best = null;
    let bestScore = Infinity;

    for (const p of candidates) {
      const pr = progressMinFromRows(rows, p.gpsLat, p.gpsLng, depMinAdjusted);
      if (!pr) continue;
      if (pr.distMeters > MAX_DIST_METERS) continue;

      const timeDiff = Math.abs(pr.progressMin - elapsedMin);
      if (timeDiff > MAX_TIME_DIFF_MIN) continue;

      const score = timeDiff * 1000 + pr.distMeters; // primarno vrijeme, sekundarno udaljenost
      if (score < bestScore) {
        bestScore = score;
        best = { bus: p, segIdx: pr.segIdx };
      }
    }

    if (!best) {
      clearMarks(rows);
      lockedBusId = null;
      lastSegIdx = null;
      return;
    }

    lockedBusId = best.bus?.bus?.id ?? null;
    if (canAcceptSeg(best.segIdx)) applyProgress(rows, best.segIdx);
  }

  paintStopsProgress();
  setInterval(paintStopsProgress, 15000);
})();
