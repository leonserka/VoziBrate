(() => {
  const root = document.getElementById("timetableLinePage");
  if (!root) return;

  const selectedDay = parseInt(root.dataset.selectedDay || "0", 10);
  const lineNum = String(root.dataset.line || "").trim();

  function parseHHMM(t) {
    const parts = String(t).split(":").map(x => parseInt(x, 10));
    return { h: parts[0] || 0, m: parts[1] || 0 };
  }

  function minutesNow() {
    const d = new Date();
    return d.getHours() * 60 + d.getMinutes();
  }


  function todayAppDay() {
    const js = new Date().getDay();
    return (js === 0) ? 7 : js;
  }

  async function paintLiveBadges() {
    const badges = document.querySelectorAll(".live-badge");
    if (!badges.length) return;

    if (selectedDay !== todayAppDay()) {
      badges.forEach(b => (b.textContent = ""));
      return;
    }

    const nowMin = minutesNow();

    let hasLive = false;
    try {
      const res = await fetch("/api/positions/current");
      const data = await res.json();
      hasLive = (data || []).some(p => (String(p?.routeShortName || "").trim() === lineNum));
    } catch (e) {
      console.error(e);
    }

    badges.forEach(b => {
      const dep = parseHHMM(b.dataset.dep);
      const arr = parseHHMM(b.dataset.arr);
      const depMin = dep.h * 60 + dep.m;
      const arrMin = arr.h * 60 + arr.m;

      const isActive = hasLive && (nowMin >= depMin) && (nowMin <= arrMin);

      b.textContent = isActive ? " 🚌 LIVE" : "";
      b.style.fontWeight = "700";
    });
  }

  paintLiveBadges();
  setInterval(paintLiveBadges, 15000);
})();
