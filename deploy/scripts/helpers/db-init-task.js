// Writes the ECS task definition JSON for the one-off database initializer.
// Usage: node db-init-task.js <output-file>   (values come from environment)
const fs = require("fs");

const env = (name) => {
  const value = process.env[name];
  if (!value) throw new Error(`${name} is required`);
  return value;
};

const taskDefinition = {
  family: `${env("PROJECT_NAME")}-db-init`,
  requiresCompatibilities: ["FARGATE"],
  networkMode: "awsvpc",
  cpu: "256",
  memory: "512",
  executionRoleArn: env("EXECUTION_ROLE_ARN"),
  runtimePlatform: { cpuArchitecture: "X86_64", operatingSystemFamily: "LINUX" },
  containerDefinitions: [
    {
      name: "db-init",
      image: env("DB_INIT_IMAGE"),
      essential: true,
      environment: [
        { name: "DB_HOST", value: env("DB_HOST") },
        { name: "DB_USER", value: "devito_admin" },
      ],
      secrets: [
        { name: "DB_PASSWORD", valueFrom: `${env("DB_SECRET_ARN")}:password::` },
      ],
      logConfiguration: {
        logDriver: "awslogs",
        options: {
          "awslogs-group": env("LOG_GROUP"),
          "awslogs-region": env("AWS_REGION"),
          "awslogs-stream-prefix": "db-init",
        },
      },
    },
  ],
};

fs.writeFileSync(process.argv[2], JSON.stringify(taskDefinition, null, 2));
