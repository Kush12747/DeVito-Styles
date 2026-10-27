"""Generate dashboard-demo-data.sql (next to this file):  python generate-dashboard-demo-data.py

Dates are written relative to CURDATE() so the script always produces a
fresh year of history no matter when it is run. Deterministic (seeded)."""
import random
import os

random.seed(2026)
out_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "dashboard-demo-data.sql")

DAYS_BACK = 365
DAYS_AHEAD = 14
SLOTS = ["09:00:00", "10:00:00", "11:00:00", "13:00:00", "15:00:00"]

# service_id -> (price, weight)   (service 1 "DeVito Style" is a test entry; skipped)
SERVICES = {2: (25.00, 35), 3: (35.00, 30), 4: (15.00, 15), 5: (45.00, 20)}
# barber_id -> (weight, last day they took bookings, relative to today)
BARBERS = {1: (1.3, DAYS_AHEAD), 2: (1.0, DAYS_AHEAD), 3: (0.7, -60), 4: (1.0, DAYS_AHEAD), 5: (0.9, DAYS_AHEAD)}
# product_id -> (name, price, weight)
PRODUCTS = {
    1: ("Matte Pomade", 19.99, 14), 2: ("Hair Clay", 21.99, 10), 3: ("Texture Powder", 16.99, 7),
    4: ("Daily Shampoo", 14.99, 9), 5: ("Daily Conditioner", 15.99, 6), 6: ("Tea Tree Shampoo", 16.99, 5),
    7: ("Beard Oil", 17.99, 12), 8: ("Beard Balm", 18.99, 8), 9: ("Beard Wash", 15.99, 6),
    10: ("Carbon Fiber Styling Comb", 9.99, 5), 11: ("Wooden Beard Comb", 12.99, 4),
    12: ("Vent Hair Brush", 18.99, 3), 13: ("Barber Neck Duster", 14.99, 2),
    14: ("Straight Razor Holder", 24.99, 2), 15: ("Barber Cape", 29.99, 2),
}
CUSTOMERS = [
    ("demo_marco", "Marco", "Bianchi"), ("demo_tyler", "Tyler", "Brooks"), ("demo_andre", "Andre", "Wallace"),
    ("demo_sam", "Sam", "Nguyen"), ("demo_jordan", "Jordan", "Reyes"), ("demo_eli", "Eli", "Carter"),
    ("demo_omar", "Omar", "Haddad"), ("demo_lucas", "Lucas", "Moreau"), ("demo_devon", "Devon", "Price"),
    ("demo_nico", "Nico", "Russo"), ("demo_kai", "Kai", "Tanaka"), ("demo_ryan", "Ryan", "Doyle"),
    ("demo_isaac", "Isaac", "Ford"), ("demo_mateo", "Mateo", "Silva"),
]
REVIEW_TEXT = {
    5: ["Best fade I've had in years.", "Sharp, clean and on time.", "Always leave looking fresh.",
        "Great conversation and a perfect cut.", "Worth every penny."],
    4: ["Solid cut, slight wait.", "Really happy with the beard line-up.", "Good experience overall."],
    3: ["Decent cut, ran a bit late.", "Fine, but not my usual style."],
    2: ["Not what I asked for this time."],
}

DOMAIN = "demo.devitostyles.com"
lines = []
w = lines.append

w("-- =====================================================================")
w("-- DASHBOARD DEMO DATA (generated)")
w("--")
w("-- Fills devitostyles with about a year of realistic history so the admin")
w("-- dashboard has something to show: demo customers, appointments, product")
w("-- orders, payments and reviews. Dates are relative to the day you run it.")
w("--")
w("-- Every row belongs to a customer whose email ends in @" + DOMAIN + ",")
w("-- so it can be removed again with sql/remove-dashboard-demo-data.sql.")
w("-- Running this script again first clears the previous demo data.")
w("-- Demo customers cannot log in (their password is not a valid hash).")
w("-- =====================================================================")
w("")
w("USE devitostyles;")
w("SET @now_check = NOW();")
w("")
w("-- Clear any previous demo data")
w("SOURCE_CLEANUP_PLACEHOLDER")
w("")
w("-- Demo customers")
for username, first, last in CUSTOMERS:
    phone = "312555" + str(random.randint(1000, 9999))
    w(f"INSERT INTO users (username, first_name, last_name, email, password, address, phone, role) VALUES "
      f"('{username}', '{first}', '{last}', '{username.replace('demo_', '')}@{DOMAIN}', 'DEMO-ACCOUNT-NO-LOGIN', "
      f"'{random.randint(10, 999)} Demo St', '{phone}', 'CUSTOMER');")
for i, (username, _, _) in enumerate(CUSTOMERS):
    w(f"SET @u{i} = (SELECT user_id FROM users WHERE username = '{username}');")
w("")


def ts(day, time):
    return f"TIMESTAMP(CURDATE() + INTERVAL {day} DAY, '{time}')"


def weighted(options):
    keys = list(options)
    return random.choices(keys, weights=[options[k] for k in keys])[0]


