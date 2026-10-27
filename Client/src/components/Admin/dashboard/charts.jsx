import { useEffect, useRef, useState } from "react";
import { bucketLabel, bucketTitle, formatCount, hourLabel, niceTicks } from "./format";

/*
 * Small hand-rolled SVG charts for the admin dashboard.
 *
 * Specs: bars <= 24px with a 4px rounded data-end and a 2px gap between
 * stacked segments, 2px lines, >= 8px end dots with a surface ring,
 * hairline gridlines, text always in text colors (never the series color),
 * and a hover tooltip on every chart.
 */

const AXIS_FONT = 11;

// Track an element's width so the SVG can be drawn at real pixel size
function useWidth() {
    const ref = useRef(null);
    const [width, setWidth] = useState(0);

    useEffect(() => {
        if (!ref.current) return;
        const observer = new ResizeObserver(([entry]) => setWidth(entry.contentRect.width));
        observer.observe(ref.current);
        return () => observer.disconnect();
    }, []);

    return [ref, width];
}

// Rect with only the top corners rounded (data-end up, square at the baseline)
function topRoundedRect(x, y, w, h, r) {
    if (h <= 0) return "";
    const radius = Math.min(r, h, w / 2);
    return `M${x},${y + h} V${y + radius} Q${x},${y} ${x + radius},${y} H${x + w - radius} Q${x + w},${y} ${x + w},${y + radius} V${y + h} Z`;
}

function Tooltip({ tip }) {
    if (!tip) return null;
    return (
        <div
            className="dash-tooltip"
            style={{ left: tip.x, top: tip.y, transform: tip.flip ? "translate(calc(-100% - 14px), -50%)" : "translate(14px, -50%)" }}
            role="status"
        >
            <div className="dash-tooltip-title">{tip.title}</div>
            {tip.rows.map(row => (
                <div className="dash-tooltip-row" key={row.label}>
                    {row.color && <span className="dash-swatch" style={{ background: row.color }} />}
                    <span className="dash-tooltip-label">{row.label}</span>
                    <span className="dash-tooltip-value">{row.value}</span>
                </div>
            ))}
        </div>
    );
}

export function Legend({ series }) {
    return (
        <ul className="dash-legend">
            {series.map(s => (
                <li key={s.key}>
                    <span className={`dash-swatch ${s.line ? "line" : ""}`} style={{ background: s.color }} />
                    {s.label}
                </li>
            ))}
        </ul>
    );
}

// Shared frame: y gridlines + ticks, x labels thinned so they never collide
function Axes({ width, height, pad, ticks, yScale, formatTick, labels }) {
    const innerW = width - pad.left - pad.right;
    const band = innerW / labels.length;
    const every = Math.max(1, Math.ceil(54 / band));

    return (
        <g className="dash-axes">
            {ticks.map(t => (
                <g key={t}>
                    <line x1={pad.left} x2={width - pad.right} y1={yScale(t)} y2={yScale(t)}
                          className={t === 0 ? "dash-baseline" : "dash-gridline"} />
                    <text x={pad.left - 8} y={yScale(t)} dy="0.32em" textAnchor="end" fontSize={AXIS_FONT}>
                        {formatTick(t)}
                    </text>
                </g>
            ))}
            {labels.map((label, i) => (
                (labels.length - 1 - i) % every === 0 && (
                    <text key={i} x={pad.left + band * i + band / 2} y={height - 8}
                          textAnchor="middle" fontSize={AXIS_FONT}>
                        {label}
                    </text>
                )
            ))}
        </g>
    );
}

/*
 * Stacked columns over time (e.g. service + product revenue).
 * series: [{ key, label, color }], bottom segment first.
 */
