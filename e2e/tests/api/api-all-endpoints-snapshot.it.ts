import {
  expect,
  test,
  type APIRequestContext,
  type APIResponse,
} from "@playwright/test";
import { login } from "../../helpers/auth";
import { createUserClientWithApiKeyViaUi } from "../../helpers/client-api";
import { startMockGamma } from "../../helpers/gamma";
import { authorizeClientWithPkce } from "../../helpers/oauth";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
  type GammaBootstrapApiKeyType,
  type GammaEnvironment,
  type GammaInstance,
} from "../../gamma-setup";

const MSCOTT_ID = "88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f";
const UNKNOWN_USER_ID = "11111111-1111-1111-1111-111111111111";

type HttpMethod = "GET" | "POST";

interface ApiSnapshotCase {
  method: HttpMethod;
  path: string;
  headers?: Record<string, string>;
  body?: unknown;
  expected: ApiSnapshot;
}

interface ApiSnapshot {
  status: number;
  contentType: string | null;
  body: unknown;
}

const MSCOTT_USER = {
  acceptanceYear: 2005,
  cid: "mscott",
  firstName: "Michael",
  id: MSCOTT_ID,
  lastName: "Scott",
  nick: "Boss",
};

const CLIENT_SUPER_GROUPS = [
  {
    enDescription: "",
    id: "2157ee72-04cd-4029-8d57-77142d3ef5fa",
    name: "styrit",
    prettyName: "styrIT",
    svDescription: "",
    type: "board",
  },
  {
    enDescription: "",
    id: "30c2ee3b-b761-46d0-9029-215a9b484f7a",
    name: "emeritus",
    prettyName: "EmerITus",
    svDescription: "",
    type: "alumni",
  },
  {
    enDescription: "",
    id: "326807b4-ae68-4626-8382-919a15a8e23c",
    name: "sprit",
    prettyName: "S.P.R.I.T.",
    svDescription: "",
    type: "alumni",
  },
  {
    enDescription: "",
    id: "364a359a-f9eb-4d81-bb99-25cc5adf176d",
    name: "didit",
    prettyName: "didIT",
    svDescription: "",
    type: "alumni",
  },
  {
    enDescription: "",
    id: "5a427d4d-adb7-4de7-9c87-a569014c7b58",
    name: "dragit",
    prettyName: "DragIT",
    svDescription: "",
    type: "alumni",
  },
  {
    enDescription: "",
    id: "712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab",
    name: "kandidatmiddagen",
    prettyName: "Kandidatmiddagen",
    svDescription: "",
    type: "functionaries",
  },
  {
    enDescription: "",
    id: "aed27030-ad90-4526-855c-1e909b1dcecb",
    name: "digit",
    prettyName: "digIT",
    svDescription: "",
    type: "committee",
  },
  {
    enDescription: "",
    id: "b3bcbbcc-0b93-4c41-a3c7-1792448c6fc1",
    name: "prit",
    prettyName: "P.R.I.T.",
    svDescription: "",
    type: "committee",
  },
  {
    enDescription: "",
    id: "b8dbca3a-52e7-4299-9499-e58ec93a0c2c",
    name: "drawit",
    prettyName: "DrawIT",
    svDescription: "",
    type: "society",
  },
];

