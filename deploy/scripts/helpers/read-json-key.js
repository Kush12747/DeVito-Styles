// Prints one key from a JSON object read on stdin, or nothing if it's missing.
// Usage: echo '{"A":"1"}' | node read-json-key.js A
let input = "";
process.stdin.on("data", (chunk) => (input += chunk));
process.stdin.on("end", () => {
  try {
    const value = JSON.parse(input)[process.argv[2]];
    if (typeof value === "string") process.stdout.write(value);
  } catch {
    // Not JSON (e.g. the secret doesn't exist yet): print nothing
  }
});
