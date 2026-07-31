SHELL := /bin/bash

BACKEND_DIR := backend
FRONTEND_DIR := frontend
DATABASE_PATH := $(abspath data/planit_db)

.PHONY: install dev backend frontend test check build

install:
	npm --prefix $(FRONTEND_DIR) ci

dev:
	./scripts/dev.sh

backend:
	mkdir -p data
	cd $(BACKEND_DIR) && PLANIT_DATABASE_PATH=$(DATABASE_PATH) mvn spring-boot:run

frontend:
	npm --prefix $(FRONTEND_DIR) run dev

test:
	mvn -f $(BACKEND_DIR)/pom.xml test
	npm --prefix $(FRONTEND_DIR) test -- --runInBand

check:
	mvn -f $(BACKEND_DIR)/pom.xml verify
	npm --prefix $(FRONTEND_DIR) run lint
	npm --prefix $(FRONTEND_DIR) test -- --runInBand
	npm --prefix $(FRONTEND_DIR) run build

build:
	mvn -f $(BACKEND_DIR)/pom.xml package
	npm --prefix $(FRONTEND_DIR) run build