const CLIENT_GROUPS = [
  {
    id: "047ac437-a789-4cc5-bb6e-ba50efd7c509",
    name: "digit2025",
    prettyName: "digIT2025",
    superGroup: {
      enDescription: "",
      id: "364a359a-f9eb-4d81-bb99-25cc5adf176d",
      name: "didit",
      prettyName: "didIT",
      svDescription: "",
      type: "alumni",
    },
  },
  {
    id: "1ed91274-13c8-4d6d-ab75-37c9d732b51b",
    name: "prit2026",
    prettyName: "P.R.I.T.2026",
    superGroup: {
      enDescription: "",
      id: "b3bcbbcc-0b93-4c41-a3c7-1792448c6fc1",
      name: "prit",
      prettyName: "P.R.I.T.",
      svDescription: "",
      type: "committee",
    },
  },
  {
    id: "2abe2264-fd61-4899-ba46-851279d85229",
    name: "digit2026",
    prettyName: "digIT2026",
    superGroup: {
      enDescription: "",
      id: "aed27030-ad90-4526-855c-1e909b1dcecb",
      name: "digit",
      prettyName: "digIT",
      svDescription: "",
      type: "committee",
    },
  },
  {
    id: "5f26a10c-e668-4ec1-b072-a7dd8f11735c",
    name: "prit2025",
    prettyName: "P.R.I.T.2025",
    superGroup: {
      enDescription: "",
      id: "326807b4-ae68-4626-8382-919a15a8e23c",
      name: "sprit",
      prettyName: "S.P.R.I.T.",
      svDescription: "",
      type: "alumni",
    },
  },
  {
    id: "672db849-8afb-4160-9f12-7f8c1d379fcc",
    name: "drawit2025",
    prettyName: "DrawIT2025",
    superGroup: {
      enDescription: "",
      id: "5a427d4d-adb7-4de7-9c87-a569014c7b58",
      name: "dragit",
      prettyName: "DragIT",
      svDescription: "",
      type: "alumni",
    },
  },
  {
    id: "834651d1-34c1-4bac-b148-6546368a8454",
    name: "styrit2026",
    prettyName: "styrIT2026",
    superGroup: {
      enDescription: "",
      id: "2157ee72-04cd-4029-8d57-77142d3ef5fa",
      name: "styrit",
      prettyName: "styrIT",
      svDescription: "",
      type: "board",
    },
  },
  {
    id: "9b239de9-88a3-4992-96d1-b8dea2a637ec",
    name: "drawit2026",
    prettyName: "DrawIT2026",
    superGroup: {
      enDescription: "",
      id: "b8dbca3a-52e7-4299-9499-e58ec93a0c2c",
      name: "drawit",
      prettyName: "DrawIT",
      svDescription: "",
      type: "society",
    },
  },
  {
    id: "a2f06d3a-7432-4655-a778-69c9142912f1",
    name: "styrit2025",
    prettyName: "styrIT2025",
    superGroup: {
      enDescription: "",
      id: "30c2ee3b-b761-46d0-9029-215a9b484f7a",
      name: "emeritus",
      prettyName: "EmerITus",
      svDescription: "",
      type: "alumni",
    },
  },
  {
    id: "ee4153d5-830d-445f-acb3-ec09c53e7c0c",
    name: "kandidatmiddagen2026",
    prettyName: "Kandidatmiddagen2026",
    superGroup: {
      enDescription: "",
      id: "712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab",
      name: "kandidatmiddagen",
      prettyName: "Kandidatmiddagen",
      svDescription: "",
      type: "functionaries",
    },
  },
];

