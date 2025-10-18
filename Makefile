.PHONY: build-image test-e2e e2e

build-image:
	@echo "Building Gamma Docker image..."
	./gradlew bootBuildImage
	docker image tag app:latest gamma-app:test

test-e2e:
ifdef GAMMA_VERSION
	@echo "Pulling Gamma Docker image from ghcr.io with version $(GAMMA_VERSION)..."
	docker pull ghcr.io/cthit/gamma:$(GAMMA_VERSION)
	docker image tag ghcr.io/cthit/gamma:$(GAMMA_VERSION) gamma-app:test
	@echo "Running e2e tests with Gamma version $(GAMMA_VERSION)..."
	cd e2e && pnpm test
else
	@echo "Running e2e tests..."
	cd e2e && pnpm test
endif

e2e:
ifdef GAMMA_VERSION
	$(MAKE) test-e2e
else
	$(MAKE) build-image test-e2e
endif
	@echo "E2E tests completed!"