export function StackedColumns({ points, range, series, formatValue, formatTick, height = 260 }) {
    const [ref, width] = useWidth();
    const [hover, setHover] = useState(null);

    const pad = { top: 26, right: 8, bottom: 30, left: 52 };
    const totals = points.map(p => series.reduce((sum, s) => sum + Number(p[s.key]), 0));
    const ticks = niceTicks(Math.max(...totals, 0));
    const max = ticks[ticks.length - 1];
    const innerW = Math.max(0, width - pad.left - pad.right);
    const innerH = height - pad.top - pad.bottom;
    const band = innerW / Math.max(points.length, 1);
    const barW = Math.min(24, band * 0.62);
    const yScale = v => pad.top + innerH - (v / max) * innerH;
    const last = points.length - 1;

    const tip = hover === null ? null : {
        x: pad.left + band * hover + band / 2,
        y: pad.top + innerH / 2,
        flip: hover > points.length / 2,
        title: bucketTitle(points[hover].start, range) + (hover === last ? " · so far" : ""),
        rows: [
            ...[...series].reverse().map(s => ({ label: s.label, color: s.color, value: formatValue(points[hover][s.key]) })),
            { label: "Total", value: formatValue(totals[hover]) }
        ]
    };

    return (
        <div className="dash-chart" ref={ref} onMouseLeave={() => setHover(null)}>
            {width > 0 && (
                <svg width={width} height={height} role="img"
                     aria-label={`${series.map(s => s.label).join(" and ")} by ${range}`}>
                    <Axes width={width} height={height} pad={pad} ticks={ticks} yScale={yScale}
                          formatTick={formatTick} labels={points.map(p => bucketLabel(p.start, range))} />

                    {points.map((p, i) => {
                        const x = pad.left + band * i + (band - barW) / 2;
                        let base = 0;
                        const segments = series.map((s, si) => {
                            const value = Number(p[s.key]);
                            const y0 = yScale(base);
                            base += value;
                            const y1 = yScale(base);
                            // 2px surface gap above every segment that has one stacked on it
                            const gap = si > 0 && value > 0 ? 2 : 0;
                            const h = Math.max(0, y0 - y1 - gap);
                            const isTop = series.slice(si + 1).every(next => Number(p[next.key]) === 0);
                            return value > 0 && (
                                isTop
                                    ? <path key={s.key} d={topRoundedRect(x, y1, barW, h, 4)} fill={s.color} />
                                    : <rect key={s.key} x={x} y={y1} width={barW} height={h} fill={s.color} />
                            );
                        });

                        return (
                            <g key={p.start} className={hover !== null && hover !== i ? "dash-dim" : ""}>
                                {segments}
                            </g>
                        );
                    })}

                    {/* Direct label on the latest bucket only */}
                    {last >= 0 && totals[last] > 0 && (
                        <text className="dash-direct-label" x={pad.left + band * last + band / 2}
                              y={yScale(totals[last]) - 8} textAnchor="middle" fontSize={AXIS_FONT}>
                            {formatTick(totals[last])}
                        </text>
                    )}

                    {/* Hover targets: the whole band, not just the bar */}
                    {points.map((p, i) => (
                        <rect key={`hit-${p.start}`} x={pad.left + band * i} y={pad.top} width={band} height={innerH}
                              fill="transparent" onMouseEnter={() => setHover(i)} />
                    ))}
                </svg>
            )}
            <Tooltip tip={tip} />
        </div>
    );
}

/*
 * Lines over time with a crosshair (e.g. appointments vs cancellations).
 * The first series gets a soft area wash.
 */
