import { AxiosInstance } from "axios";
import * as z from "zod";

const getApiTypesValidation = z.array(z.string());

const getApiKeysValidation = z.array(
  z
    .object({
      id: z.string(),
      svDescription: z.string(),
      enDescription: z.string(),
      keyType: z.string(),
      prettyName: z.string(),
    })
    .strict(),
);

const getApiKeyValidation = z
  .object({
    id: z.string(),
    svDescription: z.string(),
    enDescription: z.string(),
    keyType: z.string(),
    prettyName: z.string(),
  })
  .strict();

type CreateApiKey = {
  svDescription: string;
  enDescription: string;
  prettyName: string;
  keyType: string;
};

export class ApiKeys {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getApiKeys() {
    return getApiKeysValidation.parse(
      (await this.client.get("/admin/api-keys")).data,
    );
  }

  public async getApiTypes() {
    return getApiTypesValidation.parse(
      (await this.client.get("/admin/api-keys/types")).data,
    );
  }

  public async getApiKey(id: string) {
    return getApiKeyValidation.parse(
      (await this.client.get("/admin/api-keys/" + id)).data,
    );
  }

  public async deleteApiKey(id: string) {
    return this.client.delete("/admin/api-keys/" + id);
  }

  public async createApiKey(data: CreateApiKey) {
    return this.client.post("/admin/api-keys", data);
  }
}
