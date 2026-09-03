.PHONY: build-image test-e2e e2e dev dev-down

build-image:
	@./gradlew bootBuildImage
	@docker image tag app:latest gamma-app:test

test-e2e:
	@cd e2e && pnpm test

e2e:
	@cd e2e && GAMMA_IMAGE="$${GAMMA_IMAGE:-gamma-app:test}" pnpm test
	@echo "E2E tests completed!"

dev: build-image
	@cd e2e && pnpm install --frozen-lockfile && pnpm dev

dev-down:
	@cd e2e && pnpm dev:down
