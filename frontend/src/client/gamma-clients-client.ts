import { AxiosInstance } from "axios";
import * as z from "zod";

const getClientsValidation = z.array(
  z
    .object({
      clientId: z.string(),
      clientUid: z.string(),
      svDescription: z.string(),
      enDescription: z.string(),
      hasApiKey: z.boolean(),
      prettyName: z.string(),
      webServerRedirectUrl: z.string(),
    })
    .strict(),
);

const getClientValidation = z
  .object({
    clientId: z.string(),
    clientUid: z.string(),
    svDescription: z.string(),
    enDescription: z.string(),
    hasApiKey: z.boolean(),
    prettyName: z.string(),
    webServerRedirectUrl: z.string(),
  })
  .strict();

export class GammaClientsClient {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getClients() {
    return getClientsValidation.parse(
      (await this.client.get("/admin/client")).data,
    );
  }

  public async getClient(id: string) {
    return getClientValidation.parse(
      (await this.client.get("/admin/client/" + id)).data,
    );
  }
}
