(() => {
  const btn = document.getElementById("regenStopsBtn");
  const status = document.getElementById("regenStatus");

  if (!btn || !status) return;

  btn.addEventListener("click", async () => {
    if (!confirm("This will delete and regenerate schedule_stops for all schedules. Continue?")) return;

    btn.disabled = true;
    status.textContent = "  restoring...";

    try {
      const res = await fetch("/admin/schedules/regenerate-stops", {
        method: "POST",
        credentials: "same-origin",
        headers: { "X-Requested-With": "XMLHttpRequest" }
      });

      const text = await res.text();

      if (!res.ok) {
        status.textContent = `❌ ${res.status}: ${text}`;
        return;
      }

      status.textContent = "✅ " + text;
    } catch (e) {
      console.error(e);
      status.textContent = "❌ " + (e?.message ?? e);
    } finally {
      btn.disabled = false;
    }
  });
})();
