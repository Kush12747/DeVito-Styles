// Number, money and date formatting shared by the dashboard panels.

const money = new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" });
const moneyRound = new Intl.NumberFormat("en-US", { style: "currency", currency: "USD", maximumFractionDigits: 0 });

export function formatMoney(value) {
    return money.format(Number(value) || 0);
}

// $1,886 for tiles, $1.9K for axis ticks
export function formatMoneyShort(value, compact = false) {
    const n = Number(value) || 0;
    if (compact && Math.abs(n) >= 1000) {
        return `$${(n / 1000).toFixed(n % 1000 === 0 ? 0 : 1)}K`;
    }
    return moneyRound.format(n);
}

export function formatCount(value) {
    return new Intl.NumberFormat("en-US").format(Number(value) || 0);
}

// "2026-10-27" -> Date at local midnight (avoids the UTC shift of new Date("2026-10-27"))
export function parseDay(isoDate) {
    const [y, m, d] = isoDate.split("-").map(Number);
    return new Date(y, m - 1, d);
}

// Axis label for one trend bucket
export function bucketLabel(isoDate, range) {
    const date = parseDay(isoDate);
    if (range === "month") {
        return date.toLocaleDateString("en-US", { month: "short" });
    }
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric" });
}

// Tooltip title for one trend bucket
export function bucketTitle(isoDate, range) {
    const date = parseDay(isoDate);
    if (range === "month") {
        return date.toLocaleDateString("en-US", { month: "long", year: "numeric" });
    }
    if (range === "week") {
        return "Week of " + date.toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });
    }
    return date.toLocaleDateString("en-US", { weekday: "short", month: "short", day: "numeric" });
}

export function formatDateTime(isoDateTime) {
    return new Date(isoDateTime).toLocaleString("en-US", {
        month: "short", day: "numeric", hour: "numeric", minute: "2-digit"
    });
}

export function formatTime(isoDateTime) {
    return new Date(isoDateTime).toLocaleTimeString("en-US", { hour: "numeric", minute: "2-digit" });
}

export function hourLabel(hour) {
    const suffix = hour < 12 ? "a" : "p";
    return `${hour % 12 === 0 ? 12 : hour % 12}${suffix}`;
}

// Percent change between two numbers; null when there's nothing to compare against
export function percentChange(current, previous) {
    const c = Number(current) || 0;
    const p = Number(previous) || 0;
    if (p === 0) return c === 0 ? 0 : null;
    return ((c - p) / p) * 100;
}

// Clean axis ticks: 0 and up to `count` evenly spaced round numbers
export function niceTicks(max, count = 4) {
    if (max <= 0) return [0, 1];
    const rough = max / count;
    const magnitude = 10 ** Math.floor(Math.log10(rough));
    const step = [1, 2, 2.5, 5, 10].map(m => m * magnitude).find(s => s >= rough);
    const ticks = [];
    for (let v = 0; v <= max + step * 0.001 || ticks.length < 2; v += step) {
        ticks.push(Math.round(v * 100) / 100);
        if (v >= max) break;
    }
    return ticks;
}
