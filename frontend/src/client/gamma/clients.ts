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
      restriction: z
        .object({
          superGroups: z.array(
            z.object({
              id: z.string(),
              name: z.string(),
              prettyName: z.string(),
              svDescription: z.string(),
              enDescription: z.string(),
              type: z.string(),
              version: z.number(),
            }),
          ),
        })
        .strict()
        .nullable(),
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
    restriction: z
      .object({
        superGroups: z.array(
          z.object({
            id: z.string(),
            name: z.string(),
            prettyName: z.string(),
            svDescription: z.string(),
            enDescription: z.string(),
            type: z.string(),
            version: z.number(),
          }),
        ),
      })
      .strict()
      .nullable(),
  })
  .strict();

type CreateClient = {
  webServerRedirectUrl: string;
  svDescription: string;
  enDescription: string;
  generateApiKey: boolean;
  emailScope: boolean;
  restriction: {
    superGroupIds: string[];
  } | null;
};

export class Clients {
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

  public async createClient(data: CreateClient) {
    return this.client.post("/admin/client", data);
  }

  public async deleteClient(clientUid: string) {
    return this.client.delete("/admin/client/" + clientUid);
  }
}
