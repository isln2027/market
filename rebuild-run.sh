docker build -f market-app/Dockerfile -t market-app .
docker build -f payment-service/Dockerfile -t payment-service .
docker compose up -d