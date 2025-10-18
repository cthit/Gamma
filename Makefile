.PHONY: build-image test-e2e e2e

build-image:
	@echo "Building Gamma Docker image..."
	./gradlew bootBuildImage
	docker image tag app:latest gamma-app:test

test-e2e:
	@echo "Running e2e tests on chromium, firefox, and webkit..."
	cd e2e && pnpm test

e2e: build-image test-e2e
	@echo "E2E tests completed!"
