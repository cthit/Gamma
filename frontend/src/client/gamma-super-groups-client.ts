import { AxiosInstance } from "axios";
import * as z from "zod";

const getSuperGroupsValidation = z.array(
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
);

const getSuperGroupValidation = z
  .object({
    id: z.string(),
    name: z.string(),
    prettyName: z.string(),
    svDescription: z.string(),
    enDescription: z.string(),
    type: z.string(),
    version: z.number(),
  })
  .strict();

export class GammaSuperGroupsClient {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getSuperGroups() {
    return getSuperGroupsValidation.parse(
      (await this.client.get("/superGroups")).data,
    );
  }

  public async getSuperGroup(id: string) {
    return getSuperGroupValidation.parse(
      (await this.client.get("/superGroups/" + id)).data,
    );
  }
}
