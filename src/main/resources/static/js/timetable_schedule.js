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

  function expectedIdx(rows) {
    const nAdj = nowMinAdjustedForSchedule();
    let idx = -1;
    for (let i = 0; i < rows.length; i++) {
      const sm = stopTimeToMin(rows[i]);
      if (sm <= nAdj) idx = i;
    }
    return idx;
  }

  function metersPerDegLat() { return 111320; }
  function metersPerDegLng(lat) { return 111320 * Math.cos(lat * Math.PI / 180); }

  function toXY(lat, lng, refLat) {
    return { x: lng * metersPerDegLng(refLat), y: lat * metersPerDegLat() };
  }

  function pointToSegmentDistMeters(pLat, pLng, aLat, aLng, bLat, bLng) {
    const refLat = (aLat + bLat) / 2;

    const P = toXY(pLat, pLng, refLat);
    const A = toXY(aLat, aLng, refLat);
    const B = toXY(bLat, bLng, refLat);

    const ABx = B.x - A.x, ABy = B.y - A.y;
    const APx = P.x - A.x, APy = P.y - A.y;

    const ab2 = ABx * ABx + ABy * ABy;
    if (ab2 === 0) {
      const dx = P.x - A.x, dy = P.y - A.y;
      return Math.sqrt(dx * dx + dy * dy);
    }

    let t = (APx * ABx + APy * ABy) / ab2;
    t = Math.max(0, Math.min(1, t));

    const Cx = A.x + t * ABx;
    const Cy = A.y + t * ABy;

    const dx = P.x - Cx, dy = P.y - Cy;
    return Math.sqrt(dx * dx + dy * dy);
  }

  function bestSegmentIndex(rows, busLat, busLng) {
    let bestIdx = -1;
    let bestDist = Infinity;

    for (let i = 0; i < rows.length - 1; i++) {
      const aLat = parseFloat(rows[i].dataset.lat);
      const aLng = parseFloat(rows[i].dataset.lng);
      const bLat = parseFloat(rows[i + 1].dataset.lat);
      const bLng = parseFloat(rows[i + 1].dataset.lng);
      if ([aLat, aLng, bLat, bLng].some(x => Number.isNaN(x))) continue;

      const d = pointToSegmentDistMeters(busLat, busLng, aLat, aLng, bLat, bLng);
      if (d < bestDist) {
        bestDist = d;
        bestIdx = i;
      }
    }

    return { idx: bestIdx, dist: bestDist };
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

    const exp = expectedIdx(rows);
    if (exp < 0) {
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

    const candidates = (positions || []).filter(p =>
      String(p?.routeShortName ?? "").trim() === lineNumber
    );

    if (!candidates.length) {
      clearMarks(rows);
      lockedBusId = null;
      lastSegIdx = null;
      return;
    }

    const MAX_DIST_METERS = 250;
    const MAX_BEHIND_STOPS = 4;
    const MAX_AHEAD_STOPS = 4;

    if (lockedBusId != null) {
      const locked = candidates.find(p => p?.bus?.id === lockedBusId);
      if (locked) {
        const seg = bestSegmentIndex(rows, locked.gpsLat, locked.gpsLng);
        const behind = exp - seg.idx;
        const ahead = seg.idx - exp;

        if (seg.idx >= 0 && seg.dist <= MAX_DIST_METERS && behind <= MAX_BEHIND_STOPS && ahead <= MAX_AHEAD_STOPS) {
          if (canAcceptSeg(seg.idx)) applyProgress(rows, seg.idx);
          return;
        }
      }
      lockedBusId = null;
    }

    let best = null;
    let bestScore = Infinity;

    for (const p of candidates) {
      const seg = bestSegmentIndex(rows, p.gpsLat, p.gpsLng);
      if (seg.idx < 0 || seg.dist > MAX_DIST_METERS) continue;

      const behind = exp - seg.idx;
      if (behind > MAX_BEHIND_STOPS) continue;

      const ahead = seg.idx - exp;
      if (ahead > MAX_AHEAD_STOPS) continue;

      const score = Math.abs(seg.idx - exp) * 1000 + seg.dist;
      if (score < bestScore) {
        bestScore = score;
        best = { bus: p, seg };
      }
    }

    if (!best) {
      clearMarks(rows);
      return;
    }

    lockedBusId = best.bus?.bus?.id ?? null;
    if (canAcceptSeg(best.seg.idx)) applyProgress(rows, best.seg.idx);
  }

  paintStopsProgress();
  setInterval(paintStopsProgress, 15000);
})();