export function TrendLines({ points, range, series, height = 220 }) {
    const [ref, width] = useWidth();
    const [hover, setHover] = useState(null);

    const pad = { top: 18, right: 14, bottom: 30, left: 40 };
    const ticks = niceTicks(Math.max(...points.flatMap(p => series.map(s => Number(p[s.key]))), 0));
    const max = ticks[ticks.length - 1];
    const innerW = Math.max(0, width - pad.left - pad.right);
    const innerH = height - pad.top - pad.bottom;
    const band = innerW / Math.max(points.length, 1);
    const xAt = i => pad.left + band * i + band / 2;
    const yScale = v => pad.top + innerH - (v / max) * innerH;
    const last = points.length - 1;

    const tip = hover === null ? null : {
        x: xAt(hover),
        y: pad.top + innerH / 2,
        flip: hover > points.length / 2,
        title: bucketTitle(points[hover].start, range) + (hover === last ? " · so far" : ""),
        rows: series.map(s => ({ label: s.label, color: s.color, value: formatCount(points[hover][s.key]) }))
    };

    return (
        <div className="dash-chart" ref={ref} onMouseLeave={() => setHover(null)}>
            {width > 0 && (
                <svg width={width} height={height} role="img"
                     aria-label={`${series.map(s => s.label).join(" and ")} by ${range}`}>
                    <Axes width={width} height={height} pad={pad} ticks={ticks} yScale={yScale}
                          formatTick={formatCount} labels={points.map(p => bucketLabel(p.start, range))} />

                    {series.map((s, si) => {
                        const coords = points.map((p, i) => [xAt(i), yScale(Number(p[s.key]))]);
                        const line = coords.map(([x, y], i) => `${i ? "L" : "M"}${x},${y}`).join(" ");
                        return (
                            <g key={s.key}>
                                {si === 0 && coords.length > 1 && (
                                    <path d={`${line} L${coords[last][0]},${yScale(0)} L${coords[0][0]},${yScale(0)} Z`}
                                          fill={s.color} opacity="0.1" />
                                )}
                                <path d={line} fill="none" stroke={s.color} strokeWidth="2"
                                      strokeLinejoin="round" strokeLinecap="round" />
                            </g>
                        );
                    })}

                    {hover !== null && (
                        <line className="dash-crosshair" x1={xAt(hover)} x2={xAt(hover)} y1={pad.top} y2={pad.top + innerH} />
                    )}

                    {/* End dots (or hovered dots), ringed in the surface color */}
                    {series.map(s => {
                        const i = hover ?? last;
                        return i >= 0 && (
                            <circle key={s.key} cx={xAt(i)} cy={yScale(Number(points[i][s.key]))} r="4.5"
                                    fill={s.color} className="dash-dot" />
                        );
                    })}

                    {points.map((p, i) => (
                        <rect key={`hit-${p.start}`} x={pad.left + band * i} y={pad.top} width={band} height={innerH}
                              fill="transparent" onMouseEnter={() => setHover(i)} />
                    ))}
                </svg>
            )}
            <Tooltip tip={tip} />
        </div>
    );
}

/*
 * Ranked horizontal bars (single series). value drives the bar,
 * detail is extra text shown at the tip and in the tooltip.
 */
export function RankedBars({ items, color, value, formatValue, detail, emptyText }) {
    const [hover, setHover] = useState(null);

    if (!items.length) {
        return <p className="dash-empty">{emptyText}</p>;
    }

    const max = Math.max(...items.map(value), 1);

    return (
        <ol className="dash-ranked">
            {items.map((item, i) => (
                <li key={item.id ?? item.name}
                    className={hover !== null && hover !== i ? "dash-dim" : ""}
                    onMouseEnter={() => setHover(i)}
                    onMouseLeave={() => setHover(null)}>
                    <div className="dash-ranked-head">
                        <span className="dash-ranked-name">
                            <span className="dash-rank">{String(i + 1).padStart(2, "0")}</span>
                            {item.name}
                        </span>
                        <span className="dash-ranked-value">
                            {formatValue(value(item))}
                            {detail && <span className="dash-ranked-detail"> · {detail(item)}</span>}
                        </span>
                    </div>
                    <div className="dash-ranked-track">
                        <div className="dash-ranked-bar" style={{ width: `${(value(item) / max) * 100}%`, background: color }} />
                    </div>
                </li>
            ))}
        </ol>
    );
}

/*
 * Busy times: weekday x hour grid on a single-hue (green) sequential scale.
 * Empty cells stay at the surface; more bookings = brighter.
 */
const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

