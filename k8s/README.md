# Kubernetes Deployment Guide

This directory contains Kubernetes manifests for the Irrigation Management System.

## Prerequisites

1. **Kubernetes cluster** running (Minikube, Docker Desktop, or cloud provider)
2. **Docker images** built and available in your cluster
3. **kubectl** installed and configured

## Deployment Steps

### 1. Create the namespace
```bash
kubectl apply -f namespace.yaml
```

### 2. Deploy MySQL database
```bash
kubectl apply -f mysql-configmap.yaml
kubectl apply -f mysql-pvc.yaml
kubectl apply -f mysql-deployment.yaml
kubectl apply -f mysql-service.yaml
```

Wait for MySQL to be ready:
```bash
kubectl wait --for=condition=ready pod -l app=mysql -n irrigation-system --timeout=300s
```

### 3. Deploy Eureka Server (Service Discovery)
```bash
kubectl apply -f eureka-deployment.yaml
kubectl apply -f eureka-service.yaml
```

Wait for Eureka to be ready:
```bash
kubectl wait --for=condition=ready pod -l app=mseureka -n irrigation-system --timeout=300s
```

### 4. Deploy Config Server
```bash
kubectl apply -f configserver-deployment.yaml
kubectl apply -f configserver-service.yaml
```

Wait for Config Server to be ready:
```bash
kubectl wait --for=condition=ready pod -l app=configserver -n irrigation-system --timeout=300s
```

### 5. Deploy Gateway
```bash
kubectl apply -f gateway-deployment.yaml
kubectl apply -f gateway-service.yaml
```

### 6. Deploy Business Services
```bash
kubectl apply -f energy-service-deployment.yaml
kubectl apply -f energy-service-service.yaml
kubectl apply -f eau-service-deployment.yaml
kubectl apply -f eau-service-service.yaml
```

### 7. Deploy Frontend
```bash
kubectl apply -f frontend-deployment.yaml
kubectl apply -f frontend-service.yaml
```

## Deploy All at Once

```bash
kubectl apply -f .
```

## Access the Application

After deployment, access the services:

- **Frontend**: http://localhost:30200
- **Eureka Dashboard**: http://localhost:30080
- **API Gateway**: http://localhost:30888

## Verify Deployment

Check all pods are running:
```bash
kubectl get pods -n irrigation-system
```

Check all services:
```bash
kubectl get services -n irrigation-system
```

View logs for a specific service:
```bash
kubectl logs -f deployment/energy-service -n irrigation-system
```

## Load Docker Images (for Minikube)

If using Minikube, load Docker images:
```bash
minikube image load dsspring-mseureka:latest
minikube image load dsspring-configserver:latest
minikube image load dsspring-gateway:latest
minikube image load dsspring-energy_service:latest
minikube image load dsspring-eau_service:latest
minikube image load dsspring-irrigation_frontend:latest
```

## Cleanup

Delete all resources:
```bash
kubectl delete namespace irrigation-system
```

Or delete individual resources:
```bash
kubectl delete -f .
```

## Troubleshooting

View pod status:
```bash
kubectl describe pod <pod-name> -n irrigation-system
```

View logs:
```bash
kubectl logs <pod-name> -n irrigation-system
```

Get events:
```bash
kubectl get events -n irrigation-system --sort-by='.lastTimestamp'
```

## Scaling

Scale a deployment:
```bash
kubectl scale deployment energy-service --replicas=3 -n irrigation-system
```
