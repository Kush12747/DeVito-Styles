import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getDashboard } from "../../../Services/adminService";
import { BusyHeatmap, Legend, RankedBars, StackedColumns, StatusMeter, TrendLines } from "./charts";
import {
    bucketTitle, formatCount, formatDateTime, formatMoney, formatMoneyShort, formatTime, percentChange
} from "./format";
import "../../../styles/adminDashboard.css";

const RANGES = [
    { key: "day", label: "Daily", window: "Today", compare: "yesterday", trend: "Last 14 days" },
    { key: "week", label: "Weekly", window: "Last 7 days", compare: "prev 7 days", trend: "Last 12 weeks" },
    { key: "month", label: "Monthly", window: "Last 30 days", compare: "prev 30 days", trend: "Last 12 months" }
];

// Data colors (validated as a set against the panel surface, incl. color-blind checks)
const SERIES = {
    services: "#199e70",
    products: "#9085e9",
    cancelled: "#c98500"
};

const REVENUE_SERIES = [
    { key: "serviceRevenue", label: "Services", color: SERIES.services },
    { key: "productRevenue", label: "Products", color: SERIES.products }
];

const APPOINTMENT_SERIES = [
    { key: "appointments", label: "Appointments", color: SERIES.services, line: true },
    { key: "cancelled", label: "Cancelled", color: SERIES.cancelled, line: true }
];

const REFRESH_MS = 60_000;

function Panel({ title, meta, actions, className = "", children }) {
    return (
        <section className={`dash-panel ${className}`}>
            <header className="dash-panel-head">
                <div>
                    <h2 className="dash-panel-title">{title}</h2>
                    {meta && <span className="dash-panel-meta">{meta}</span>}
                </div>
                {actions}
            </header>
            {children}
        </section>
    );
}

function Delta({ current, previous, compare }) {
    const change = percentChange(current, previous);

    if (change === null) {
        return <span className="dash-delta flat">new vs {compare}</span>;
    }

    const direction = change > 0.5 ? "up" : change < -0.5 ? "down" : "flat";
    const arrow = { up: "▲", down: "▼", flat: "■" }[direction];

    return (
        <span className={`dash-delta ${direction}`}>
            <span aria-hidden="true">{arrow}</span> {change > 0 ? "+" : ""}{change.toFixed(1)}%
            <span className="dash-delta-compare"> vs {compare}</span>
        </span>
    );
}

function KpiTile({ label, value, current, previous, compare, sub }) {
    return (
        <div className="dash-kpi">
            <span className="dash-kpi-label">{label}</span>
            <span className="dash-kpi-value">{value}</span>
            {previous !== undefined && <Delta current={current} previous={previous} compare={compare} />}
            {sub && <span className="dash-kpi-sub">{sub}</span>}
        </div>
    );
}