export function BusyHeatmap({ cells }) {
    const [hover, setHover] = useState(null);

    if (!cells.length) {
        return <p className="dash-empty">No appointments in this window yet.</p>;
    }

    const counts = new Map(cells.map(c => [`${c.dayOfWeek}-${c.hour}`, c.count]));
    const hours = [...new Set(cells.map(c => c.hour))].sort((a, b) => a - b);
    // Hide Sunday (closed) unless something was actually booked on one
    const days = DAYS.map((name, i) => ({ name, dow: i + 1 }))
        .filter(d => d.dow !== 7 || cells.some(c => c.dayOfWeek === 7));
    const max = Math.max(...cells.map(c => c.count));
    const total = cells.reduce((sum, c) => sum + c.count, 0);

    return (
        <div className="dash-heatmap-wrap">
            <div className="dash-heatmap" style={{ gridTemplateColumns: `2.6rem repeat(${hours.length}, 1fr)` }}
                 onMouseLeave={() => setHover(null)}>
                <span />
                {hours.map(h => <span key={h} className="dash-heat-hour">{hourLabel(h)}</span>)}

                {days.map(d => (
                    <div key={d.name} className="dash-heat-row" style={{ display: "contents" }}>
                        <span className="dash-heat-day">{d.name}</span>
                        {hours.map(h => {
                            const count = counts.get(`${d.dow}-${h}`) ?? 0;
                            const t = count / max;
                            const key = `${d.dow}-${h}`;
                            return (
                                <span
                                    key={key}
                                    className={`dash-heat-cell ${hover === key ? "is-hover" : ""}`}
                                    style={{ "--t": `${Math.round(t * 100)}%` }}
                                    onMouseEnter={() => setHover(key)}
                                    aria-label={`${d.name} ${hourLabel(h)}: ${count} appointments`}
                                />
                            );
                        })}
                    </div>
                ))}
            </div>

            <div className="dash-heat-footer">
                <span className="dash-heat-readout">
                    {hover
                        ? (() => {
                            const [dow, h] = hover.split("-").map(Number);
                            const count = counts.get(hover) ?? 0;
                            return `${DAYS[dow - 1]} ${hourLabel(h)} → ${count} appt${count === 1 ? "" : "s"} (${total ? Math.round((count / total) * 100) : 0}%)`;
                        })()
                        : "hover a cell for detail"}
                </span>
                <span className="dash-heat-scale" aria-hidden="true">
                    fewer
                    {[0, 25, 50, 75, 100].map(t => (
                        <span key={t} className="dash-heat-cell" style={{ "--t": `${t}%` }} />
                    ))}
                    more
                </span>
            </div>
        </div>
    );
}

/*
 * Appointment outcomes as one stacked meter. Status colors always ship
 * with a label and count, never color alone.
 */
export function StatusMeter({ mix }) {
    const parts = [
        { key: "completed", label: "Completed", icon: "✓", className: "good" },
        { key: "booked", label: "Booked", icon: "◷", className: "neutral" },
        { key: "cancelled", label: "Cancelled", icon: "✕", className: "critical" }
    ];
    const total = parts.reduce((sum, p) => sum + mix[p.key], 0);

    if (!total) {
        return <p className="dash-empty">No appointments in this window yet.</p>;
    }

    return (
        <div className="dash-status">
            <div className="dash-status-bar" role="img"
                 aria-label={parts.map(p => `${p.label} ${mix[p.key]}`).join(", ")}>
                {parts.filter(p => mix[p.key] > 0).map(p => (
                    <span key={p.key} className={`dash-status-seg ${p.className}`}
                          style={{ flexGrow: mix[p.key] }}
                          title={`${p.label}: ${formatCount(mix[p.key])}`} />
                ))}
            </div>
            <ul className="dash-status-legend">
                {parts.map(p => (
                    <li key={p.key}>
                        <span className={`dash-status-icon ${p.className}`} aria-hidden="true">{p.icon}</span>
                        <span className="dash-status-label">{p.label}</span>
                        <span className="dash-status-count">{formatCount(mix[p.key])}</span>
                        <span className="dash-status-pct">{Math.round((mix[p.key] / total) * 100)}%</span>
                    </li>
                ))}
            </ul>
        </div>
    );
}