test.describe.serial("api snapshots", () => {
  let env: GammaEnvironment | undefined;
  let gamma: GammaInstance | undefined;
  let infoAuthHeader = "";
  let accountScaffoldAuthHeader = "";
  let allowListAuthHeader = "";
  let clientAuthHeader = "";

  test.beforeAll(async ({ browser }) => {
    env = await startDependencies();
    gamma = await startMockGamma(env);

    infoAuthHeader = makeAuthHeader(getApiKeyCredentials(gamma, "INFO"));
    accountScaffoldAuthHeader = makeAuthHeader(
      getApiKeyCredentials(gamma, "ACCOUNT_SCAFFOLD"),
    );
    allowListAuthHeader = makeAuthHeader(
      getApiKeyCredentials(gamma, "ALLOW_LIST"),
    );

    const context = await browser.newContext({ ignoreHTTPSErrors: true });
    const page = await context.newPage();
    await login(page, gamma.url, "mscott", "password1337", "Boss");
    const createdClient = await createUserClientWithApiKeyViaUi(
      page,
      gamma.url,
      "API Snapshot Client",
    );
    await authorizeClientWithPkce(
      page,
      gamma.url,
      createdClient.clientId,
      createdClient.redirectUri,
    );
    clientAuthHeader = makeAuthHeader({
      id: createdClient.apiKeyId,
      token: createdClient.apiKeyToken,
    });
    await context.close();
  });

  test.afterAll(async () => {
    if (gamma) {
      await stopGammaInstance(gamma);
    }
    if (env) {
      await stopDependencies(env);
    }
  });

  test("given info api key when requesting info v1 endpoints then responses match snapshots", async ({
    request,
  }) => {
    const target = requireGamma(gamma);
    const testCases: ApiSnapshotCase[] = [
      {
        method: "GET",
        path: "/api/info/v1/blob",
        headers: { Authorization: infoAuthHeader },
        expected: jsonSnapshot(200, []),
      },
      {
        method: "GET",
        path: `/api/info/v1/users/${MSCOTT_ID}`,
        headers: { Authorization: infoAuthHeader },
        expected: jsonSnapshot(200, {
          groups: [],
          user: MSCOTT_USER,
        }),
      },
      {
        method: "GET",
        path: `/api/info/v1/users/${UNKNOWN_USER_ID}`,
        headers: { Authorization: infoAuthHeader },
        expected: jsonSnapshot(404, {
          error: "Not Found",
          message: "USER_NOT_FOUND_RESPONSE",
          status: 404,
        }),
      },
    ];

    for (const testCase of testCases) {
      await assertApiSnapshot(request, target.url, testCase);
    }
  });

  test("given account scaffold api key when requesting account scaffold v1 endpoints then responses match snapshots", async ({
    request,
  }) => {
    const target = requireGamma(gamma);
    const testCases: ApiSnapshotCase[] = [
      {
        method: "GET",
        path: "/api/account-scaffold/v1/supergroups",
        headers: { Authorization: accountScaffoldAuthHeader },
        expected: jsonSnapshot(200, []),
      },
      {
        method: "GET",
        path: "/api/account-scaffold/v1/users",
        headers: { Authorization: accountScaffoldAuthHeader },
        expected: jsonSnapshot(200, []),
      },
    ];

    for (const testCase of testCases) {
      await assertApiSnapshot(request, target.url, testCase);
    }
  });

  test("given allow list api route when requesting allow list v1 endpoints then responses match snapshots", async ({
    request,
  }) => {
    const target = requireGamma(gamma);
    const testCases: ApiSnapshotCase[] = [
      {
        method: "GET",
        path: "/api/allow-list/v1",
        expected: {
          body: null,
          contentType: null,
          status: 401,
        },
      },
      {
        method: "POST",
        path: "/api/allow-list/v1",
        headers: { Authorization: allowListAuthHeader },
        body: { cids: ["snapcid", "snapcid"] },
        expected: jsonSnapshot(206, ["snapcid"]),
      },
    ];

    for (const testCase of testCases) {
      await assertApiSnapshot(request, target.url, testCase);
    }
  });

  test("given client api key when requesting client v1 endpoints then responses match snapshots", async ({
    request,
  }) => {
    const target = requireGamma(gamma);
    const testCases: ApiSnapshotCase[] = [
      {
        method: "GET",
        path: "/api/client/v1/groups",
        headers: { Authorization: clientAuthHeader },
        expected: jsonSnapshot(200, CLIENT_GROUPS),
      },
      {
        method: "GET",
        path: "/api/client/v1/superGroups",
        headers: { Authorization: clientAuthHeader },
        expected: jsonSnapshot(200, CLIENT_SUPER_GROUPS),
      },
      {
        method: "GET",
        path: "/api/client/v1/users",
        headers: { Authorization: clientAuthHeader },
        expected: jsonSnapshot(200, [MSCOTT_USER]),
      },
      {
        method: "GET",
        path: `/api/client/v1/users/${MSCOTT_ID}`,
        headers: { Authorization: clientAuthHeader },
        expected: jsonSnapshot(200, MSCOTT_USER),
      },
      {
        method: "GET",
        path: `/api/client/v1/users/${UNKNOWN_USER_ID}`,
        headers: { Authorization: clientAuthHeader },
        expected: jsonSnapshot(404, {
          error: "Not Found",
          message: "User Not Found Or Unauthorized",
          status: 404,
        }),
      },
      {
        method: "GET",
        path: `/api/client/v1/groups/for/${MSCOTT_ID}`,
        headers: { Authorization: clientAuthHeader },
        expected: jsonSnapshot(200, []),
      },
      {
        method: "GET",
        path: `/api/client/v1/groups/for/${UNKNOWN_USER_ID}`,
        headers: { Authorization: clientAuthHeader },
        expected: jsonSnapshot(404, {
          error: "Not Found",
          message: "User Not Found Or Unauthorized",
          status: 404,
        }),
      },
      {
        method: "GET",
        path: "/api/client/v1/authorities",
        headers: { Authorization: clientAuthHeader },
        expected: jsonSnapshot(200, []),
      },
      {
        method: "GET",
        path: `/api/client/v1/authorities/for/${MSCOTT_ID}`,
        headers: { Authorization: clientAuthHeader },
        expected: jsonSnapshot(200, []),
      },
    ];

    for (const testCase of testCases) {
      await assertApiSnapshot(request, target.url, testCase);
    }
  });
});

