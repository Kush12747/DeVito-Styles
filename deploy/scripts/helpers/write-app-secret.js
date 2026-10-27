// Writes the app secret JSON from environment variables (loaded from config.env).
// Usage: node write-app-secret.js <output-file>
const fs = require("fs");

const keys = [
  "MAIL_HOST",
  "MAIL_PORT",
  "MAIL_USERNAME",
  "MAIL_PASSWORD",
  "CLOUD_NAME",
  "API_KEY",
  "API_SECRET",
  "STRIPE_SECRET_KEY",
  "STRIPE_WEBHOOK_SECRET",
  "JWT_SECRET",
  "ANTHROPIC_API_KEY",
];

const secret = Object.fromEntries(keys.map((key) => [key, process.env[key] ?? ""]));
fs.writeFileSync(process.argv[2], JSON.stringify(secret), { mode: 0o600 });
