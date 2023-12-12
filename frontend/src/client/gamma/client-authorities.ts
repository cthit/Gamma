import { AxiosInstance } from "axios";
import * as z from "zod";

const getAuthoritiesValidation = z.array(
  z
    .object({
      clientUid: z.string(),
      authorityName: z.string(),
      superGroups: z.array(
        z
          .object({
            id: z.string(),
            name: z.string(),
            prettyName: z.string(),
            svDescription: z.string(),
            enDescription: z.string(),
            type: z.string(),
            version: z.number(),
          })
          .strict(),
      ),
      users: z.array(z.any()),
      posts: z.array(z.any()),
    })
    .strict(),
);

export class ClientAuthorities {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getAuthorities(clientUid: string) {
    return getAuthoritiesValidation.parse(
      (await this.client.get("/admin/client/" + clientUid + "/authority")).data,
    );
  }

  public async createAuthority(clientUid: string, authorityName: string) {
    return this.client.post("/admin/client/authority", {
      clientUid,
      authorityName,
    });
  }

  public async addSuperGroupToAuthority(data: {
    clientUid: string;
    authorityName: string;
    superGroupId: string;
  }) {
    return this.client.post("/admin/client/authority/super-group", data);
  }
}