function requireGamma(gamma: GammaInstance | undefined): GammaInstance {
  if (!gamma) {
    throw new Error("Gamma instance was not started");
  }
  return gamma;
}

function getApiKeyCredentials(
  gamma: GammaInstance,
  type: GammaBootstrapApiKeyType,
): { id: string; token: string } {
  const credentials = gamma.apiKeys?.[type];
  if (!credentials) {
    throw new Error(`Bootstrap ${type} api key credentials were not captured`);
  }
  return credentials;
}

function makeAuthHeader(credentials: { id: string; token: string }): string {
  return `pre-shared ${credentials.id}:${credentials.token}`;
}

function jsonSnapshot(status: number, body: unknown): ApiSnapshot {
  return {
    body,
    contentType: "application/json",
    status,
  };
}

async function assertApiSnapshot(
  request: APIRequestContext,
  baseUrl: string,
  testCase: ApiSnapshotCase,
): Promise<void> {
  const url = `${baseUrl}${testCase.path}`;
  const headers = {
    Accept: "application/json",
    ...(testCase.headers ?? {}),
  };
  const response =
    testCase.method === "GET"
      ? await request.get(url, { headers })
      : await request.post(url, {
          headers,
          ...(testCase.body !== undefined ? { data: testCase.body } : {}),
        });

  const snapshot = await toApiSnapshot(response);
  expect(snapshot.contentType).not.toBe("text/html");
  expect(snapshot).toEqual(testCase.expected);
}

async function toApiSnapshot(response: APIResponse): Promise<ApiSnapshot> {
  const contentTypeRaw = response.headers()["content-type"] ?? null;
  const contentType = contentTypeRaw?.split(";")[0] ?? null;
  const bodyRaw = await response.text();

  let body: unknown = null;
  if (bodyRaw.length > 0) {
    if (
      contentType === "application/json" ||
      (contentType !== null && contentType.endsWith("+json"))
    ) {
      body = normalizeSnapshotValue(JSON.parse(bodyRaw) as unknown);
    } else {
      body = bodyRaw;
    }
  }

  return {
    status: response.status(),
    contentType,
    body,
  };
}

function normalizeSnapshotValue(value: unknown): unknown {
  if (Array.isArray(value)) {
    const normalizedItems = value.map(normalizeSnapshotValue);
    if (normalizedItems.every((item) => isPlainObject(item))) {
      return [...normalizedItems].sort((a, b) =>
        JSON.stringify(a).localeCompare(JSON.stringify(b)),
      );
    }
    return normalizedItems;
  }

  if (!isPlainObject(value)) {
    return value;
  }

  const entries = Object.entries(value).sort(([left], [right]) =>
    left.localeCompare(right),
  );
  const normalizedObject: Record<string, unknown> = {};
  for (const [key, entryValue] of entries) {
    if (key === "timestamp") {
      normalizedObject[key] = "<timestamp>";
      continue;
    }
    normalizedObject[key] = normalizeSnapshotValue(entryValue);
  }
  return normalizedObject;
}

function isPlainObject(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}