appointments = reviews = orders = 0
w("-- Appointments (and reviews for some completed visits)")
for day in range(-DAYS_BACK, DAYS_AHEAD + 1):
    # day-of-week from a fixed reference: we do not know the real weekday at
    # generation time, so the SQL skips Sundays itself with a WHERE clause.
    growth = 0.55 + 0.45 * (day + DAYS_BACK) / DAYS_BACK          # business grows over the year
    seasonal = 1.15 if (day % 365) in range(300, 340) else 1.0     # a busier stretch
    demand = 11 * growth * seasonal
    if day > 0:
        demand *= max(0.2, 1 - day / 16)                           # future fills up gradually
    count = max(0, min(len(SLOTS) * len(BARBERS), int(random.gauss(demand, 2))))
    taken = set()
    for _ in range(count):
        barbers = {b: wt for b, (wt, last_day) in BARBERS.items() if day <= last_day}
        b = weighted(barbers)
        slot = random.choices(SLOTS, weights=[1.1, 1.2, 1.0, 0.9, 1.3])[0]
        if (b, slot) in taken:
            continue
        taken.add((b, slot))
        s = weighted({k: v[1] for k, v in SERVICES.items()})
        u = random.randrange(len(CUSTOMERS))
        roll = random.random()
        if day < 0:
            status = "'CANCELLED'" if roll < 0.09 else ("'BOOKED'" if roll < 0.12 else "'COMPLETED'")
        else:
            status = (f"'CANCELLED'" if roll < 0.06 else
                      f"IF({ts(day, slot)} < NOW(), 'COMPLETED', 'BOOKED')")
        # INSERT ... SELECT with a WHERE so Sundays (closed) are skipped and a
        # slot already taken by a real booking is never double-booked.
        w(f"INSERT INTO appointment (user_id, barber_id, service_id, appointment_datetime, status) "
          f"SELECT @u{u}, {b}, {s}, {ts(day, slot)}, {status} FROM DUAL "
          f"WHERE DAYOFWEEK({ts(day, slot)}) <> 1 AND NOT EXISTS (SELECT 1 FROM appointment "
          f"WHERE barber_id = {b} AND appointment_datetime = {ts(day, slot)});")
        appointments += 1
        if day < -1 and status == "'COMPLETED'" and random.random() < 0.3:
            rating = random.choices([5, 4, 3, 2], weights=[62, 27, 8, 3])[0]
            text = random.choice(REVIEW_TEXT[rating]).replace("'", "''")
            w(f"INSERT INTO review (user_id, appointment_id, barber_id, rating, review_text, created_at) "
              f"SELECT @u{u}, LAST_INSERT_ID(), {b}, {rating}, '{text}', {ts(day + random.randint(0, 1), '19:30:00')} "
              f"FROM DUAL WHERE ROW_COUNT() > 0;")
            reviews += 1
w("")

w("-- Product orders, their items and payments")
for day in range(-DAYS_BACK, 1):
    growth = 0.5 + 0.5 * (day + DAYS_BACK) / DAYS_BACK
    for _ in range(max(0, int(random.gauss(2.2 * growth, 1.1)))):
        hour = random.randint(8, 21)
        if day == 0 and hour > 11:
            continue
        time = f"{hour:02d}:{random.randint(0, 59):02d}:{random.randint(0, 59):02d}"
        items = {}
        for _ in range(random.choices([1, 2, 3], weights=[55, 32, 13])[0]):
            p = weighted({k: v[2] for k, v in PRODUCTS.items()})
            items[p] = items.get(p, 0) + random.choices([1, 2], weights=[80, 20])[0]
        subtotal = round(sum(PRODUCTS[p][1] * q for p, q in items.items()), 2)
        tax = round(subtotal * 0.0625, 2)
        total = round(subtotal + tax, 2)
        roll = random.random()
        if day < -14:
            status = "Completed" if roll < 0.86 else ("Refunded" if roll < 0.9 else "Cancelled")
        else:
            status = "Paid" if roll < 0.6 else ("Processing" if roll < 0.82 else ("Pending" if roll < 0.95 else "Cancelled"))
        pay_status = {"Completed": "Succeeded", "Paid": "Succeeded", "Processing": "Succeeded",
                      "Refunded": "Refunded", "Pending": "Pending", "Cancelled": "Failed"}[status]
        u = random.randrange(len(CUSTOMERS))
        orders += 1
        number = f"DEMO-{orders:05d}"
        w(f"INSERT INTO orders (order_number, user_id, status, subtotal, tax_amount, shipping_cost, discount_amount, "
          f"total_amount, created_at, updated_at) VALUES ('{number}', @u{u}, '{status}', {subtotal:.2f}, {tax:.2f}, "
          f"0.00, 0.00, {total:.2f}, {ts(day, time)}, {ts(day, time)});")
        w("SET @o = LAST_INSERT_ID();")
        values = ", ".join(f"(@o, {p}, '{PRODUCTS[p][0]}', {q}, {PRODUCTS[p][1]:.2f}, {PRODUCTS[p][1] * q:.2f})"
                           for p, q in items.items())
        w(f"INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, line_total) VALUES {values};")
        paid_at = ts(day, time) if pay_status in ("Succeeded", "Refunded") else "NULL"
        w(f"INSERT INTO payments (order_id, payment_provider, payment_intent_id, payment_status, amount, currency, "
          f"paid_at, created_at) VALUES (@o, 'stripe', 'pi_demo_{orders:05d}', '{pay_status}', {total:.2f}, 'USD', "
          f"{paid_at}, {ts(day, time)});")
w("")

cleanup = open(out_path.replace("dashboard-demo-data.sql", "remove-dashboard-demo-data.sql"), encoding="utf-8").read()
body = cleanup.split("-- BEGIN CLEANUP", 1)[1].split("-- END CLEANUP", 1)[0].strip()
text = "\n".join(lines).replace("SOURCE_CLEANUP_PLACEHOLDER", body) + "\n"
text = text.replace("SET @now_check = NOW();\n", "")
with open(out_path, "w", encoding="utf-8", newline="\n") as f:
    f.write(text)
print(f"appointments={appointments} reviews={reviews} orders={orders} lines={len(text.splitlines())}")
