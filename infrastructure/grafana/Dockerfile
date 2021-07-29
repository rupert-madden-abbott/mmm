FROM grafana/grafana:8.0.6

COPY grafana.ini /etc/grafana/grafana.ini
COPY datasource.yaml /etc/grafana/provisioning/datasources/datasource.yaml
COPY dashboards/dashboards.yaml /etc/grafana/provisioning/dashboards/dashboards.yaml
COPY dashboards/game.json /var/lib/grafana/dashboards/game.json