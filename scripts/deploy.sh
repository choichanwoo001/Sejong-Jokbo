#!/bin/bash

# 스크립트 실행 중 에러 발생 시 중단
set -e

echo "🚀 Starting deployment..."

# Docker 이미지 Pull
echo "📥 Pulling latest images..."
docker-compose pull

# 컨테이너 재시작 (변경된 이미지만 적용)
echo "🔄 Restarting containers..."
docker-compose up -d

# 사용하지 않는 이미지 정리 (공간 확보)
echo "🧹 Pruning unused images..."
docker image prune -f

echo "✅ Deployment finished successfully!"