// Accessible table view of the trend data
function TrendTable({ points, range }) {
    return (
        <div className="dash-table-wrap">
            <table className="dash-table">
                <thead>
                    <tr>
                        <th>Period</th>
                        <th>Services</th>
                        <th>Products</th>
                        <th>Total</th>
                        <th>Appts</th>
                        <th>Cancelled</th>
                    </tr>
                </thead>
                <tbody>
                    {[...points].reverse().map(p => (
                        <tr key={p.start}>
                            <td>{bucketTitle(p.start, range)}</td>
                            <td>{formatMoney(p.serviceRevenue)}</td>
                            <td>{formatMoney(p.productRevenue)}</td>
                            <td>{formatMoney(Number(p.serviceRevenue) + Number(p.productRevenue))}</td>
                            <td>{formatCount(p.appointments)}</td>
                            <td>{formatCount(p.cancelled)}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

const ACTIVITY_TAGS = { ORDER: "ORD", APPOINTMENT: "APT", REVIEW: "REV" };

function ActivityLine({ event }) {
    const status = event.status?.toLowerCase();
    return (
        <li className="dash-log-line">
            <span className={`dash-log-tag ${event.type.toLowerCase()}`}>{ACTIVITY_TAGS[event.type]}</span>
            <span className="dash-log-text">
                <span>
                    <strong>{event.customerName}</strong> {event.detail}
                    {event.rating != null && <span className="dash-log-rating"> {"★".repeat(event.rating)}</span>}
                </span>
                <span className="dash-log-time">{formatDateTime(event.occurredAt)}</span>
            </span>
            <span className="dash-log-right">
                {event.amount != null && <span className="dash-log-amount">{formatMoney(event.amount)}</span>}
                {status && <span className={`dash-log-status ${status}`}>{status}</span>}
            </span>
        </li>
    );
}

function DashboardOverview() {
    const [range, setRange] = useState("week");
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [syncedAt, setSyncedAt] = useState(null);
    const [revenueView, setRevenueView] = useState("chart");
    const [reloadKey, setReloadKey] = useState(0);

    // Fetch on range change, on manual refresh and every minute.
    // `ignore` drops a slow response once the range has moved on.
    useEffect(() => {
        let ignore = false;

        function fetchData() {
            getDashboard(range)
                .then(result => {
                    if (ignore) return;
                    setData(result);
                    setError(null);
                    setSyncedAt(new Date());
                })
                .catch(err => {
                    if (ignore) return;
                    console.error(err);
                    setError(err.status === 401 || err.status === 403 ? "auth" : "network");
                })
                .finally(() => {
                    if (!ignore) setLoading(false);
                });
        }

        fetchData();
        const timer = setInterval(fetchData, REFRESH_MS);
        return () => {
            ignore = true;
            clearInterval(timer);
        };
    }, [range, reloadKey]);

    function changeRange(nextRange) {
        if (nextRange === range) return;
        setLoading(true);
        setRange(nextRange);
    }

    function refresh() {
        setLoading(true);
        setReloadKey(key => key + 1);
    }

    const meta = RANGES.find(r => r.key === range);

    if (error === "auth") {
        return (
            <div className="dash-message">
                <p className="dash-prompt-line"><span className="dash-prompt">$</span> access denied</p>
                <p>Your session doesn't have admin access or has expired.</p>
                <Link to="/login" className="dash-button">Log in again</Link>
            </div>
        );
    }

    const current = data?.current;
    const previous = data?.previous;
    const cancelRate = current && current.appointments + current.cancelled > 0
        ? (current.cancelled / (current.appointments + current.cancelled)) * 100
        : 0;

    return (
        <div className="dash" aria-busy={loading}>
            {/* Header: prompt line, title, live status and range toggle */}
            <header className="dash-header">
                <div>
                    <p className="dash-prompt-line" aria-hidden="true">
                        <span className="dash-prompt">root@devito:~/admin$</span> ./overview --range={range}
                        <span className="dash-caret" />
                    </p>
                    <h1 className="dash-title">Shop Overview</h1>
                </div>

                <div className="dash-header-controls">
                    <span className={`dash-live ${error ? "offline" : ""}`}>
                        <span className="dash-live-dot" aria-hidden="true" />
                        {error
                            ? "Offline · retrying"
                            : syncedAt ? `Live · synced ${syncedAt.toLocaleTimeString()}` : "Connecting…"}
                    </span>

                    <div className="dash-range" role="group" aria-label="Time range">
                        {RANGES.map(r => (
                            <button
                                key={r.key}
                                type="button"
                                className={r.key === range ? "active" : ""}
                                aria-pressed={r.key === range}
                                onClick={() => changeRange(r.key)}
                            >
                                {r.label}
                            </button>
                        ))}
                    </div>

                    <button type="button" className="dash-icon-btn" onClick={refresh}
                            aria-label="Refresh now" title="Refresh now" disabled={loading}>
                        ⟳
                    </button>
                </div>
            </header>

            {error === "network" && !data && (
                <div className="dash-message">
                    <p className="dash-prompt-line"><span className="dash-prompt">$</span> connection refused</p>
                    <p>Couldn't reach the server. Make sure the backend is running, then refresh.</p>
                </div>
            )}

            {!data && !error && <div className="dash-loading">Loading metrics<span className="dash-caret" /></div>}

            {data && (
                <>
                    {/* KPI tiles */}
                    <div className="dash-kpis">
                        <KpiTile
                            label={`Revenue · ${meta.window}`}
                            value={formatMoneyShort(current.revenue)}
                            current={current.revenue}
                            previous={previous.revenue}
                            compare={meta.compare}
                            sub={`Services ${formatMoneyShort(current.serviceRevenue)} · Products ${formatMoneyShort(current.productRevenue)}`}
                        />
                        <KpiTile
                            label="Appointments"
                            value={formatCount(current.appointments)}
                            current={current.appointments}
                            previous={previous.appointments}
                            compare={meta.compare}
                            sub={`${formatCount(current.cancelled)} cancelled (${cancelRate.toFixed(0)}%)`}
                        />
                        <KpiTile
                            label="Clients served"
                            value={formatCount(current.clientsServed)}
                            current={current.clientsServed}
                            previous={previous.clientsServed}
                            compare={meta.compare}
                            sub="Unique customers with a completed visit"
                        />
                        <KpiTile
                            label="Product orders"
                            value={formatCount(current.productOrders)}
                            current={current.productOrders}
                            previous={previous.productOrders}
                            compare={meta.compare}
                            sub={current.productOrders
                                ? `Avg order ${formatMoney(current.productRevenue / current.productOrders)}`
                                : "No paid orders yet"}
                        />
                        <KpiTile
                            label="Avg rating"
                            value={current.averageRating != null ? `${current.averageRating.toFixed(2)} ★` : "—"}
                            sub={`${formatCount(current.reviewCount)} review${current.reviewCount === 1 ? "" : "s"}${previous.averageRating != null ? ` · ${previous.averageRating.toFixed(2)} ${meta.compare}` : ""}`}
                        />
                    </div>

                    <div className="dash-grid">
                        <Panel
                            title="Revenue"
                            meta={meta.trend}
                            className="span-8"
                            actions={
                                <div className="dash-panel-actions">
                                    <Legend series={REVENUE_SERIES} />
                                    <button type="button" className="dash-link-btn"
                                            onClick={() => setRevenueView(v => (v === "chart" ? "table" : "chart"))}>
                                        [{revenueView === "chart" ? "table" : "chart"}]
                                    </button>
                                </div>
                            }
                        >
                            {revenueView === "chart"
                                ? <StackedColumns points={data.trend} range={range} series={REVENUE_SERIES}
                                                  formatValue={formatMoney} formatTick={v => formatMoneyShort(v, true)} />
                                : <TrendTable points={data.trend} range={range} />}
                        </Panel>

                        <Panel title="Appointment outcomes" meta={meta.trend} className="span-4">
                            <StatusMeter mix={data.statusMix} />
                        </Panel>

                        <Panel title="Bookings" meta={meta.trend} className="span-6"
                               actions={<Legend series={APPOINTMENT_SERIES} />}>
                            <TrendLines points={data.trend} range={range} series={APPOINTMENT_SERIES} />
                        </Panel>

                        <Panel title="Busy times" meta={`${meta.trend} · by weekday & hour`} className="span-6">
                            <BusyHeatmap cells={data.busyTimes} />
                        </Panel>

                        <Panel title="Top services" meta={`${meta.trend} · by bookings`} className="span-4">
                            <RankedBars
                                items={data.topServices}
                                color={SERIES.services}
                                value={item => item.count}
                                formatValue={formatCount}
                                detail={item => formatMoneyShort(item.revenue)}
                                emptyText="No bookings in this window yet."
                            />
                        </Panel>

                        <Panel title="Top products" meta={`${meta.trend} · by revenue`} className="span-4">
                            <RankedBars
                                items={data.topProducts}
                                color={SERIES.products}
                                value={item => Number(item.revenue)}
                                formatValue={v => formatMoneyShort(v)}
                                detail={item => `${formatCount(item.count)} sold`}
                                emptyText="No paid orders in this window yet."
                            />
                        </Panel>

                        <Panel title="Up next" meta="Booked appointments" className="span-4">
                            {data.upcoming.length ? (
                                <ul className="dash-upcoming">
                                    {data.upcoming.map(a => (
                                        <li key={a.appointmentId}>
                                            <div className="dash-upcoming-when">
                                                <span>{new Date(a.appointmentDatetime).toLocaleDateString("en-US", { weekday: "short", month: "short", day: "numeric" })}</span>
                                                <strong>{formatTime(a.appointmentDatetime)}</strong>
                                            </div>
                                            <div className="dash-upcoming-what">
                                                <strong>{a.customerName}</strong>
                                                <span>{a.serviceName} · {a.barberName}</span>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <p className="dash-empty">Nothing booked yet.</p>
                            )}
                        </Panel>

                        <Panel title="Barber performance" meta={meta.trend} className="span-7">
                            {data.barbers.length ? (
                                <div className="dash-table-wrap">
                                    <table className="dash-table">
                                        <thead>
                                            <tr>
                                                <th>Barber</th>
                                                <th>Appts</th>
                                                <th>Completed</th>
                                                <th>Revenue</th>
                                                <th>Rating</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {data.barbers.map(b => {
                                                const top = Math.max(...data.barbers.map(x => Number(x.revenue)), 1);
                                                return (
                                                    <tr key={b.barberId}>
                                                        <td>{b.name}</td>
                                                        <td>{formatCount(b.appointments)}</td>
                                                        <td>{formatCount(b.completed)}</td>
                                                        <td>
                                                            <div className="dash-inline-bar">
                                                                <span>{formatMoneyShort(b.revenue)}</span>
                                                                <div className="dash-ranked-track">
                                                                    <div className="dash-ranked-bar"
                                                                         style={{ width: `${(Number(b.revenue) / top) * 100}%`, background: SERIES.services }} />
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            {b.averageRating != null
                                                                ? <>{b.averageRating.toFixed(2)} <span className="dash-muted">({b.reviewCount})</span></>
                                                                : <span className="dash-muted">—</span>}
                                                        </td>
                                                    </tr>
                                                );
                                            })}
                                        </tbody>
                                    </table>
                                </div>
                            ) : (
                                <p className="dash-empty">No appointments in this window yet.</p>
                            )}
                        </Panel>

                        <Panel title="Activity log" meta="Latest orders, visits & reviews" className="span-5 dash-terminal">
                            {data.activity.length ? (
                                <ul className="dash-log">
                                    {data.activity.map((event, i) => <ActivityLine key={i} event={event} />)}
                                </ul>
                            ) : (
                                <p className="dash-empty">No activity yet.</p>
                            )}
                        </Panel>
                    </div>
                </>
            )}
        </div>
    );
}

export default DashboardOverview;
