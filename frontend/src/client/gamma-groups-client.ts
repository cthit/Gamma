import { AxiosInstance } from "axios";
import * as z from "zod";

const getGroupsValidation = z.array(
  z
    .object({
      id: z.string(),
      name: z.string(),
      prettyName: z.string(),
      superGroup: z
        .object({
          id: z.string(),
          version: z.number(),
          type: z.string(),
          name: z.string(),
          prettyName: z.string(),
          svDescription: z.string(),
          enDescription: z.string(),
        })
        .strict(),
    })
    .strict(),
);

const getGroupValidation = z
  .object({
    id: z.string(),
    version: z.number(),
    name: z.string(),
    prettyName: z.string(),
    groupMembers: z.array(
      z
        .object({
          unofficialPostName: z.string(),
          user: z
            .object({
              id: z.string(),
              firstName: z.string(),
              lastName: z.string(),
              nick: z.string(),
              cid: z.string(),
              acceptanceYear: z.number(),
            })
            .strict(),
          post: z
            .object({
              id: z.string(),
              svName: z.string(),
              enName: z.string(),
              emailPrefix: z.string(),
              version: z.number(),
            })
            .strict(),
        })
        .strict(),
    ),
    superGroup: z
      .object({
        id: z.string(),
        version: z.number(),
        type: z.string(),
        name: z.string(),
        prettyName: z.string(),
        svDescription: z.string(),
        enDescription: z.string(),
      })
      .strict(),
  })
  .strict();

export class GammaGroupsClient {
  private client: AxiosInstance;

  constructor(client: AxiosInstance) {
    this.client = client;
  }

  public async getGroups() {
    return getGroupsValidation.parse((await this.client.get("/groups")).data);
  }

  public async getGroup(id: string) {
    return getGroupValidation.parse(
      (await this.client.get("/groups/" + id)).data,
    );
  }
}
