# One-off database initializer. Runs the schema and seed scripts against RDS
# from inside the VPC, then exits. Built by scripts/4-init-database.sh.
FROM mysql:8.4

COPY production-schema.sql initial-data.sql /sql/
COPY db-init.sh /usr/local/bin/db-init.sh
RUN chmod +x /usr/local/bin/db-init.sh

ENTRYPOINT ["/usr/local/bin/db-init.sh"]
